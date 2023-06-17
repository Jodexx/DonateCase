package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.StartAnimation;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static com.jodexindustries.donatecase.dc.Main.customConfig;


public class Case {

    /**
     * List of entities currently in use
     */
    public static List<ArmorStand> listAR = new ArrayList<>();
    /**
     * Open cases (active)
     */
    public static HashMap<Player, Location> openCase = new HashMap<>();
    /**
     * Active cases
     */
    public static HashMap<Location, String> ActiveCase = new HashMap<>();

    /**
     * Players, who opened cases (started scrolling)
     */
    public static List<Player> caseOpen = new ArrayList<>();

    public static void saveLocation(String name, String type, Location lv) {
        String location = lv.getWorld().getName() + ";" + lv.getX() + ";" + lv.getY() + ";" + lv.getZ() + ";" + lv.getPitch() + ";" + lv.getYaw();
        customConfig.getCases().set("DonatCase.Cases." + name + ".location", location);
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

    public static boolean hasCaseByLocation(Location loc) {
        ConfigurationSection cases_ = customConfig.getCases().getConfigurationSection("DonatCase.Cases");
        if(cases_ == null) {
            return false;
        }
        for (String name : cases_.getValues(false).keySet()) {
            if(customConfig.getCases().getString("DonatCase.Cases." + name + ".location") == null) {
                return false;
            } else {
                String[] location = customConfig.getCases().getString("DonatCase.Cases." + name + ".location").split(";");
                World world = Bukkit.getWorld(location[0]);
                Location temp = new Location(world, Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3]));
                if(temp.equals(loc)) {
                    return true;
                }
            }
        }

