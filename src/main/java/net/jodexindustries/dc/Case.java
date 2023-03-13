package net.jodexindustries.dc;

import java.util.Iterator;
import net.jodexindustries.tools.CustomConfig;
import org.bukkit.configuration.ConfigurationSection;

public class Case {
    public Case() {
    }

    public static void saveLocation(String name, String type, String lv) {
        CustomConfig.getCases().set("DonatCase.Cases." + name + ".location", lv);
        CustomConfig.getCases().set("DonatCase.Cases." + name + ".type", type);
        CustomConfig.saveCases();
    }

    public static void setKeys(String casename, String player, int keys) {
        player = player.toLowerCase();
        if (DonateCase.Tconfig) {
            CustomConfig.getKeys().set("DonatCase.Cases." + casename + "." + player, keys == 0 ? null : keys);
            CustomConfig.saveKeys();
        } else {
            DonateCase.mysql.setKey(casename, player, keys);
        }

    }

    public static void setNullKeys(String casename, String player) {
        player = player.toLowerCase();
        if (DonateCase.Tconfig) {
            CustomConfig.getKeys().set("DonatCase.Cases." + casename + "." + player, 0);
            CustomConfig.saveKeys();
        } else {
            DonateCase.mysql.setKey(casename, player, 0);
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
        return DonateCase.Tconfig ? CustomConfig.getKeys().getInt("DonatCase.Cases." + name + "." + player) : DonateCase.mysql.getKey(name, player);
    }

    public static boolean hasCaseByLocation(String loc) {
        ConfigurationSection cases_ = CustomConfig.getCases().getConfigurationSection("DonatCase.Cases");
        if(cases_ == null) {
            return false;
        }
        Iterator<String> var2 = cases_.getValues(false).keySet().iterator();

        String name;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            name = var2.next();
        } while(CustomConfig.getCases().getString("DonatCase.Cases." + name + ".location") == null || !CustomConfig.getCases().getString("DonatCase.Cases." + name + ".location").equalsIgnoreCase(loc));

        return true;
    }

    public static String getCaseByLocation(String loc) {
        ConfigurationSection cases_ = CustomConfig.getCases().getConfigurationSection("DonatCase.Cases");
        Iterator var2 = cases_.getValues(false).keySet().iterator();

        String name;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            name = (String)var2.next();
        } while(CustomConfig.getCases().getString("DonatCase.Cases." + name + ".location") == null || !CustomConfig.getCases().getString("DonatCase.Cases." + name + ".location").equalsIgnoreCase(loc));

        return CustomConfig.getCases().getString("DonatCase.Cases." + name + ".type");
    }

    public static String getCaseNameByLocation(String loc) {
        ConfigurationSection cases_ = CustomConfig.getCases().getConfigurationSection("DonatCase.Cases");
        Iterator var2 = cases_.getValues(false).keySet().iterator();

        String name;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            name = (String)var2.next();
        } while(CustomConfig.getCases().getString("DonatCase.Cases." + name + ".location") == null || !CustomConfig.getCases().getString("DonatCase.Cases." + name + ".location").equalsIgnoreCase(loc));

        return name;
    }

    public static boolean hasCaseByName(String name) {
        return CustomConfig.getConfig().getConfigurationSection("DonatCase.Cases").contains(name);
    }

    public static boolean hasCaseDataByName(String name) {
        return CustomConfig.getCases().getConfigurationSection("DonatCase.Cases").contains(name);
    }

    public static boolean hasCaseByTitle(String title) {
        ConfigurationSection cases_ = CustomConfig.getConfig().getConfigurationSection("DonatCase.Cases");
        Iterator var2 = cases_.getValues(false).keySet().iterator();

        String name;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            name = (String)var2.next();
        } while(CustomConfig.getConfig().getString("DonatCase.Cases." + name + ".Title") == null || !DonateCase.t.rc(CustomConfig.getConfig().getString("DonatCase.Cases." + name + ".Title")).equalsIgnoreCase(title));

        return true;
    }

    public static boolean hasCaseNameByTitle(String title) {
        ConfigurationSection cases_ = CustomConfig.getCases().getConfigurationSection("DonatCase.Cases");

        for (String name : cases_.getValues(false).keySet()) {
            if (CustomConfig.getCases().getString("DonatCase.Cases." + name + ".type") != null) {
                String type = CustomConfig.getCases().getString("DonatCase.Cases." + name + ".type");
                if (CustomConfig.getConfig().getString("DonatCase.Cases." + type + ".Title").equalsIgnoreCase(title)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String getCaseByTitle(String title) {
        ConfigurationSection cases_ = CustomConfig.getConfig().getConfigurationSection("DonatCase.Cases");
        Iterator var2 = cases_.getValues(false).keySet().iterator();

        String name;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            name = (String)var2.next();
        } while(CustomConfig.getConfig().getString("DonatCase.Cases." + name + ".Title") == null || !DonateCase.t.rc(CustomConfig.getConfig().getString("DonatCase.Cases." + name + ".Title")).equalsIgnoreCase(title));

        return name;
    }
}
