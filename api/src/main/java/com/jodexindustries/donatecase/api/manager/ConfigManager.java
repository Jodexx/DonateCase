package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.config.CaseStorage;
import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.config.Loadable;
import com.jodexindustries.donatecase.api.config.Messages;
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
    ConfigurationNode getNode(@NotNull String name);

    Map<String, ? extends Config> get();

    default ConfigurationNode getConfig() {
        return getNode("Config.yml");
    }

    default ConfigurationNode getAnimations() {
        return getNode("Animations.yml");
    }

    @NotNull
    Messages getMessages();

    @NotNull
    CaseStorage getCaseStorage();

}
