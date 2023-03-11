package net.jodexindustries.tools;

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CustomConfig {
    private static File filec;
    private static File filek;
    private static File filecon;
    private static FileConfiguration Cases;
    private static FileConfiguration Keys;
    public static YamlConfiguration Config;

    public CustomConfig() {
        filecon = new File(Bukkit.getServer().getPluginManager().getPlugin("DonateCase").getDataFolder(), "Config.yml");
        Config = YamlConfiguration.loadConfiguration(filecon);
    }

    public static void setup() {
        filec = new File(Bukkit.getServer().getPluginManager().getPlugin("DonateCase").getDataFolder(), "Cases.yml");
        Cases = YamlConfiguration.loadConfiguration(filec);
        filek = new File(Bukkit.getServer().getPluginManager().getPlugin("DonateCase").getDataFolder(), "Keys.yml");
        Keys = YamlConfiguration.loadConfiguration(filek);
        filecon = new File(Bukkit.getServer().getPluginManager().getPlugin("DonateCase").getDataFolder(), "Config.yml");
        Config = YamlConfiguration.loadConfiguration(filecon);
    }

    public static void saveCases() {
        try {
            Cases.save(filec);
        } catch (IOException var1) {
            System.out.println("Couldn't save Cases.yml");
        }

    }

    public static void saveConfig() {
        try {
            Config.save(filecon);
        } catch (IOException var1) {
            System.out.println("Couldn't save Config.yml");
        }

    }

    public static void saveKeys() {
        try {
            Keys.save(filek);
        } catch (IOException var1) {
            System.out.println("Couldn't save Keys.yml");
        }

    }

    public static FileConfiguration getCases() {
        return Cases;
    }

    public static FileConfiguration getKeys() {
        return Keys;
    }

    public static FileConfiguration getConfig() {
        return Config;
    }
}
