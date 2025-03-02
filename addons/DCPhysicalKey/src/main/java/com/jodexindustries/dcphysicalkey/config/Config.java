package com.jodexindustries.dcphysicalkey.config;

import com.jodexindustries.dcphysicalkey.bootstrap.MainAddon;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {

    private final File file;
    private YamlConfiguration config;
    public Config(MainAddon addon) {
        file = new File(addon.getDataFolder(), "config.yml");
        if(!file.exists()) addon.saveResource("config.yml", false);

        config = YamlConfiguration.loadConfiguration(file);
    }

    public YamlConfiguration get() {
        return config;
    }

    public void reloadConfig(){
        config = YamlConfiguration.loadConfiguration(file);
    }
}

