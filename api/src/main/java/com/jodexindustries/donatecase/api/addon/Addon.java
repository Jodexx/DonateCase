package com.jodexindustries.donatecase.api.addon;

import com.jodexindustries.donatecase.api.CaseManager;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

public interface Addon extends Plugin {
    @NotNull
    @Override
    default PluginDescriptionFile getDescription() {
        return null;
    }

    @NotNull
    @Override
    default FileConfiguration getConfig() {
        return null;
    }

    @Override
    default void saveConfig() {

    }

    @Override
    default void saveDefaultConfig() {

    }

    @Override
    default void reloadConfig() {

    }

    @NotNull
    @Override
    default PluginLoader getPluginLoader() {
        return null;
    }

    @NotNull
    @Override
    default Server getServer() {
        return null;
    }

    @Override
    default boolean isEnabled() {
        return false;
    }

    @Override
    default void onLoad() {

    }

    @Override
    default boolean isNaggable() {
        return false;
    }

    @Override
    default void setNaggable(boolean canNag) {

    }

    @Nullable
    @Override
    default ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, @Nullable String id) {
        return null;
    }

    @NotNull
    @Override
    default Logger getLogger() {
        return null;
    }

    void onDisable();
    void onEnable();
    String getName();
    String getVersion();
    Plugin getDonateCase();
    CaseManager getCaseAPI();
    File getDataFolder();
    void saveResource(@NotNull String resourcePath, boolean replace);
    InputStream getResource(@NotNull String filename);
}