package com.jodexindustries.donatecase.api.scheduler;

import com.jodexindustries.donatecase.api.addon.Addon;

public interface Scheduler {

    SchedulerTask run(Addon addon, Runnable task, long delay);

    SchedulerTask run(Addon addon, Runnable task, long delay, long period);

    SchedulerTask async(Addon addon, Runnable task, long delay);

    SchedulerTask async(Addon addon, Runnable task, long delay, long period);

    void cancel(int taskId);

    void shutdown();
}
