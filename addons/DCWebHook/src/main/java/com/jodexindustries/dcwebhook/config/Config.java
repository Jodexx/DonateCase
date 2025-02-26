package com.jodexindustries.dcwebhook.config;


import com.jodexindustries.dcwebhook.bootstrap.MainAddon;
import lombok.Getter;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.logging.Level;

public class Config {

    private final MainAddon addon;
    private final YamlConfigurationLoader loader;

    @Getter
    private DiscordWebhook webhook;

    public Config(MainAddon addon) {
        this.addon = addon;
        File file = new File(addon.getDataFolder(), "config.yml");
        if(!file.exists()) addon.saveResource("config.yml", false);
        this.loader = YamlConfigurationLoader.builder().file(file).build();
    }

    public void load() {
        try {
            ConfigurationNode node = loader.load();
            this.webhook = node.get(DiscordWebhook.class);
        } catch (ConfigurateException e) {
            addon.getLogger().log(Level.WARNING, "Error with loading configuration", e);
        }
    }

}