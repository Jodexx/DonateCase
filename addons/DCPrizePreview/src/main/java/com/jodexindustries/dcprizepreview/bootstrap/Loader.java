package com.jodexindustries.dcprizepreview.bootstrap;

import com.jodexindustries.dcprizepreview.config.Config;
import com.jodexindustries.dcprizepreview.gui.EventListener;
import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.addon.external.ExternalAddon;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddonBukkit;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Loader {
    private final Plugin plugin;
    private InternalJavaAddonBukkit addon;
    private EventListener listener;
    private DCAPIBukkit api;

    public Loader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public Loader(InternalJavaAddonBukkit addon) {
        this.addon = addon;
        this.plugin = ((ExternalAddon) addon.getDonateCase()).getPlugin();
    }

    public void enable() {
        Config config = addon != null ? new Config(addon) : new Config(plugin);
        api = addon != null ? DCAPIBukkit.get(addon) : DCAPIBukkit.get(plugin);
        listener = new EventListener(config, api);
        Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public void disable() {
        if (listener != null) {
            HandlerList.unregisterAll(listener);
        }
    }

}