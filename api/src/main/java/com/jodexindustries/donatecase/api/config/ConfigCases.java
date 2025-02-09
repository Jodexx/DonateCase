package com.jodexindustries.donatecase.api.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;

import java.util.Map;

public interface ConfigCases {

    void load() throws ConfigurateException;

    @Nullable
    Config get(@NotNull String name);

    @NotNull
    Map<String, Config> getMap();
}
