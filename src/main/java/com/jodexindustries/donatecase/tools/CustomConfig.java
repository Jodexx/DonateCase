package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.dc.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

public class CustomConfig {
    private final File fileCases;
    private final File fileKeys;
    private final File fileConfig;
    private final File fileAnimations;
    private final File fileData;
    private YamlConfiguration Lang;
    private final YamlConfiguration Cases;
    private final YamlConfiguration Keys;
    private final YamlConfiguration Config;
    private final YamlConfiguration Animations;
    private final YamlConfiguration Data;

    public CustomConfig() {
        fileAnimations = new File(Main.instance.getDataFolder(), "Animations.yml");
        Animations = YamlConfiguration.loadConfiguration(fileAnimations);
        fileCases = new File(Main.instance.getDataFolder(), "Cases.yml");
        Cases = YamlConfiguration.loadConfiguration(fileCases);
        fileKeys = new File(Main.instance.getDataFolder(), "Keys.yml");
        Keys = YamlConfiguration.loadConfiguration(fileKeys);
        fileConfig = new File(Main.instance.getDataFolder(), "Config.yml");
        Config = YamlConfiguration.loadConfiguration(fileConfig);
        fileData = new File(Main.instance.getDataFolder(), "Data.yml");
        Data = YamlConfiguration.loadConfiguration(fileData);

        File path = new File(Main.instance.getDataFolder(), "lang");
        File[] listFiles;
        int length = (Objects.requireNonNull(listFiles = path.listFiles())).length;
        String lang = getConfig().getString("DonatCase.Languages");
        for(int i = 0; i < length; ++i) {
            File l = listFiles[i];
            if (l.getName().toLowerCase().split("_")[0].equalsIgnoreCase(lang)) {
                this.Lang = YamlConfiguration.loadConfiguration(l);
                break;
            }
        }
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

    public void saveAnimations() {
        try {
            Animations.save(fileAnimations);
        } catch (IOException var1) {
            Main.instance.getLogger().log(Level.WARNING, "Couldn't save Animations.yml");
        }

    }


    public void saveData() {
        try {
            Data.save(fileData);
        } catch (IOException var1) {
            Main.instance.getLogger().log(Level.WARNING, "Couldn't save Data.yml");
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
    public YamlConfiguration getData() {
        if(Data == null) {
            reload();
        }
        return Data;
    }

    public YamlConfiguration getLang() {
        return Lang;
    }
}
