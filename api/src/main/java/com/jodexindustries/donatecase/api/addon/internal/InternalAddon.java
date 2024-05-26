package com.jodexindustries.donatecase.api.addon.internal;

import com.jodexindustries.donatecase.api.CaseManager;
import com.jodexindustries.donatecase.api.addon.Addon;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;

public interface InternalAddon extends Addon {
    void onDisable();
    void onEnable();
    File getDataFolder();
    void saveResource(@NotNull String resourcePath, boolean replace);
    InputStream getResource(@NotNull String filename);
    Plugin getDonateCase();
    CaseManager getCaseAPI();
}
