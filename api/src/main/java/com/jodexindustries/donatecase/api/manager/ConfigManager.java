package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.config.CaseStorage;
import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.config.Loadable;
import com.jodexindustries.donatecase.api.config.Messages;
import com.jodexindustries.donatecase.api.config.converter.ConfigType;
import com.jodexindustries.donatecase.api.data.config.ConfigData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.util.Map;
import java.util.Optional;

public interface ConfigManager extends Loadable {

    Config load(@NotNull File file);

    @Nullable
    Config unload(@NotNull String name);

    @Nullable
    Config getConfig(@NotNull String name);

    @NotNull
    Optional<? extends Config> getConfig(@NotNull ConfigType type);

    @Nullable
    ConfigurationNode getNode(@NotNull String name);

    Map<String, ? extends Config> get();

    ConfigData getConfig();

    default ConfigurationNode getAnimations() {
        return getNode("Animations.yml");
    }

    @NotNull
    Messages getMessages();

    @NotNull
    CaseStorage getCaseStorage();

}
