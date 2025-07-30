package com.jodexindustries.donatecase.common.scheduler;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;
import lombok.Setter;

import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;
import java.util.logging.Level;

public class WrappedTask implements SchedulerTask {

    private final Addon owner;
    private final int taskId;
    private final boolean sync;

    private final Runnable r;
    private final Consumer<SchedulerTask> c;

    private volatile boolean cancelled = false;
    @Setter
    private ScheduledFuture<?> future;

    @SuppressWarnings("unchecked")
    public WrappedTask(Addon owner, int taskId, boolean sync, Object task) {
        this.owner = owner;
        this.taskId = taskId;
        this.sync = sync;

        if (task instanceof Runnable) {
            this.r = (Runnable) task;
            this.c = null;
        } else if (task instanceof Consumer) {
            this.r = null;
            this.c = (Consumer<SchedulerTask>) task;
        } else {
            throw new IllegalArgumentException("Invalid task type: " + task.getClass());
        }
    }

    @Override
    public void run() {
        if (cancelled) return;
        try {
            if (r != null) r.run();
            if (c != null) c.accept(this);
        } catch (Throwable t) {
            DCAPI.getInstance().getPlatform().getLogger().log(Level.WARNING, "Exception while executing task: " + taskId, t);
        }
    }

    @Override
    public int getTaskId() {
        return taskId;
    }

    @Override
    public boolean isSync() {
        return sync;
    }

    @Override
    public boolean isCancelled() {
        return cancelled || (future != null && future.isCancelled());
    }

    @Override
    public Addon getOwner() {
        return owner;
    }

    @Override
    public void cancel() {
        cancelled = true;
        if (future != null) {
            future.cancel(false);
        }
        DCAPI.getInstance().getPlatform().getScheduler().cancel(taskId);
    }
}
