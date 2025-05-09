package com.jodexindustries.dcwebhook.bootstrap;

import com.jodexindustries.dcwebhook.config.Config;
import com.jodexindustries.dcwebhook.events.EventListener;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.InternalJavaAddon;

import java.io.File;

public final class MainAddon extends InternalJavaAddon {

    public final DCAPI api = DCAPI.getInstance();
    public final EventListener eventListener = new EventListener(this);

    public static MainAddon instance;

    public Config config;

    @Override
    public void onLoad() {
        instance = this;
        this.config = new Config(new File(getDataFolder(), "config.yml"), this);
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
