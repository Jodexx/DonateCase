package com.jodexindustries.donatecase.api.config;

import com.jodexindustries.donatecase.api.config.converter.ConfigType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;

public interface Config {

    ConfigurationNode node();

    default ConfigurationNode node(Object... path) {
        return node().node(path);
    }

    @Nullable
    default <T> T getSerialized(Class<T> clazz) {
        return null;
    }

    default String getNameWithoutExtension() {
        String name = file().getName();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex > 0) return name.substring(0, dotIndex);
        return name;
    }

    File file();

    String path();

    int version();

    ConfigType type();

    void type(ConfigType type);

    void load() throws ConfigurateException;

    boolean delete();

    void save() throws ConfigurateException;
}