        return false;
    }

    // get case type by location in Cases.yml
    public static String getCaseTypeByLocation(Location loc) {
        ConfigurationSection cases_ = customConfig.getCases().getConfigurationSection("DonatCase.Cases");

        for(String name : cases_.getValues(false).keySet()) {
            if(customConfig.getCases().getString("DonatCase.Cases." + name + ".location") == null) {
                return null;
            } else {
                String[] location = customConfig.getCases().getString("DonatCase.Cases." + name + ".location").split(";");
                World world = Bukkit.getWorld(location[0]);
                Location temp = new Location(world, Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3]));
                if (temp.equals(loc)) {
                    return customConfig.getCases().getString("DonatCase.Cases." + name + ".type");
                }
            }
        }
        return null;
    }

    public static Location getCaseLocationByBlockLocation(Location blockLocation) {
        ConfigurationSection cases_ = customConfig.getCases().getConfigurationSection("DonatCase.Cases");

        for(String name : cases_.getValues(false).keySet()) {
            if(customConfig.getCases().getString("DonatCase.Cases." + name + ".location") == null) {
                return null;
            } else {
                String[] location = customConfig.getCases().getString("DonatCase.Cases." + name + ".location").split(";");
                World world = Bukkit.getWorld(location[0]);
                Location temp = new Location(world, Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3]));
                if (temp.equals(blockLocation)) {
                    Location result = temp.clone();
                    result.setPitch(Float.parseFloat(location[4]));
                    result.setYaw(Float.parseFloat(location[5]));
                    return result;
                }
            }
        }
        return null;
    }

    // get case name by location in Cases.yml
    public static String getCaseNameByLocation(Location loc) {
        ConfigurationSection cases_ = customConfig.getCases().getConfigurationSection("DonatCase.Cases");
        for (String name : cases_.getValues(false).keySet()) {
            if(customConfig.getCases().getString("DonatCase.Cases." + name + ".location") == null) {
                return null;
            } else {
                String[] location = customConfig.getCases().getString("DonatCase.Cases." + name + ".location").split(";");
                World world = Bukkit.getWorld(location[0]);
                Location temp = new Location(world, Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3]));
                if (temp.equals(loc)) {
                    return name;
                }
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

    public static String getRandomGroup(String c) {
        return Tools.getRandomGroup(c);
    }

    public static String getWinGroupId(String c, String winGroup) {
        return customConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup + ".Item.ID").toUpperCase();
    }
    public static String getWinGroupDisplayName(String c, String winGroup) {
        return customConfig.getConfig().getString("DonatCase.Cases." + c + ".Items." + winGroup + ".Item.DisplayName");
    }
    public static boolean getWinGroupEnchant(String c, String winGroup) {
        return customConfig.getConfig().getBoolean("DonatCase.Cases." + c + ".Items." + winGroup + ".Item.Enchanted");
    }
    public static JavaPlugin getInstance() {
        return Main.instance;
    }

    public static void onCaseOpenFinish(String casename, Player player, boolean needsound, String winGroup) {
        String sound;
        String casetitle = customConfig.getConfig().getString("DonatCase.Cases." + casename + ".Title");
        String winGroupDisplayName = customConfig.getConfig().getString("DonatCase.Cases." + casename + ".Items." + winGroup + ".Item.DisplayName");
        String winGroupGroup = customConfig.getConfig().getString("DonatCase.Cases." + casename + ".Items." + winGroup + ".Group");
        // Give command
        String givecommand = customConfig.getConfig().getString("DonatCase.Cases." + casename + ".Items." + winGroup + ".GiveCommand");
        if (customConfig.getConfig().getBoolean("DonatCase.LevelGroup")) {
            String playergroup = Main.getPermissions().getPrimaryGroup(player).toLowerCase();
            if (customConfig.getConfig().getConfigurationSection("DonatCase.LevelGroups").contains(playergroup) &&
                    customConfig.getConfig().getInt("DonatCase.LevelGroups." + playergroup) >=
                            customConfig.getConfig().getInt("DonatCase.LevelGroups." + winGroupGroup)) {
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.t.rt(givecommand, "%player:" + player.getName(), "%group:" + winGroupGroup));
            }
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.t.rt(givecommand, "%player:" + player.getName(), "%group:" + winGroupGroup));
        }
        // Custom commands
        for (String command : customConfig.getConfig().getStringList("DonatCase.Cases." + casename + ".Items." + winGroup + ".Commands")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.t.rt(command, "%player:" + player.getName(), "%group:" + winGroupGroup));
        }
        // Sound
        if (needsound) {
            if (customConfig.getConfig().getString("DonatCase.Cases." + casename + ".AnimationSound") != null) {
                sound = customConfig.getConfig().getString("DonatCase.Cases." + casename + ".AnimationSound");
                if (sound != null) {
                    player.playSound(player.getLocation(), Sound.valueOf(sound),
                            customConfig.getConfig().getInt("DonatCase.Cases." + casename + ".Sound.Volume"),
                            customConfig.getConfig().getInt("DonatCase.Cases." + casename + ".Sound.Pitch"));
                }
            }
        }
        // Title && SubTitle
        String title;
        if (customConfig.getConfig().getString("DonatCase.Cases." + casename + ".Item." + winGroup + ".Title") != null) {

            title = Main.t.rc(Main.t.rt(customConfig.getConfig().getString("DonatCase.Cases." + casename + ".Item." + winGroup + ".Title"),
                    "%groupdisplayname:" + winGroupDisplayName, "%group:" + winGroup));
        } else {
            title = "";
        }
        String subtitle;
        if (customConfig.getConfig().getString("DonatCase.Cases." + casename + ".Item." + winGroup + ".Title") != null) {
            subtitle = Main.t.rc(Main.t.rt(customConfig.getConfig().getString("DonatCase.Cases." + casename + ".Item." + winGroup + ".SubTitle"),
                    "%groupdisplayname:" + winGroupDisplayName, "%group:" + winGroup));
        } else {
            subtitle = "";
        }
        player.sendTitle(title, subtitle, 5, 60, 5);

        // Broadcast
        for (String cmd2 : customConfig.getConfig().getStringList("DonatCase.Cases." + casename + ".Items." + winGroup + ".Broadcast")) {
            Bukkit.broadcastMessage(Main.t.rc(Main.t.rt(cmd2, "%player:" + player.getName(), "%group:" + winGroupDisplayName, "%case:" + casetitle)));
        }
    }
}
