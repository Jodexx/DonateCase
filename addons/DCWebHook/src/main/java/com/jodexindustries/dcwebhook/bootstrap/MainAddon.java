package com.jodexindustries.dcwebhook.bootstrap;

import com.jodexindustries.dcwebhook.config.Config;
import com.jodexindustries.dcwebhook.events.EventListener;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.InternalJavaAddon;

public final class MainAddon extends InternalJavaAddon {

    public final DCAPI api = DCAPI.getInstance();
    public final EventListener eventListener = new EventListener(this);

    public Config config;

    @Override
    public void onLoad() {
        this.config = new Config(this);
    }

    @Override
    public void onEnable() {
        config.load();

        api.getEventBus().register(eventListener);
    }

    @Override
    public void onDisable() {
        api.getEventBus().unregister(eventListener);
    }

}
