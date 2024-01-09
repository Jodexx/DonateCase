package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.events.AnimationEndEvent;
import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.gui.CaseGui;
import com.jodexindustries.donatecase.tools.CustomConfig;
import com.jodexindustries.donatecase.tools.StartAnimation;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.jodexindustries.donatecase.dc.Main.*;


public class Case {

    /**
     * List of entities currently in use
     */
    public static List<ArmorStand> listAR = new ArrayList<>();
    /**
     * Active cases
     */
    public static HashMap<Location, String> ActiveCase = new HashMap<>();


    /**
     * Players, who opened cases (open gui)
     */
    public static HashMap<UUID, OpenCase> playerOpensCase = new HashMap<>();

    /**
     * History data massive, key - case name
     */
    public static HashMap<String, HistoryData[]> historyData = new HashMap<>();


    /**
     * Save case location
     * @param caseName Case name (custom)
     * @param type Case type (config)
     * @param lv Case location
     */
    public static void saveLocation(String caseName, String type, Location lv) {
        String location = lv.getWorld().getName() + ";" + lv.getX() + ";" + lv.getY() + ";" + lv.getZ() + ";" + lv.getPitch() + ";" + lv.getYaw();
        customConfig.getCases().set("DonatCase.Cases." + caseName + ".location", location);
        customConfig.getCases().set("DonatCase.Cases." + caseName + ".type", type);
        customConfig.saveCases();
    }

    /**
     * Set case keys to a specific player
     * @param caseName Case name
     * @param player Player name
     * @param keys Number of keys
     */
    public static void setKeys(String caseName, String player, int keys) {
        if (!Main.sql) {
            customConfig.getKeys().set("DonatCase.Cases." + caseName + "." + player, keys == 0 ? null : keys);
            customConfig.saveKeys();
        } else {
            Main.mysql.setKey(caseName, player, keys);
        }

    }

    /**
     * Set null case keys to a specific player
     * @param caseName Case name
     * @param player Player name
     */
    public static void setNullKeys(String caseName, String player) {
        if (!Main.sql) {
            customConfig.getKeys().set("DonatCase.Cases." + caseName + "." + player, 0);
            customConfig.saveKeys();
        } else {
            Main.mysql.setKey(caseName, player, 0);
        }

    }

    /**
     * Add case keys to a specific player
     * @param caseName Case name
     * @param player Player name
     * @param keys Number of keys
     */
    public static void addKeys(String caseName, String player, int keys) {
        setKeys(caseName, player, getKeys(caseName, player) + keys);
    }

    /**
     * Delete case keys for a specific player
     * @param caseName Case name
     * @param player Player name
     * @param keys Number of keys
     */

    public static void removeKeys(String caseName, String player, int keys) {
        setKeys(caseName, player, getKeys(caseName, player) - keys);
    }

    /**
     * Get the keys to a certain player's case
     * @param name Case name
     * @param player Player name
     * @return Number of keys
     */

    public static int getKeys(String name, String player) {
        return Main.sql ? Main.mysql.getKey(name, player) : customConfig.getKeys().getInt("DonatCase.Cases." + name + "." + player);
    }

    /**
     * Delete case by location in Cases.yml
     * @param loc Case location
     */
    public static void deleteCaseByLocation(Location loc) {
        customConfig.getCases().set("DonatCase.Cases." + Case.getCaseNameByLocation(loc), null);
        customConfig.saveCases();
    }

