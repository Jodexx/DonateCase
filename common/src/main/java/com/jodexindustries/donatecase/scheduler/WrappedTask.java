package com.jodexindustries.donatecase.scheduler;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;

import java.util.function.Consumer;

public class WrappedTask implements SchedulerTask {

    private final Addon owner;
    private final int taskId;
    private final boolean sync;

    private final Runnable r;
    private final Consumer<SchedulerTask> c;

    private volatile boolean cancelled = false;

    @SuppressWarnings("unchecked")
    public WrappedTask(Addon owner, int taskId, boolean sync, Object task) {
        this.owner = owner;
        this.taskId = taskId;
        this.sync = sync;
        if (task instanceof Runnable) {
            this.c = null;
            this.r = (Runnable) task;
        } else if (task instanceof Consumer) {
            this.r = null;
            this.c = (Consumer<SchedulerTask>) task;
        } else {
            throw new AssertionError("Illegal task class " + task);
        }
    }

    @Override
    public void run() {
        if (!isCancelled()) {
            if (r != null) r.run();
            if (c != null) c.accept(this);
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
        return cancelled;
    }

    @Override
    public Addon getOwner() {
        return owner;
    }

    @Override
    public void cancel() {
        this.cancelled = true;
        DCAPI.getInstance().getPlatform().getScheduler().cancel(taskId);
    }

}
