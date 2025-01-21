package com.jodexindustries.donatecase.api.config;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;

import java.util.List;

public interface Messages {

    @NotNull
    String getString(@NotNull String path);

    List<String> getStringList(@NotNull String path);

    void load(@NotNull String language) throws ConfigurateException;
}
