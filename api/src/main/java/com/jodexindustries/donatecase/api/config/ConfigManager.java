package com.jodexindustries.donatecase.api.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

public interface ConfigManager extends Loadable {

    @Nullable
    Config getConfig(@NotNull String name);

    @Nullable
    ConfigurationNode get(@NotNull String name);

    default ConfigurationNode getConfig() {
        return get("Config.yml");
    }

    default ConfigurationNode getAnimations() {
        return get("Animations.yml");
    }

    @NotNull
    Messages getMessages();

    @NotNull
    ConfigCases getConfigCases();

    @NotNull
    CaseStorage getCaseStorage();

}
