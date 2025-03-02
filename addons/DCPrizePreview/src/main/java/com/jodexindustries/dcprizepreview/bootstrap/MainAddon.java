package com.jodexindustries.dcprizepreview.bootstrap;

import com.jodexindustries.dcprizepreview.config.Config;
import com.jodexindustries.dcprizepreview.gui.EventListener;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.InternalJavaAddon;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class MainAddon extends InternalJavaAddon {

    public final DCAPI api = DCAPI.getInstance();
    private final EventListener eventListener = new EventListener(this);

    public Config config;

    @Override
    public void onLoad() {
        config = new Config(this);
    }

    @Override
    public void onEnable() {
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
}
