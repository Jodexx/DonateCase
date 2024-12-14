package com.jodexindustries.freecases.config;

import com.jodexindustries.freecases.bootstrap.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {
    private final File configFile;
    private final File dataFile;
    private YamlConfiguration config;
    private YamlConfiguration data;
    private final Logger logger;

    public Config(Main main) {
        this.logger = main.getLogger();
        this.configFile = new File(main.getDataFolder(), "Config.yml");
        this.dataFile = new File(main.getDataFolder(), "Data.yml");

        if(!configFile.exists()) main.saveResource("Config.yml", false);
        if(!dataFile.exists()) main.saveResource("Data.yml", false);

        this.config = YamlConfiguration.loadConfiguration(configFile);
        this.data = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        data = YamlConfiguration.loadConfiguration(dataFile);
        logger.info("Config reloaded");
    }

    public YamlConfiguration getConfig() {
        return config;
    }
    public YamlConfiguration getData() {
        return data;
    }
    public void saveData() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Couldn't save Data.yml");
        }
    }
}
