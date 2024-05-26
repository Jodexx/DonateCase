package com.jodexindustries.donatecase.api.addon.external;

import org.bukkit.plugin.Plugin;

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
}
