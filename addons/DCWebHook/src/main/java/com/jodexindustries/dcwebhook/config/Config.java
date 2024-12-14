package com.jodexindustries.dcwebhook.config;

import com.jodexindustries.dcwebhook.bootstrap.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {

    private final Main main;
    private YamlConfiguration config;
    private final File configFile;

    public Config(Main main) {
        this.main = main;
        configFile = new File(main.getDataFolder(), "config.yml");
        if (!configFile.exists()) main.saveResource("config.yml", false);

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        main.getLogger().info("Config reloaded");
    }

}
