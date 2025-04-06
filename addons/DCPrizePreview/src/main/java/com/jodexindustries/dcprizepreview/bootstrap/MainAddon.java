package com.jodexindustries.dcprizepreview.bootstrap;

import com.jodexindustries.dcprizepreview.config.Config;
import com.jodexindustries.dcprizepreview.gui.EventListener;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.InternalJavaAddon;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.spongepowered.configurate.ConfigurateException;

import java.io.File;
import java.util.logging.Level;

public class MainAddon extends InternalJavaAddon {

    public final DCAPI api = DCAPI.getInstance();
    private final EventListener eventListener = new EventListener(this);

    public Config config;

    @Override
    public void onLoad() {
        config = new Config(new File(getDataFolder(), "config.yml"), this);
    }

    @Override
    public void onEnable() {
        load(false);
        api.getEventBus().register(eventListener);
        Bukkit.getServer().getPluginManager().registerEvents(eventListener, BukkitUtils.getDonateCase());
    }

    @Override
    public void onDisable() {
        if (eventListener != null) {
            api.getEventBus().unregister(eventListener);
            HandlerList.unregisterAll(eventListener);
        }
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
