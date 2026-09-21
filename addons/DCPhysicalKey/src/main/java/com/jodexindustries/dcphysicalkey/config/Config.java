package com.jodexindustries.dcphysicalkey.config;

import com.jodexindustries.dcphysicalkey.bootstrap.MainAddon;
import com.jodexindustries.donatecase.common.config.ConfigImpl;
import com.jodexindustries.donatecase.common.config.converter.DefaultConfigType;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.util.logging.Level;

public class Config {

    private final MainAddon addon;
    private final ConfigImpl config;

    public Config(MainAddon addon) {
        this.addon = addon;

        File file = new File(addon.getDataFolder(), "config.yml");
        if (!file.exists()) {
            addon.saveResource("config.yml", false);
        }

        config = new ConfigImpl(file, DefaultConfigType.UNKNOWN_CUSTOM);
        load();
    }

    public ConfigurationNode node(Object... path) {
        return config.node(path);
    }

    public void load() {
        try {
            config.load();
        } catch (ConfigurateException e) {
            addon.getLogger().log(Level.WARNING, "Error with loading configuration", e);
        }
    }
}
