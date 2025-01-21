package com.jodexindustries.donatecase.api.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Map;

public interface ConfigCases {

    void load() throws ConfigurateException;

    @Nullable
    ConfigurationNode getCase(@NotNull String name);

    @NotNull
    Map<String, ConfigurationNode> getMap();
}
