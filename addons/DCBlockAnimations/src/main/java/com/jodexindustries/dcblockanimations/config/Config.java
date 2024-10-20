package com.jodexindustries.dcblockanimations.config;

import com.jodexindustries.dcblockanimations.bootstrap.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {
    private YamlConfiguration config;
    private final File configFile;

    public Config(Main main) {
        configFile = new File(main.getDataFolder(), "config.yml");

        if(!configFile.exists()) main.saveResource("config.yml", false);

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }
}
