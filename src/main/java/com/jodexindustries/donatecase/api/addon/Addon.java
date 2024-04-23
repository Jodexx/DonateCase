package com.jodexindustries.donatecase.api.addon;

import com.jodexindustries.donatecase.api.CaseAPI;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;

public interface Addon {
    void onDisable();
    void onEnable();
    String getName();
    String getVersion();
    Plugin getDonateCase();
    CaseAPI getCaseAPI();
    File getDataFolder();
    void saveResource(@NotNull String resourcePath, boolean replace);
    InputStream getResource(@NotNull String filename);
}