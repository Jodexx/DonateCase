package com.jodexindustries.donatecase.api.config;

import com.jodexindustries.donatecase.api.config.converter.ConfigType;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;

public interface Config {

    ConfigurationNode node();

    default ConfigurationNode node(Object... path) {
        return node().node(path);
    }

    File file();

    String path();

    int version();

    ConfigType type();

    void type(ConfigType type);

    void load() throws ConfigurateException;

    void save() throws ConfigurateException;
}
