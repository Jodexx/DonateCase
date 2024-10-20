package com.jodexindustries.dcprizepreview.bootstrap;

import com.jodexindustries.dcprizepreview.config.Config;
import com.jodexindustries.dcprizepreview.gui.EventListener;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Loader {
    private final Plugin plugin;
    private InternalJavaAddon addon;
    private EventListener listener;

    public Loader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public Loader(InternalJavaAddon addon) {
        this.addon = addon;
        this.plugin = addon.getDonateCase();
    }

    public void enable() {
        Config config = addon != null ? new Config(addon) : new Config(plugin);
        listener = new EventListener(config);
        Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public void disable() {
        if (listener != null) {
            HandlerList.unregisterAll(listener);
        }
    }
}