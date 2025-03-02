package com.jodexindustries.dcblockanimations;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.InternalJavaAddon;
import lombok.Getter;

public final class MainAddon extends InternalJavaAddon {

    public final DCAPI api = DCAPI.getInstance();

    @Getter
    private Config config;
    private EventListener eventListener;

    @Override
    public void onLoad() {
        this.config = new Config(this);
        this.eventListener = new EventListener(this);
    }

    @Override
    public void onEnable() {
        api.getEventBus().register(eventListener);
    }

    @Override
    public void onDisable() {
        api.getEventBus().unregister(eventListener);
    }

}
