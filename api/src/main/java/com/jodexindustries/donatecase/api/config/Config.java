package com.jodexindustries.donatecase.api.config;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;

public interface Config {

    ConfigurationNode node();

    default ConfigurationNode node(Object... path) {
        return node().node(path);
    }

    File file();

    void load() throws ConfigurateException;

    void save() throws ConfigurateException;
}
