package com.jodexindustries.donatecase.common.scheduler;

import com.jodexindustries.donatecase.api.scheduler.Scheduler;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class BackendScheduler implements Scheduler {

    protected final ConcurrentMap<Integer, SchedulerTask> tasks = new ConcurrentHashMap<>();

    protected void add(SchedulerTask task) {
        tasks.put(task.getTaskId(), task);
    }

    protected void remove(int taskId) {
        SchedulerTask task = tasks.remove(taskId);
        if(task != null) task.cancel();
    }

}
