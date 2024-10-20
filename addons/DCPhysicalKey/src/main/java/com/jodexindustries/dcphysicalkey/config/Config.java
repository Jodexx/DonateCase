package com.jodexindustries.dcphysicalkey.config;

import com.jodexindustries.dcphysicalkey.bootstrap.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {

    private final File file;
    private YamlConfiguration config;
    public Config(Main plugin) {
        file = new File(plugin.getDataFolder(), "config.yml");
        if(!file.exists()) plugin.saveResource("config.yml", false);

        config = YamlConfiguration.loadConfiguration(file);
    }

    public YamlConfiguration get() {
        return config;
    }

    public void reloadConfig(){
        config = YamlConfiguration.loadConfiguration(file);
    }
}

