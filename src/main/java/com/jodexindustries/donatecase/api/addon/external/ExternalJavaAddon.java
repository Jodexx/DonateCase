package com.jodexindustries.donatecase.api.addon.external;

import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class ExternalJavaAddon implements ExternalAddon {
    private final Plugin plugin;
    public ExternalJavaAddon(Plugin plugin) {
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
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.isEnabled();
    }

    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }
}
