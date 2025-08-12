package com.jodexindustries.donatecase.common.scheduler;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.scheduler.Scheduler;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class BackendScheduler implements Scheduler {

    private final ScheduledExecutorService syncExecutor = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors(),
            new ThreadFactory() {
                private final AtomicInteger counter = new AtomicInteger(0);

                @Override
                public Thread newThread(@NotNull Runnable r) {
                    Thread thread = new Thread(r, "DonateCase-Sync-Thread-" + counter.incrementAndGet());
                    thread.setDaemon(true);
                    return thread;
                }
            }
    );

    private final ForkJoinPool asyncExecutor = new ForkJoinPool(
            Runtime.getRuntime().availableProcessors(),
            ForkJoinPool.defaultForkJoinWorkerThreadFactory,
            null,
            true
    );

    private final AtomicInteger taskCounter = new AtomicInteger(0);
    protected final ConcurrentMap<Integer, SchedulerTask> tasks = new ConcurrentHashMap<>();

    protected void add(SchedulerTask task) {
        tasks.put(task.getTaskId(), task);
    }

    protected void remove(int taskId) {
        SchedulerTask task = tasks.remove(taskId);
        if (task != null) task.cancel();
    }

    @Override
    public SchedulerTask run(Addon addon, Runnable task) {
        return schedule(addon, task, 0, -1, false);
    }

    @Override
    public SchedulerTask run(Addon addon, Runnable task, long delay) {
        return schedule(addon, task, delay, -1, false);
    }

    @Override
    public SchedulerTask run(Addon addon, Runnable task, long delay, long period) {
        return schedule(addon, task, delay, period, false);
    }

    @Override
    public void run(Addon addon, Consumer<SchedulerTask> consumer) {
        scheduleConsumer(addon, consumer, 0, -1, false);
    }

    @Override
    public void run(Addon addon, Consumer<SchedulerTask> consumer, long delay) {
        scheduleConsumer(addon, consumer, delay, -1, false);
    }

    @Override
    public void run(Addon addon, Consumer<SchedulerTask> consumer, long delay, long period) {
        scheduleConsumer(addon, consumer, delay, period, false);
    }

    @Override
    public SchedulerTask async(Addon addon, Runnable task, long delay) {
        return schedule(addon, task, delay, -1, true);
    }

    @Override
    public SchedulerTask async(Addon addon, Runnable task, long delay, long period) {
        return schedule(addon, task, delay, period, true);
    }

    @Override
    public void async(Addon addon, Consumer<SchedulerTask> consumer, long delay) {
        scheduleConsumer(addon, consumer, delay, -1, true);
    }

    @Override
    public void async(Addon addon, Consumer<SchedulerTask> consumer, long delay, long period) {
        scheduleConsumer(addon, consumer, delay, period, true);
    }

    private SchedulerTask schedule(Addon addon, Runnable task, long delayTicks, long periodTicks, boolean async) {
        long delayMs = delayTicks * 50;
        long periodMs = periodTicks > 0 ? periodTicks * 50 : -1;

        int id = taskCounter.incrementAndGet();
        WrappedTask wrappedTask = new WrappedTask(addon, id, !async, task);
        add(wrappedTask);

        if (async) {
            if (periodMs > 0) {
                ScheduledFuture<?> future = syncExecutor.scheduleAtFixedRate(
                        () -> asyncExecutor.execute(wrappedTask),
                        delayMs, periodMs, TimeUnit.MILLISECONDS);
                wrappedTask.setFuture(future);
            } else {
                if (delayMs > 0) {
                    ScheduledFuture<?> future = syncExecutor.schedule(
                            () -> asyncExecutor.execute(wrappedTask),
                            delayMs, TimeUnit.MILLISECONDS);
                    wrappedTask.setFuture(future);
                } else {
                    asyncExecutor.execute(wrappedTask);
                }
            }
        } else {
            ScheduledFuture<?> future = (periodMs > 0)
                    ? syncExecutor.scheduleAtFixedRate(wrappedTask, delayMs, periodMs, TimeUnit.MILLISECONDS)
                    : syncExecutor.schedule(wrappedTask, delayMs, TimeUnit.MILLISECONDS);
            wrappedTask.setFuture(future);
        }

        return wrappedTask;
    }

    private void scheduleConsumer(Addon addon, Consumer<SchedulerTask> consumer, long delayTicks, long periodTicks, boolean async) {
        long delayMs = delayTicks * 50;
        long periodMs = periodTicks > 0 ? periodTicks * 50 : -1;

        int id = getId();
        WrappedTask[] wrapperHolder = new WrappedTask[1];

        Runnable task = () -> consumer.accept(wrapperHolder[0]);

        WrappedTask wrappedTask = new WrappedTask(addon, id, !async, task);
        wrapperHolder[0] = wrappedTask;
        add(wrappedTask);

        if (async) {
            if (periodMs > 0) {
                ScheduledFuture<?> future = syncExecutor.scheduleAtFixedRate(
                        () -> asyncExecutor.execute(wrappedTask),
                        delayMs, periodMs, TimeUnit.MILLISECONDS);
                wrappedTask.setFuture(future);
            } else {
                if (delayMs > 0) {
                    ScheduledFuture<?> future = syncExecutor.schedule(
                            () -> asyncExecutor.execute(wrappedTask),
                            delayMs, TimeUnit.MILLISECONDS);
                    wrappedTask.setFuture(future);
                } else {
                    asyncExecutor.execute(wrappedTask);
                }
            }
        } else {
            ScheduledFuture<?> future = (periodMs > 0)
                    ? syncExecutor.scheduleAtFixedRate(wrappedTask, delayMs, periodMs, TimeUnit.MILLISECONDS)
                    : syncExecutor.schedule(wrappedTask, delayMs, TimeUnit.MILLISECONDS);
            wrappedTask.setFuture(future);
        }
    }

    protected int getId() {
        return taskCounter.incrementAndGet();
    }

    @Override
    public void cancel(int taskId) {
        remove(taskId);
    }

    @Override
    public void shutdown() {
        syncExecutor.shutdownNow();
        asyncExecutor.shutdownNow();
        tasks.values().forEach(SchedulerTask::cancel);
        tasks.clear();
    }
}