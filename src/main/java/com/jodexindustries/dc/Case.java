package com.jodexindustries.dc;

import com.jodexindustries.tools.CustomConfig;
import org.bukkit.configuration.ConfigurationSection;

public class Case {

    public static void saveLocation(String name, String type, String lv) {
        CustomConfig.getCases().set("DonatCase.Cases." + name + ".location", lv);
        CustomConfig.getCases().set("DonatCase.Cases." + name + ".type", type);
        CustomConfig.saveCases();
    }

    public static void setKeys(String casename, String player, int keys) {
        player = player.toLowerCase();
        if (Main.Tconfig) {
            CustomConfig.getKeys().set("DonatCase.Cases." + casename + "." + player, keys == 0 ? null : keys);
            CustomConfig.saveKeys();
        } else {
            Main.mysql.setKey(casename, player, keys);
        }

    }

    public static void setNullKeys(String casename, String player) {
        player = player.toLowerCase();
        if (Main.Tconfig) {
            CustomConfig.getKeys().set("DonatCase.Cases." + casename + "." + player, 0);
            CustomConfig.saveKeys();
        } else {
            Main.mysql.setKey(casename, player, 0);
        }

    }

    public static void addKeys(String casename, String player, int keys) {
        setKeys(casename, player, getKeys(casename, player) + keys);
    }

    public static void removeKeys(String casename, String player, int keys) {
        player = player.toLowerCase();
        setKeys(casename, player, getKeys(casename, player) - keys);
    }

    public static int getKeys(String name, String player) {
        player = player.toLowerCase();
        return Main.Tconfig ? CustomConfig.getKeys().getInt("DonatCase.Cases." + name + "." + player) : Main.mysql.getKey(name, player);
    }

    public static boolean hasCaseByLocation(String loc) {
        ConfigurationSection cases_ = CustomConfig.getCases().getConfigurationSection("DonatCase.Cases");
        if(cases_ == null) {
            return false;
        }
        for (String name : cases_.getValues(false).keySet()) {
            if(CustomConfig.getCases().getString("DonatCase.Cases." + name + ".location") == null) {
                return false;
            } else
            if(CustomConfig.getCases().getString("DonatCase.Cases." + name + ".location").equalsIgnoreCase(loc)) {
                return true;
            }
        }

        return false;
    }

    // get case type by location in Cases.yml
    public static String getCaseTypeByLocation(String loc) {
        ConfigurationSection cases_ = CustomConfig.getCases().getConfigurationSection("DonatCase.Cases");

        for(String name : cases_.getValues(false).keySet()) {
            if(CustomConfig.getCases().getString("DonatCase.Cases." + name + ".location") == null) {
                return null;
            } else if (CustomConfig.getCases().getString("DonatCase.Cases." + name + ".location").equalsIgnoreCase(loc)) {
                return CustomConfig.getCases().getString("DonatCase.Cases." + name + ".type");
            }
        }
        return null;
    }
    // get case name by location in Cases.yml
    public static String getCaseNameByLocation(String loc) {
        ConfigurationSection cases_ = CustomConfig.getCases().getConfigurationSection("DonatCase.Cases");
        for (String name : cases_.getValues(false).keySet()) {
            if(CustomConfig.getCases().getString("DonatCase.Cases." + name + ".location") == null) {
                return null;
            } else if(CustomConfig.getCases().getString("DonatCase.Cases." + name + ".location").equalsIgnoreCase(loc)) {
                return name;
            }
        }

        return null;
    }
    //has case by name in Config.yml
    public static boolean hasCaseByName(String name) {
        if(CustomConfig.getConfig().getConfigurationSection("DonatCase.Cases") == null) {
            return false;
        } else
        return CustomConfig.getConfig().getConfigurationSection("DonatCase.Cases").contains(name);
    }
    // has case data in Cases.yml
    public static boolean hasCaseDataByName(String name) {
        if(CustomConfig.getCases().getConfigurationSection("DonatCase.Cases") == null) {
            return false;
        } else
        return CustomConfig.getCases().getConfigurationSection("DonatCase.Cases").contains(name);
    }

    // has case by title in Config.yml
    public static boolean hasCaseByTitle(String title) {
        ConfigurationSection cases_ = CustomConfig.getConfig().getConfigurationSection("DonatCase.Cases");
        for (String name : cases_.getValues(false).keySet()) {
            if(CustomConfig.getConfig().getString("DonatCase.Cases." + name + ".Title") == null) {
                return false;
            } else if (Main.t.rc(CustomConfig.getConfig().getString("DonatCase.Cases." + name + ".Title")).equalsIgnoreCase(title)) {
                return true;
            }
        }

        return false;
    }
    // get case by title in Config.yml
    public static String getCaseByTitle(String title) {
        ConfigurationSection cases_ = CustomConfig.getConfig().getConfigurationSection("DonatCase.Cases");
        for (String name : cases_.getValues(false).keySet()) {
            if(CustomConfig.getConfig().getString("DonatCase.Cases." + name + ".Title") == null) {
                return null;
            } else if (Main.t.rc(CustomConfig.getConfig().getString("DonatCase.Cases." + name + ".Title")).equalsIgnoreCase(title)) {
                return name;
            }
        }
        return null;
    }
}
