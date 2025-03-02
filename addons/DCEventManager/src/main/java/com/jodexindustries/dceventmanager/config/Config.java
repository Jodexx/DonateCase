package com.jodexindustries.dceventmanager.config;

import com.jodexindustries.dceventmanager.bootstrap.MainAddon;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {
    @Getter
    private YamlConfiguration config;
    @Getter
    private YamlConfiguration placeholders;
    private final File configFile;
    private final File placeholdersFile;

    public Config(MainAddon main) {
        configFile = new File(main.getDataFolder(), "config.yml");
        placeholdersFile = new File(main.getDataFolder(), "placeholders.yml");

        if(!configFile.exists()) main.saveResource("config.yml", false);
        if(!placeholdersFile.exists()) main.saveResource("placeholders.yml", false);

        config = YamlConfiguration.loadConfiguration(configFile);
        placeholders = YamlConfiguration.loadConfiguration(placeholdersFile);
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }
    public void reloadPlaceholders() {
        placeholders = YamlConfiguration.loadConfiguration(placeholdersFile);
    }
}
