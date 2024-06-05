package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.api.Case;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
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
        fileAnimations = new File(Case.getInstance().getDataFolder(), "Animations.yml");
        Animations = YamlConfiguration.loadConfiguration(fileAnimations);
        fileCases = new File(Case.getInstance().getDataFolder(), "Cases.yml");
        Cases = YamlConfiguration.loadConfiguration(fileCases);
        fileKeys = new File(Case.getInstance().getDataFolder(), "Keys.yml");
        Keys = YamlConfiguration.loadConfiguration(fileKeys);
        fileConfig = new File(Case.getInstance().getDataFolder(), "Config.yml");
        Config = YamlConfiguration.loadConfiguration(fileConfig);
        fileData = new File(Case.getInstance().getDataFolder(), "Data.yml");
        Data = YamlConfiguration.loadConfiguration(fileData);

        File path = new File(Case.getInstance().getDataFolder(), "lang");
        File[] listFiles;
        int length = (listFiles = path.listFiles()).length;
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
        Case.getInstance().setupConfigs();
    }

    public void saveCases() {
        try {
            Cases.save(fileCases);
        } catch (IOException var1) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save Cases.yml");
        }

    }
    public void saveConfig() {
        try {
            Config.save(fileConfig);
        } catch (IOException var1) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save Config.yml");
        }

    }

    public void saveKeys() {
        try {
            Keys.save(fileKeys);
        } catch (IOException var1) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save Keys.yml");
        }
    }

    public void saveAnimations() {
        try {
            Animations.save(fileAnimations);
        } catch (IOException var1) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save Animations.yml");
        }

    }


    public void saveData() {
        try {
            Data.save(fileData);
        } catch (IOException var1) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save Data.yml");
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
