package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.StartAnimation;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.jodexindustries.donatecase.dc.Main.customConfig;


public class Case {

    public static List<ArmorStand> listAR = new ArrayList<>();
    public static HashMap<Player, Location> openCase = new HashMap<>();
    public static HashMap<Location, String> ActiveCase = new HashMap<>();

    public static void saveLocation(String name, String type, String lv) {
        customConfig.getCases().set("DonatCase.Cases." + name + ".location", lv);
        customConfig.getCases().set("DonatCase.Cases." + name + ".type", type);
        customConfig.saveCases();
    }

    public static void setKeys(String casename, String player, int keys) {
        if (Main.Tconfig) {
            customConfig.getKeys().set("DonatCase.Cases." + casename + "." + player, keys == 0 ? null : keys);
            customConfig.saveKeys();
        } else {
            Main.mysql.setKey(casename, player, keys);
        }

    }

    public static void setNullKeys(String casename, String player) {
        if (Main.Tconfig) {
            customConfig.getKeys().set("DonatCase.Cases." + casename + "." + player, 0);
            customConfig.saveKeys();
        } else {
            Main.mysql.setKey(casename, player, 0);
        }

    }

    public static void addKeys(String casename, String player, int keys) {
        setKeys(casename, player, getKeys(casename, player) + keys);
    }

    public static void removeKeys(String casename, String player, int keys) {
        setKeys(casename, player, getKeys(casename, player) - keys);
    }

    public static int getKeys(String name, String player) {
        return Main.Tconfig ? customConfig.getKeys().getInt("DonatCase.Cases." + name + "." + player) : Main.mysql.getKey(name, player);
    }

    public static boolean hasCaseByLocation(String loc) {
        ConfigurationSection cases_ = customConfig.getCases().getConfigurationSection("DonatCase.Cases");
        if(cases_ == null) {
            return false;
        }
        for (String name : cases_.getValues(false).keySet()) {
            if(customConfig.getCases().getString("DonatCase.Cases." + name + ".location") == null) {
                return false;
            } else
            if(customConfig.getCases().getString("DonatCase.Cases." + name + ".location").equalsIgnoreCase(loc)) {
                return true;
            }
        }

        return false;
    }

    // get case type by location in Cases.yml
    public static String getCaseTypeByLocation(String loc) {
        ConfigurationSection cases_ = customConfig.getCases().getConfigurationSection("DonatCase.Cases");

        for(String name : cases_.getValues(false).keySet()) {
            if(customConfig.getCases().getString("DonatCase.Cases." + name + ".location") == null) {
                return null;
            } else if (customConfig.getCases().getString("DonatCase.Cases." + name + ".location").equalsIgnoreCase(loc)) {
                return customConfig.getCases().getString("DonatCase.Cases." + name + ".type");
            }
        }
        return null;
    }
    // get case name by location in Cases.yml
    public static String getCaseNameByLocation(String loc) {
        ConfigurationSection cases_ = customConfig.getCases().getConfigurationSection("DonatCase.Cases");
        for (String name : cases_.getValues(false).keySet()) {
            if(customConfig.getCases().getString("DonatCase.Cases." + name + ".location") == null) {
                return null;
            } else if(customConfig.getCases().getString("DonatCase.Cases." + name + ".location").equalsIgnoreCase(loc)) {
                return name;
            }
        }

        return null;
    }
    //has case by name in Config.yml
    public static boolean hasCaseByName(String name) {
        if(customConfig.getConfig().getConfigurationSection("DonatCase.Cases") == null) {
            return false;
        } else
            return Objects.requireNonNull(customConfig.getConfig().getConfigurationSection("DonatCase.Cases")).contains(name);
    }
    // has case data in Cases.yml
    public static boolean hasCaseDataByName(String name) {
        if(customConfig.getCases().getConfigurationSection("DonatCase.Cases") == null) {
            return false;
        } else
            return Objects.requireNonNull(customConfig.getCases().getConfigurationSection("DonatCase.Cases")).contains(name);
    }

    // has case by title in Config.yml
    public static boolean hasCaseByTitle(String title) {
        ConfigurationSection cases_ = customConfig.getConfig().getConfigurationSection("DonatCase.Cases");
        for (String name : cases_.getValues(false).keySet()) {
            if(customConfig.getConfig().getString("DonatCase.Cases." + name + ".Title") == null) {
                return false;
            } else if (Main.t.rc(Objects.requireNonNull(customConfig.getConfig().getString("DonatCase.Cases." + name + ".Title"))).equalsIgnoreCase(title)) {
                return true;
            }
        }

        return false;
    }
    // get case by title in Config.yml
    public static String getCaseByTitle(String title) {
        ConfigurationSection cases_ = customConfig.getConfig().getConfigurationSection("DonatCase.Cases");
        for (String name : cases_.getValues(false).keySet()) {
            if(customConfig.getConfig().getString("DonatCase.Cases." + name + ".Title") == null) {
                return null;
            } else if (Main.t.rc(customConfig.getConfig().getString("DonatCase.Cases." + name + ".Title")).equalsIgnoreCase(title)) {
                return name;
            }
        }
        return null;
    }
    public static void startAnimation(Player player, Location location, String casename) {
        new StartAnimation(player, location, casename);
    }
}
