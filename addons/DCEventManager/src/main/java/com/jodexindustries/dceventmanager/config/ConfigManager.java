package com.jodexindustries.dceventmanager.config;

import com.jodexindustries.dceventmanager.bootstrap.MainAddon;
import com.jodexindustries.donatecase.common.config.ConfigImpl;
import lombok.Getter;
import org.spongepowered.configurate.ConfigurateException;

import java.io.File;

@Getter
public class ConfigManager {

    private final ConfigImpl config;
    private final EventConfig eventConfig;
    private final PlaceholderConfig placeholderConfig;

    private final MainAddon addon;

    public ConfigManager(MainAddon addon) {
        this.addon = addon;

        File configFile = new File(addon.getDataFolder(), "config.yml");
        File eventFile = new File(addon.getDataFolder(), "events.yml");
        File placeholderFile = new File(addon.getDataFolder(), "placeholders.yml");

        if(!configFile.exists()) addon.saveResource("config.yml", false);
        if(!eventFile.exists()) addon.saveResource("events.yml", false);
        if(!placeholderFile.exists()) addon.saveResource("placeholders.yml", false);

        this.config = new ConfigImpl(configFile);
        this.eventConfig = new EventConfig(eventFile);
        this.placeholderConfig = new PlaceholderConfig(placeholderFile);
    }

    public void load() throws ConfigurateException {
        config.load();
        eventConfig.load();
        placeholderConfig.load();
    }
}
