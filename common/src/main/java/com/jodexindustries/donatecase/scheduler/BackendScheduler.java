package com.jodexindustries.donatecase.scheduler;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.scheduler.Scheduler;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class BackendScheduler implements Scheduler {

    private final AtomicInteger taskIdCounter = new AtomicInteger(1);
    private final ConcurrentMap<Integer, SchedulerTask> tasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService syncExecutor = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService asyncExecutor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    @Override
    public SchedulerTask run(Addon addon, Runnable task, long delay) {
        return scheduleTask(addon, task, delay, 0, true);
    }

    @Override
    public SchedulerTask run(Addon addon, Runnable task, long delay, long period) {
        return scheduleTask(addon, task, delay, period, true);
    }

    @Override
    public SchedulerTask async(Addon addon, Runnable task, long delay) {
        return scheduleTask(addon, task, delay, 0, false);
    }

    @Override
    public SchedulerTask async(Addon addon, Runnable task, long delay, long period) {
        return scheduleTask(addon, task, delay, period, false);
    }

    @Override
    public void cancel(int taskId) {
        SchedulerTask task = tasks.remove(taskId);
        if (task != null && !task.isCancelled()) task.cancel();
    }

    @Override
    public void shutdown() {
        syncExecutor.shutdown();
        asyncExecutor.shutdown();
        try {
            if (!syncExecutor.awaitTermination(5, TimeUnit.SECONDS)) syncExecutor.shutdownNow();
            if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) asyncExecutor.shutdownNow();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            asyncExecutor.shutdownNow();
            syncExecutor.shutdownNow();
        }
        tasks.values().forEach(SchedulerTask::cancel);
        tasks.clear();
    }

    private SchedulerTask scheduleTask(Addon owner, Runnable task, long delay, long period, boolean async) {
        if (delay < 0 || period < 0) throw new IllegalArgumentException("Delay and period must be non-negative.");

        int taskId = taskIdCounter.getAndIncrement();
        WrappedTask wrappedTask = new WrappedTask(owner, taskId, async, task);

        ScheduledExecutorService executor = async ? asyncExecutor : syncExecutor;

        if (period > 0) {
            executor.scheduleAtFixedRate(() -> executeTask(wrappedTask), delay, period, TimeUnit.MILLISECONDS);
        } else {
            executor.schedule(() -> {
                executeTask(wrappedTask);
                tasks.remove(taskId);
            }, delay, TimeUnit.MILLISECONDS);
        }

        tasks.put(taskId, wrappedTask);
        return wrappedTask;
    }

    private void executeTask(SchedulerTask task) {
        if (!task.isCancelled()) {
            try {
                task.run();
            } catch (Exception e) {
                task.getOwner().getLogger().log(Level.WARNING, "Error with executing task: " + task.getTaskId(), e);
            }
        }
    }

    private static class WrappedTask implements SchedulerTask {

        private final Addon owner;
        private final int taskId;
        private final boolean async;
        private final Runnable delegate;

        private volatile boolean cancelled = false;

        public WrappedTask(Addon owner, int taskId, boolean async, Runnable delegate) {
            this.owner = owner;
            this.taskId = taskId;
            this.async = async;
            this.delegate = delegate;
        }

        @Override
        public void run() {
            if (!isCancelled()) {
                delegate.run();
            }
        }

        @Override
        public int getTaskId() {
            return taskId;
        }

        @Override
        public boolean isAsync() {
            return async;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public Addon getOwner() {
            return owner;
        }

        @Override
        public void cancel() {
            this.cancelled = true;
            DCAPI.getInstance().getScheduler().cancel(taskId);
        }

    }
}