    /**
     * Delete case by name in Cases.yml
     * @param name Case name
     */
    public static void deleteCaseByName(String name) {
        customConfig.getCases().set("DonatCase.Cases." + name, null);
        customConfig.saveCases();
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
                if(hasCaseByName(customConfig.getCases().getString("DonatCase.Cases." + name + ".type"))) {
                    String[] location = customConfig.getCases().getString("DonatCase.Cases." + name + ".location").split(";");
                    World world = Bukkit.getWorld(location[0]);
                    Location temp = new Location(world, Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3]));
                    if (temp.equals(loc)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Get case type by location
     * @param loc Case location
     * @return Case type
     */
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


    /**
     * Get case name by location
     * @param loc Case location
     * @return Case name
     */
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
    /**
     * Is there a case with a name?
     * @param name Case name
     * @return true/false
     */
    public static boolean hasCaseByName(String name) {
        if(casesConfig.getCases().isEmpty()) {
            return false;
        }
        for (String caseName : casesConfig.getCases().keySet()) {
            if(caseName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Are there cases that have been created?
     * @param name Case name
     * @return true/false
     */
    public static boolean hasCaseDataByName(String name) {
        if(customConfig.getCases().getConfigurationSection("DonatCase.Cases") == null) {
            return false;
        } else
            return Objects.requireNonNull(customConfig.getCases().getConfigurationSection("DonatCase.Cases")).contains(name);
    }

    /**
     * Are there cases with a specific title?
     * @param title Case title
     * @return true/false
     */
    public static boolean hasCaseByTitle(String title) {
        for (YamlConfiguration caseConfig : casesConfig.getCases().values()) {
            if(caseConfig.getString("case.Title") == null) {
                return false;
            } else if (Main.t.rc(caseConfig.getString("case.Title")).equalsIgnoreCase(title)) {
                return true;
            }
        }

        return false;
    }
    /**
     * Get all cases in config
     * @return cases
     */
    public static Map<String, YamlConfiguration> getCases() {
        return casesConfig.getCases();
    }

    /**
     * Start animation at a specific location
     * @param player The player who opened the case
     * @param location Location where to start the animation
     * @param caseName Case name
     */
    public static void startAnimation(Player player, Location location, String caseName) {
        new StartAnimation(player, location, caseName);
    }
    /**
     * Get random group from case
     * @param c Case name
     * @return Group name (item name)
     */
    public static String getRandomGroup(String c) {
        return Tools.getRandomGroup(c);
    }
    /**
     * Get win group id (item id)
     * @param c Case name
     * @param winGroup Group name
     * @return Group id
     */
    public static String getWinGroupId(String c, String winGroup) {
        return casesConfig.getCase(c).getString("case.Items." + winGroup + ".Item.ID");
    }
    /**
     * Get win group displayname
     * @param c Case name
     * @param winGroup Group name
     * @return Group displayname
     */
    public static String getWinGroupDisplayName(String c, String winGroup) {
        return casesConfig.getCase(c).getString("case.Items." + winGroup + ".Item.DisplayName");
    }
    /**
     * Get win group enchant (boolean)
     * @param c Case name
     * @param winGroup Group name
     * @return true/false
     */
    public static boolean getWinGroupEnchant(String c, String winGroup) {
        return casesConfig.getCase(c).getBoolean("case.Items." + winGroup + ".Item.Enchanted");
    }

    /**
     * Get win group Rgb (String massive)
     * @param c Case name
     * @param winGroup Group name
     * @return rgb massive with 3 items
     */
    public static String[] getWinGroupRgb(String c, String winGroup) {
        String[] rgb = null;
        String rgbString = casesConfig.getCase(c).getString("case.Items." + winGroup + ".Item.Rgb");
        if(rgbString != null) {
            rgb = rgbString.replaceAll(" ", "").split(",");
        }
        return rgb;
    }
    /**
     * Get plugin instance
     * @return DonateCase instance
     */
    public static JavaPlugin getInstance() {
        return Main.instance;
    }

    /**
     * Animation end method for custom animations is called to completely end the animation
     * @param winGroup Win group
     * @param c Case type
     * @param animation Animation name
     * @param player Player who opened
     * @param location Case location
     */
    public static void animationEnd(String c, String animation, Player player, Location location, String winGroup) {
        AnimationEndEvent animationEndEvent = new AnimationEndEvent(player, animation, c, location, winGroup);
        Bukkit.getServer().getPluginManager().callEvent(animationEndEvent);
        ActiveCase.remove(location.getBlock().getLocation());
    }

    /**
     * Case open finish method for custom animations is called to grant a group, send a message, and more
     * @param caseName Case name
     * @param player Player who opened
     * @param needSound Boolean sound
     * @param winGroup Win group
     */
    public static void onCaseOpenFinish(String caseName, Player player, boolean needSound, String winGroup) {
        String sound;
        String choice = "";
        String winGroupDisplayName = t.rc(casesConfig.getCase(caseName).getString("case.Items." + winGroup + ".Item.DisplayName"));
        String winGroupName = casesConfig.getCase(caseName).getString("case.Items." + winGroup + ".Group");
        String giveType = casesConfig.getCase(caseName).getString("case.Items." + winGroup + ".GiveType", "ONE");
        List<String> actions = casesConfig.getCase(caseName).getStringList("case.Items." + winGroup + ".Actions");

        if (customConfig.getConfig().getBoolean("DonatCase.LevelGroup") && Main.getPermissions() != null) {
            String playergroup = Main.getPermissions().getPrimaryGroup(player).toLowerCase();
            if (!customConfig.getConfig().getConfigurationSection("DonatCase.LevelGroups").contains(playergroup) ||
                    customConfig.getConfig().getInt("DonatCase.LevelGroups." + playergroup) < customConfig.getConfig().getInt("DonatCase.LevelGroups." + winGroupName)) {
                if (giveType.equalsIgnoreCase("ONE")) {
                    executeActions(actions, player, winGroupName, winGroupDisplayName);
                } else {
                    choice = getChoice(caseName, winGroup);
                    executeActions(casesConfig.getCase(caseName).getStringList("case.Items." + winGroup + ".RandomActions." + choice + ".Actions"), player, winGroupName, winGroupDisplayName);
                }
            }
        } else {
            if(giveType.equalsIgnoreCase("ONE")) {
                executeActions(actions, player, winGroupName, winGroupDisplayName);
            } else {
                choice = getChoice(caseName, winGroup);
                executeActions(casesConfig.getCase(caseName).getStringList("case.Items." + winGroup + ".RandomActions." + choice + ".Actions"), player, winGroupName, winGroupDisplayName);
            }
        }
        // Sound
        if (needSound) {
            if (casesConfig.getCase(caseName).getString("case.AnimationSound") != null) {
                sound = casesConfig.getCase(caseName).getString("case.AnimationSound");
                if (sound != null) {
                    player.playSound(player.getLocation(), Sound.valueOf(sound),
                            casesConfig.getCase(caseName).getInt("case.Sound.Volume"),
                            casesConfig.getCase(caseName).getInt("case.Sound.Pitch"));
                }
            }
        }
        HistoryData data = new HistoryData(caseName, player.getName(), System.currentTimeMillis(), winGroup, choice);
        HistoryData[] list = historyData.getOrDefault(caseName, new HistoryData[10]);
        System.arraycopy(list, 0, list, 1, list.length - 1);
        list[0] = data;

        historyData.put(caseName, list);
        for (int i = 0; i < list.length; i++) {
            HistoryData data1 = list[i];
            if(data1 != null) {
                customConfig.getData().set("Data." + caseName + "." + i + ".Player", data1.getPlayerName());
                customConfig.getData().set("Data." + caseName + "." + i + ".Time", data1.getTime());
                customConfig.getData().set("Data." + caseName + "." + i + ".Group", data1.getGroup());
            }
        }

        customConfig.saveData();
    }
    private static String getChoice(String caseName, String winGroup) {
        String endCommand = "";
        Random random = new Random();
        int maxChance = 0;
        int from = 0;
        for (String command : casesConfig.getCase(caseName).getConfigurationSection("case.Items." + winGroup + ".RandomActions").getKeys(false)) {
            maxChance += casesConfig.getCase(caseName).getInt("case.Items." + winGroup + ".RandomActions." + command  + ".Chance");
        }
        int rand = random.nextInt(maxChance);
        for (String command : casesConfig.getCase(caseName).getConfigurationSection("case.Items." + winGroup + ".RandomActions").getKeys(false)) {
            int itemChance = casesConfig.getCase(caseName).getInt("case.Items." + winGroup + ".RandomActions." + command + ".Chance");
            if (from <= rand && rand < from + itemChance) {
                endCommand = command;
                break;
            }
            from += itemChance;
        }
        return endCommand;
    }

    private static void executeActions(List<String> actions, Player player, String winGroupGroup, String winGroupDisplayName) {
        for (String action : actions) {
            if (Main.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                action = PAPISupport.setPlaceholders(player, action);
            }
            action = Main.t.rc(action);
            if (action.startsWith("[command] ")) {
                action = action.replaceFirst("\\[command] ", "");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.t.rt(action, "%player%:" + player.getName(), "%group%:" + winGroupGroup, "%groupdisplayname%:" + winGroupDisplayName));
            }
            if (action.startsWith("[broadcast] ")) {
                action = action.replaceFirst("\\[broadcast] ", "");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(Main.t.rt(action, "%player%:" + player.getName(), "%group%:" + winGroupGroup, "%groupdisplayname%:" + winGroupDisplayName));
                }
            }
            if (action.startsWith("[title] ")) {
                action = action.replaceFirst("\\[title] ", "");
                String[] args = action.split(";");
                String title = "";
                String subTitle = "";
                if(args.length >= 1) {
                    title = args[0];
                    subTitle = args[1];
                }
                player.sendTitle(Main.t.rt(title, "%player%:" + player.getName(), "%group%:" + winGroupGroup, "%groupdisplayname%:" + winGroupDisplayName),
                        Main.t.rt(subTitle, "%player%:" + player.getName(), "%group%:" + winGroupGroup, "%groupdisplayname%:" + winGroupDisplayName));
            }
        }
    }



    /**
     * Get case location (in Cases.yml) by block location
     * @param blockLocation Block location
     * @return case location in Cases.yml (with yaw and pitch)
     */
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

    /** Get plugin configuration manager
     * @return configuration manager instance
     */
    public static @NotNull CustomConfig getCustomConfig() {
        return customConfig;
    }

    /**
     * Get case title
     * @param caseName Case name
     * @return case title
     */
    public static String getCaseTitle(String caseName) {
        return casesConfig.getCase(caseName).getString("case.Title");
    }

    /**
     * Open case gui
     * @param p Player
     * @param caseType Case type
     * @param blockLocation Block location
     */
    public static Inventory openGui(Player p, String caseType, Location blockLocation) {
        Inventory inventory = null;
        if (!Case.playerOpensCase.containsKey(p.getUniqueId())) {
            Case.playerOpensCase.put(p.getUniqueId(), new OpenCase(blockLocation, caseType, p.getUniqueId()));
            inventory = new CaseGui(p, caseType).getInventory();
        } else {
            instance.getLogger().warning("Player already opened case");
        }
        return inventory;
    }

    /**
     * Get tools
     * @return Tools instance
     */
    public static Tools getTools() {
        return t;
    }
}
