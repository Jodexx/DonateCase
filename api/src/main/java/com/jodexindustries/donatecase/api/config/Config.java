package com.jodexindustries.donatecase.api.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;

public interface Config extends Loadable {

    @Nullable
    ConfigurationNode get(@NotNull File file);

    @Nullable
    ConfigurationNode get(@NotNull String name);

    default ConfigurationNode getConfig() {
        return get("Config.yml");
    }

    default ConfigurationNode getAnimations() {
        return get("Animations.yml");
    }


    File getFile(@NotNull String name);

    void delete(@NotNull File file);

    void delete(@NotNull String name);

    boolean save(String name);

    boolean save(File file);

    @NotNull
    Messages getMessages();

    @NotNull
    ConfigCases getConfigCases();

    @NotNull
    CaseStorage getCaseStorage();

}
