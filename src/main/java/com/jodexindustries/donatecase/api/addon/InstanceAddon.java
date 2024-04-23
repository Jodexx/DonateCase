package com.jodexindustries.donatecase.api.addon;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.CaseAPI;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;

public interface InstanceAddon extends Addon {
    @Override
    default void onDisable() {}

    @Override
    default void onEnable() {}

    @Override
    default Plugin getDonateCase() {
        return DonateCase.instance;
    }
    @Override
    default CaseAPI getCaseAPI() {
        return DonateCase.api;
    }

    @Override
    default File getDataFolder() {
        return null;
    }

    @Override
    default void saveResource(@NotNull String resourcePath, boolean replace) {}

    @Override
    default InputStream getResource(@NotNull String filename) {
        return null;
    }
}
