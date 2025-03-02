package com.jodexindustries.dcblockanimations;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {

    @Getter
    private YamlConfiguration config;
    private final File configFile;

    public Config(MainAddon addon) {
        configFile = new File(addon.getDataFolder(), "config.yml");

        if(!configFile.exists()) addon.saveResource("config.yml", false);

        load();
    }

    public void load() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }
}
