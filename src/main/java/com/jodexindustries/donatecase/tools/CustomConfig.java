package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.dc.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class CustomConfig {
    private File fileCases;
    private File fileKeys;
    private File fileConfig;
    private File fileAnimations;
    private YamlConfiguration Cases;
    private YamlConfiguration Keys;
    public YamlConfiguration Config;
    public YamlConfiguration Animations;

    public CustomConfig() {
        fileAnimations = new File(Main.instance.getDataFolder(), "Animations.yml");
        Animations = YamlConfiguration.loadConfiguration(fileAnimations);
        fileCases = new File(Main.instance.getDataFolder(), "Cases.yml");
        Cases = YamlConfiguration.loadConfiguration(fileCases);
        fileKeys = new File(Main.instance.getDataFolder(), "Keys.yml");
        Keys = YamlConfiguration.loadConfiguration(fileKeys);
        fileConfig = new File(Main.instance.getDataFolder(), "Config.yml");
        Config = YamlConfiguration.loadConfiguration(fileConfig);
    }

    public void reload(){
        Main.instance.setupConfigs();
    }

    public void saveCases() {
        try {
            Cases.save(fileCases);
        } catch (IOException var1) {
            Main.instance.getLogger().log(Level.WARNING, "Couldn't save Cases.yml");
        }

    }
    public void saveConfig() {
        try {
            Config.save(fileConfig);
        } catch (IOException var1) {
            Main.instance.getLogger().log(Level.WARNING, "Couldn't save Config.yml");
        }

    }

    public void saveKeys() {
        try {
            Keys.save(fileKeys);
        } catch (IOException var1) {
            Main.instance.getLogger().log(Level.WARNING, "Couldn't save Keys.yml");
        }

    }

    public YamlConfiguration getCases() {
        if(Cases == null) {
            reload();
        }
        return Cases;
    }

    public YamlConfiguration getKeys() {
        if(Keys == null) {
            reload();
        }
        return Keys;
    }

    public YamlConfiguration getConfig() {
        if(Config == null) {
            reload();
        }
        return Config;
    }

    public YamlConfiguration getAnimations() {
        if(Animations == null) {
            reload();
        }
        return Animations;
    }
}
