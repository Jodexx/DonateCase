package com.jodexindustries.donatecase.api.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.util.Map;

public interface ConfigManager extends Loadable {

    Config load(@NotNull File file);

    @Nullable
    Config getConfig(@NotNull String name);

    @Nullable
    ConfigurationNode get(@NotNull String name);

    Map<String, ? extends Config> get();

    default ConfigurationNode getConfig() {
        return get("Config.yml");
    }

    default ConfigurationNode getAnimations() {
        return get("Animations.yml");
    }

    @NotNull
    Messages getMessages();

    @NotNull
    CaseStorage getCaseStorage();

}
