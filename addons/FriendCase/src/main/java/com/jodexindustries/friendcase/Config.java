package com.jodexindustries.friendcase;


import lombok.Getter;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Config {

    private final MainAddon addon;
    private final YamlConfigurationLoader loader;

    @Getter
    private ConfigurationNode node;

    public Config(MainAddon addon) {
        this.addon = addon;
        File file = new File(addon.getDataFolder(), "config.yml");
        if(!file.exists()) addon.saveResource("config.yml", false);
        this.loader = YamlConfigurationLoader.builder().file(file).build();
    }

    public void load() {
        try {
            this.node = loader.load();
        } catch (ConfigurateException e) {
            addon.getLogger().log(Level.WARNING, "Error with loading configuration", e);
        }
    }

    public String getString(Object... path) {
        return node.node(path).getString();
    }

    public List<String> getList(Object... path) {
        try {
            return node.node(path).getList(String.class);
        } catch (SerializationException e) {
            return new ArrayList<>();
        }
    }

}