package com.jodexindustries.donatecase.api.addon;

import org.bukkit.plugin.Plugin;

public interface Addon {
    void onDisable();
    void onEnable();
    String getName();
    String getVersion();
    Plugin getDonateCase();
}