package com.jodexindustries.donatecase.api.scheduler;

import com.jodexindustries.donatecase.api.addon.Addon;

public interface SchedulerTask extends Runnable {

    int getTaskId();

    boolean isSync();

    boolean isCancelled();

    Addon getOwner();

    void cancel();

}

