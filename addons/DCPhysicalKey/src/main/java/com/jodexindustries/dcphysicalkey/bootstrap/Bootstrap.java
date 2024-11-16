package com.jodexindustries.dcphysicalkey.bootstrap;

import com.jodexindustries.dcphysicalkey.commands.MainCommand;
import com.jodexindustries.dcphysicalkey.config.Config;
import com.jodexindustries.dcphysicalkey.listener.EventListener;
import com.jodexindustries.dcphysicalkey.tools.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Bootstrap {
    private final Main plugin;
    private final Config config;
    private final ItemManager itemManager;
    private final EventListener eventListener;
    private final MainCommand mainCommand;

    @NotNull
    public static final NamespacedKey NAMESPACED_KEY = Objects.requireNonNull(NamespacedKey.fromString("dcphysicalkey:key"));

    public Bootstrap(Main plugin) {
        this.plugin = plugin;
        this.config = new Config(plugin);
        this.itemManager = new ItemManager(this);
        this.eventListener = new EventListener(this);
        this.mainCommand = new MainCommand(plugin.getDCAPI().getSubCommandManager(), this);
    }

    public void load() {
        mainCommand.register();
        itemManager.load();
        Bukkit.getServer().getPluginManager().registerEvents(eventListener, plugin.getPlugin());

    }

    public void unload() {
        mainCommand.unregister();
        HandlerList.unregisterAll(eventListener);
    }

    public Config getConfig() {
        return config;
    }

    public Main getPlugin() {
        return plugin;
    }
}
