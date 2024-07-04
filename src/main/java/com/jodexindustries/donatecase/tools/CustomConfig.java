package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.database.yaml.YamlData;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Class for load all configuration files
 */
public class CustomConfig {

    private final File fileCases;
    private final File fileKeys;
    private final File fileConfig;
    private final File fileAnimations;
    private YamlConfiguration lang;
    private File fileLang;
    private final YamlConfiguration cases;
    private final YamlConfiguration keys;
    private final YamlConfiguration config;
    private final YamlConfiguration animations;

    private final YamlData data;

    /**
     * Default initialization constructor
     */
    public CustomConfig() {
        fileAnimations = new File(Case.getInstance().getDataFolder(), "Animations.yml");
        animations = YamlConfiguration.loadConfiguration(fileAnimations);
        fileCases = new File(Case.getInstance().getDataFolder(), "Cases.yml");
        cases = YamlConfiguration.loadConfiguration(fileCases);
        fileKeys = new File(Case.getInstance().getDataFolder(), "Keys.yml");
        keys = YamlConfiguration.loadConfiguration(fileKeys);
        fileConfig = new File(Case.getInstance().getDataFolder(), "Config.yml");
        config = YamlConfiguration.loadConfiguration(fileConfig);

        data = new YamlData();

        File path = new File(Case.getInstance().getDataFolder(), "lang");
        File[] listFiles = path.listFiles();
        if(listFiles == null) return;
        String lang = getConfig().getString("DonatCase.Languages");
        for (File l : listFiles) {
            if (l.getName().toLowerCase().split("_")[0].equalsIgnoreCase(lang)) {
                this.fileLang = l;
                this.lang = YamlConfiguration.loadConfiguration(l);
                break;
            }
        }
    }

    /**
     * Reload all configurations
     */
    public void reload(){
        Case.getInstance().setupConfigs();
    }

    /**
     * Save Cases.yml configuration
     */
    public void saveCases() {
        try {
            cases.save(fileCases);
        } catch (IOException var1) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save Cases.yml");
        }

    }

    /**
     * Save Config.yml configuration
     */
    public void saveConfig() {
        try {
            config.save(fileConfig);
        } catch (IOException var1) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save Config.yml");
        }
    }

    /**
     * Save Keys.yml configuration
     */
    public void saveKeys() {
        try {
            keys.save(fileKeys);
        } catch (IOException var1) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save Keys.yml");
        }
    }

    /**
     * Save Animations.yml configuration
     */
    public void saveAnimations() {
        try {
            animations.save(fileAnimations);
        } catch (IOException var1) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save Animations.yml");
        }

    }

    /**
     * Save lang configuration
     */
    public void saveLang() {
        try {
            lang.save(fileLang);
        } catch (IOException ignored) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save " + fileLang.getName());
        }
    }


    /**
     * Get Cases.yml configuration
     * @return Configuration
     */
    public YamlConfiguration getCases() {
        if(cases == null) {
            reload();
        }
        return cases;
    }

    /**
     * Get Keys.yml configuration
     * @return Configuration
     */
    public YamlConfiguration getKeys() {
        if(keys == null) {
            reload();
        }
        return keys;
    }

    /**
     * Get Config.yml configuration
     * @return Configuration
     */
    public YamlConfiguration getConfig() {
        if(config == null) {
            reload();
        }
        return config;
    }

    /**
     * Get Animations.yml configuration
     * @return Configuration
     */
    public YamlConfiguration getAnimations() {
        if(animations == null) {
            reload();
        }
        return animations;
    }

    /**
     * Get language configuration
     * @return Lang configuration
     */
    public YamlConfiguration getLang() {
        return lang;
    }

    public YamlData getData() {
        return data;
    }
}
