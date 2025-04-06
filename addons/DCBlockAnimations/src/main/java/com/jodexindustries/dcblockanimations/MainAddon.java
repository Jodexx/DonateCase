package com.jodexindustries.dcblockanimations;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.InternalJavaAddon;
import lombok.Getter;
import org.spongepowered.configurate.ConfigurateException;

import java.io.File;
import java.util.logging.Level;

public final class MainAddon extends InternalJavaAddon {

    public static final DCAPI api = DCAPI.getInstance();

    @Getter
    private Config config;
    private EventListener eventListener;

    @Override
    public void onLoad() {
        this.config = new Config(new File(getDataFolder(), "config.yml"), this);
        this.eventListener = new EventListener(this);
    }

    @Override
    public void onEnable() {
        load(false);
        api.getEventBus().register(eventListener);
    }

    @Override
    public void onDisable() {
        api.getEventBus().unregister(eventListener);
    }

    public void load(boolean log) {
        try {
            config.load();
            if (log) getLogger().info("Config reloaded!");
        } catch (ConfigurateException e) {
            getLogger().log(Level.WARNING, "Error with loading configuration:", e);
        }
    }

}
