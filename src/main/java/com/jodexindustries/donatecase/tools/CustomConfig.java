package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.dc.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class CustomConfig {
    private File filec;
    private File filek;
    private File filecon;
    private File fileanim;
    private YamlConfiguration Cases;
    private YamlConfiguration Keys;
    public YamlConfiguration Config;
    public YamlConfiguration Animations;

    public CustomConfig() {
        fileanim = new File(Main.instance.getDataFolder(), "Animations.yml");
        Animations = YamlConfiguration.loadConfiguration(fileanim);
        filec = new File(Main.instance.getDataFolder(), "Cases.yml");
        Cases = YamlConfiguration.loadConfiguration(filec);
        filek = new File(Main.instance.getDataFolder(), "Keys.yml");
        Keys = YamlConfiguration.loadConfiguration(filek);
        filecon = new File(Main.instance.getDataFolder(), "Config.yml");
        Config = YamlConfiguration.loadConfiguration(filecon);
    }


    public void reload(){
        fileanim = new File(Main.instance.getDataFolder(), "Animations.yml");
        Animations = YamlConfiguration.loadConfiguration(fileanim);
        filec = new File(Main.instance.getDataFolder(), "Cases.yml");
        Cases = YamlConfiguration.loadConfiguration(filec);
        filek = new File(Main.instance.getDataFolder(), "Keys.yml");
        Keys = YamlConfiguration.loadConfiguration(filek);
        filecon = new File(Main.instance.getDataFolder(), "Config.yml");
        Config = YamlConfiguration.loadConfiguration(filecon);
        Main.instance.setupConfigs();
    }

    public void saveCases() {
        try {
            Cases.save(filec);
        } catch (IOException var1) {
            Main.instance.getLogger().log(Level.WARNING, "Couldn't save Cases.yml");
        }

    }
    public void saveConfig() {
        try {
            Config.save(filecon);
        } catch (IOException var1) {
            Main.instance.getLogger().log(Level.WARNING, "Couldn't save Config.yml");
        }

    }

    public void saveKeys() {
        try {
            Keys.save(filek);
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
