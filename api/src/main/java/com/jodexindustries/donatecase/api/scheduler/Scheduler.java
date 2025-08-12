package com.jodexindustries.donatecase.api.scheduler;

import com.jodexindustries.donatecase.api.addon.Addon;

import java.util.function.Consumer;

public interface Scheduler {

    SchedulerTask run(Addon addon, Runnable task);

    SchedulerTask run(Addon addon, Runnable task, long delay);

    SchedulerTask run(Addon addon, Runnable task, long delay, long period);

    void run(Addon addon, Consumer<SchedulerTask> task);

    void run(Addon addon, Consumer<SchedulerTask> task, long delay);

    void run(Addon addon, Consumer<SchedulerTask> task, long delay, long period);

    SchedulerTask async(Addon addon, Runnable task, long delay);

    SchedulerTask async(Addon addon, Runnable task, long delay, long period);

    void async(Addon addon, Consumer<SchedulerTask> task, long delay);

    void async(Addon addon, Consumer<SchedulerTask> task, long delay, long period);

    void cancel(int taskId);

    void shutdown();
}
