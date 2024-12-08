package com.jodexindustries.dcprizepreview.bootstrap;

import com.jodexindustries.dcprizepreview.config.Config;
import com.jodexindustries.dcprizepreview.gui.EventListener;
import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.addon.external.ExternalJavaAddon;
import com.jodexindustries.donatecase.api.addon.internal.InternalAddon;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class Loader {
    private final Plugin plugin;
    private final DCAPIBukkit api;
    private final Addon addon;
    private EventListener listener;

    public Loader(Plugin plugin) {
        this.addon = new ExternalJavaAddon(plugin);
        this.api = DCAPIBukkit.get(plugin);
        this.plugin = plugin;
    }

    public Loader(InternalAddon addon) {
        this.addon = addon;
        this.api = DCAPIBukkit.get(addon);
        this.plugin = api.getDonateCase();
    }

    public void enable() {
        Config config = new Config(addon);
        listener = new EventListener(config, api);
        Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public void disable() {
        if (listener != null) {
            HandlerList.unregisterAll(listener);
        }
    }

}