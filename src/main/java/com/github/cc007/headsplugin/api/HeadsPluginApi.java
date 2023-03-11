package com.github.cc007.headsplugin.api;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public class HeadsPluginApi {
    private static HeadsPluginServices headsPluginServices;

    /**
     * Gets a plugin
     *
     * @param pluginName Name of the plugin to get
     * @return Optional of the plugin from name
     */
    public static Optional<Plugin> getPlugin(String pluginName) {
        return Optional.ofNullable(
                Bukkit.getServer()
                        .getPluginManager()
                        .getPlugin(pluginName)
        ).filter(Plugin::isEnabled);
    }

    /**
     * Gets the HeadPluginAPI plugin
     *
     * @return Optional of the plugin
     */
    public static Optional<Plugin> getPlugin() {
        return getPlugin("HeadsPluginAPI");
    }

    /**
     * Get the services provider for this plugin.
     * These services include things like searching for heads, updating categories, etc.
     *
     * @return Optional of the services provider
     */
    public static Optional<HeadsPluginServices> getHeadsPluginServices() {
        return Optional.ofNullable(headsPluginServices);
    }

    /**
     * @hidden
     */
    public static void setHeadsPluginServices(@NonNull HeadsPluginServices headsPluginServices) {
        HeadsPluginApi.headsPluginServices = headsPluginServices;
    }
}
