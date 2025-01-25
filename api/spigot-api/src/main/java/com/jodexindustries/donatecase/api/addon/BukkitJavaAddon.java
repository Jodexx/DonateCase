package com.jodexindustries.donatecase.api.addon;

import com.jodexindustries.donatecase.api.addon.Addon;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Logger;

public class BukkitJavaAddon implements Addon {
    private final Plugin plugin;

    public BukkitJavaAddon(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return plugin.getName();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }

    @Override
    @NotNull
    public File getDataFolder() {
        return plugin.getDataFolder();
    }
}
