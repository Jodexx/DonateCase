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
    private YamlConfiguration lang;
    private File fileLang;
    private final YamlConfiguration cases;
    private final YamlConfiguration keys;
    private final YamlConfiguration config;
    private final YamlConfiguration animations;
    private final YamlConfiguration data;

    public CustomConfig() {
        fileAnimations = new File(Case.getInstance().getDataFolder(), "Animations.yml");
        animations = YamlConfiguration.loadConfiguration(fileAnimations);
        fileCases = new File(Case.getInstance().getDataFolder(), "Cases.yml");
        cases = YamlConfiguration.loadConfiguration(fileCases);
        fileKeys = new File(Case.getInstance().getDataFolder(), "Keys.yml");
        keys = YamlConfiguration.loadConfiguration(fileKeys);
        fileConfig = new File(Case.getInstance().getDataFolder(), "Config.yml");
        config = YamlConfiguration.loadConfiguration(fileConfig);
        fileData = new File(Case.getInstance().getDataFolder(), "Data.yml");
        data = YamlConfiguration.loadConfiguration(fileData);

        File path = new File(Case.getInstance().getDataFolder(), "lang");
        File[] listFiles;
        int length = (listFiles = path.listFiles()).length;
        String lang = getConfig().getString("DonatCase.Languages");
        for(int i = 0; i < length; ++i) {
            File l = listFiles[i];
            if (l.getName().toLowerCase().split("_")[0].equalsIgnoreCase(lang)) {
                this.fileLang = l;
                this.lang = YamlConfiguration.loadConfiguration(l);
                break;
            }
        }
    }

    public void reload(){
        Case.getInstance().setupConfigs();
    }

    public void saveCases() {
        try {
            cases.save(fileCases);
        } catch (IOException var1) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save Cases.yml");
        }

    }
    public void saveConfig() {
        try {
            config.save(fileConfig);
        } catch (IOException var1) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save Config.yml");
        }
    }

    public void saveKeys() {
        try {
            keys.save(fileKeys);
        } catch (IOException var1) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save Keys.yml");
        }
    }

    public void saveAnimations() {
        try {
            animations.save(fileAnimations);
        } catch (IOException var1) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save Animations.yml");
        }

    }

    public void saveLang() {
        try {
            lang.save(fileLang);
        } catch (IOException ignored) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save " + fileLang.getName());
        }
    }


    public void saveData() {
        try {
            data.save(fileData);
        } catch (IOException var1) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save Data.yml");
        }

    }

    public YamlConfiguration getCases() {
        if(cases == null) {
            reload();
        }
        return cases;
    }

    public YamlConfiguration getKeys() {
        if(keys == null) {
            reload();
        }
        return keys;
    }

    public YamlConfiguration getConfig() {
        if(config == null) {
            reload();
        }
        return config;
    }

    public YamlConfiguration getAnimations() {
        if(animations == null) {
            reload();
        }
        return animations;
    }
    public YamlConfiguration getData() {
        if(data == null) {
            reload();
        }
        return data;
    }

    public YamlConfiguration getLang() {
        return lang;
    }
}
