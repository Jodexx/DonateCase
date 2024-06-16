package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.PlayerOpenCase;
import com.jodexindustries.donatecase.api.data.PermissionDriver;
import com.jodexindustries.donatecase.api.events.AnimationEndEvent;
import com.jodexindustries.donatecase.gui.CaseGui;
import com.jodexindustries.donatecase.tools.CasesConfig;
import com.jodexindustries.donatecase.tools.CustomConfig;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jodexindustries.donatecase.DonateCase.*;


/**
 * The main class for API interaction with DonateCase, this is where most of the functions are located.
 */ 
public class Case{
    /**
     * Active cases
     */
    public static HashMap<UUID, ActiveCase> activeCases = new HashMap<>();

    /**
     * Active cases, but by location
     */
    public static HashMap<Location, UUID> activeCasesByLocation = new HashMap<>();


    /**
     * Players, who opened cases (open gui)
     */
    public static HashMap<UUID, PlayerOpenCase> playersGui = new HashMap<>();

    /**
     * Loaded cases in runtime
     */
    public static HashMap<String, CaseData> caseData = new HashMap<>();

    /**
     * Save case location
     * @param caseName Case name (custom)
     * @param type Case type (config)
     * @param lv Case location
     */
    public static void saveLocation(String caseName, String type, Location lv) {
        CaseData c = getCase(type);
        if(lv.getWorld() == null) {
            instance.getLogger().warning("Error with saving location: world not found!");
            return;
        }
        if(CaseManager.getHologramManager() != null && (c != null && c.getHologram().isEnabled())) CaseManager.getHologramManager().createHologram(lv.getBlock(), c);
        String location = lv.getWorld().getName() + ";" + lv.getX() + ";" + lv.getY() + ";" + lv.getZ() + ";" + lv.getPitch() + ";" + lv.getYaw();
        getCustomConfig().getCases().set("DonatCase.Cases." + caseName + ".location", location);
        getCustomConfig().getCases().set("DonatCase.Cases." + caseName + ".type", type);
        getCustomConfig().saveCases();
    }

    /**
     * Set case keys to a specific player
     * @param caseType Case type
     * @param player Player name
     * @param keys Number of keys
     */
    public static void setKeys(String caseType, String player, int keys) {
        if (!instance.sql) {
            getCustomConfig().getKeys().set("DonatCase.Cases." + caseType + "." + player, keys == 0 ? null : keys);
            getCustomConfig().saveKeys();
        } else {
            if(instance.mysql != null) instance.mysql.setKey(caseType, player, keys);
        }

    }

    /**
     * Set null case keys to a specific player
     * @param caseType Case type
     * @param player Player name
     */
    public static void setNullKeys(String caseType, String player) {
        if (!instance.sql) {
            getCustomConfig().getKeys().set("DonatCase.Cases." + caseType + "." + player, 0);
            getCustomConfig().saveKeys();
        } else {
            if(instance.mysql != null) instance.mysql.setKey(caseType, player, 0);
        }

    }

    /**
     * Add case keys to a specific player
     * @param caseType Case type
     * @param player Player name
     * @param keys Number of keys
     */
    public static void addKeys(String caseType, String player, int keys) {
        setKeys(caseType, player, getKeys(caseType, player) + keys);
    }

    /**
     * Delete case keys for a specific player
     * @param caseType Case name
     * @param player Player name
     * @param keys Number of keys
     */

    public static void removeKeys(String caseType, String player, int keys) {
        setKeys(caseType, player, getKeys(caseType, player) - keys);
    }

    /**
     * Get the keys to a certain player's case
     * @param caseType Case caseType
     * @param player Player name
     * @return Number of keys
     */

    public static int getKeys(String caseType, String player) {
        return instance.sql ? (instance.mysql == null ? 0 : instance.mysql.getKey(caseType, player)) : getCustomConfig().getKeys().getInt("DonatCase.Cases." + caseType + "." + player);
    }

    /**
     * Delete case by location in Cases.yml
     * @param loc Case location
     */
    public static void deleteCaseByLocation(Location loc) {
        getCustomConfig().getCases().set("DonatCase.Cases." + getCaseCustomNameByLocation(loc), null);
        getCustomConfig().saveCases();
    }

    /**
     * Delete case by name in Cases.yml
     * @param name Case name
     */
    public static void deleteCaseByName(String name) {
        getCustomConfig().getCases().set("DonatCase.Cases." + name, null);
        getCustomConfig().saveCases();
    }

    /**
     * Check if case has by location
     * @param loc Case location
     * @return Boolean
     */

    public static boolean hasCaseByLocation(Location loc) {
        ConfigurationSection cases_ = getCustomConfig().getCases().getConfigurationSection("DonatCase.Cases");
        if(cases_ == null) {
            return false;
        }
        for (String name : cases_.getValues(false).keySet()) {
            ConfigurationSection caseSection = getCustomConfig().getCases().getConfigurationSection("DonatCase.Cases." + name);
            if(caseSection == null || caseSection.getString("location") == null) {
                return false;
            } else {
                String type = caseSection.getString("type");
                String location = caseSection.getString("location");
                if(hasCaseByType(type) && location != null) {
                    String[] worldLocation = location.split(";");
                    World world = Bukkit.getWorld(worldLocation[0]);
                    Location temp = new Location(world, Double.parseDouble(worldLocation[1]), Double.parseDouble(worldLocation[2]), Double.parseDouble(worldLocation[3]));
                    if (temp.equals(loc)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Get case information by location
     * @param loc Case location
     * @param infoType Information type ("type" or "name")
     * @return Case information
     */
    private static String getCaseInfoByLocation(Location loc, String infoType) {
        ConfigurationSection casesSection = getCustomConfig().getCases().getConfigurationSection("DonatCase.Cases");
        if (casesSection == null) return null;

        for (String name : casesSection.getValues(false).keySet()) {
            ConfigurationSection caseSection = casesSection.getConfigurationSection(name);
            if (caseSection == null) continue;

            String location = caseSection.getString("location");
            if (location == null) continue;

            String[] worldLocation = location.split(";");
            World world = Bukkit.getWorld(worldLocation[0]);
            Location temp = new Location(world, Double.parseDouble(worldLocation[1]), Double.parseDouble(worldLocation[2]), Double.parseDouble(worldLocation[3]));

            if (temp.equals(loc)) {
                return infoType.equals("type") ? caseSection.getString("type") : name;
            }
        }
        return null;
    }

    /**
     * Get case type by location
     * @param loc Case location
     * @return Case type
     */
    public static String getCaseTypeByLocation(Location loc) {
        return getCaseInfoByLocation(loc, "type");
    }

    /**
     * Get case name by location
     * @param loc Case location
     * @return Case name
     */
    public static String getCaseCustomNameByLocation(Location loc) {
        return getCaseInfoByLocation(loc, "name");
    }

    /**
     * Is there a case with a type?
     * @param caseType Case type
     */
    public static boolean hasCaseByType(String caseType) {
        if(caseData.isEmpty()) {
            return false;
        }
        return caseData.containsKey(caseType);
    }
    /**
     * Are there cases that have been created?
     * @param name Case name
     * @return true/false
     */
    public static boolean hasCaseTypeByCustomName(String name) {
        if(getCustomConfig().getCases().getConfigurationSection("DonatCase.Cases") == null) {
            return false;
        } else
            return Objects.requireNonNull(getCustomConfig().getCases().getConfigurationSection("DonatCase.Cases")).contains(name);
    }

    /**
     * Are there cases with a specific title?
     * @param title Case title
     * @return true/false
     */
    public static boolean hasCaseByTitle(String title) {
        for (CaseData data : caseData.values()) {
            if(data.getCaseTitle().equalsIgnoreCase(title)) return true;
        }

        return false;
    }
    /**
     * Get all cases in "cases" folder
     * @return cases
     * @deprecated
     * <p> Use {@link Case#getCasesConfig()} instead</p>
     */
    @Deprecated
    public static Map<String, YamlConfiguration> getCases() {
        return instance.casesConfig.getCases();
    }

    /**
     * Get cases config instance
     * Used for loading cases folder
     */
    public static CasesConfig getCasesConfig() {
        return instance.casesConfig;
    }

    /**
     * Get random group from case
     * @param c Case data
     * @return Item data
     */
    public static CaseData.Item getRandomItem(CaseData c) {
        return Tools.getRandomGroup(c);
    }

    /**
     * Get plugin instance
     * @return DonateCase instance
     */
    public static DonateCase getInstance() {
        return instance;
    }

    /**
     * Animation end method for custom animations is called to completely end the animation
     * @param item Item data
     * @param caseData Case data
     * @param player Player who opened
     * @param uuid Active case uuid
     */
    public static void animationEnd(CaseData caseData, Player player, UUID uuid, CaseData.Item item) {
        ActiveCase activeCase = activeCases.get(uuid);
        Location location = activeCase.getLocation();
        activeCasesByLocation.remove(location.getBlock().getLocation());
        activeCases.remove(uuid);
        if(CaseManager.getHologramManager() != null && caseData.getHologram().isEnabled()) {
            CaseManager.getHologramManager().createHologram(location.getBlock(), caseData);
        }
        AnimationEndEvent animationEndEvent = new AnimationEndEvent(player, caseData.getAnimation(), caseData, location, item);
        Bukkit.getServer().getPluginManager().callEvent(animationEndEvent);
    }

    /**
     * Animation pre end method for custom animations is called to grant a group, send a message, and more
     * @param caseData Case data
     * @param player Player who opened
     * @param needSound Boolean sound
     * @param item Win item data
     */
    public static void animationPreEnd(CaseData caseData, Player player, boolean needSound, CaseData.Item item) {
        String choice = "";
        Map<String, Integer> levelGroups = getDefaultLevelGroup();
        if(!caseData.getLevelGroups().isEmpty()) levelGroups = caseData.getLevelGroups();
        String playerGroup = getPlayerGroup(player);
        if(isAlternative(levelGroups, playerGroup, item.getGroup())) {
            executeActions(player, caseData, item, null, true);
        } else {
            if (item.getGiveType().equalsIgnoreCase("ONE")) {
                executeActions(player, caseData, item, null, false);
            } else {
                choice = getChoice(item);
                executeActions(player, caseData, item, choice, false);
            }
        }

        // Sound
        if (needSound) {
            if (caseData.getAnimationSound() != null) {
                player.playSound(player.getLocation(), caseData.getAnimationSound().getSound(),
                        caseData.getAnimationSound().getVolume(),
                        caseData.getAnimationSound().getPitch());
            }
        }
        CaseData.HistoryData data = new CaseData.HistoryData(item.getItemName(), caseData.getCaseType(), player.getName(), System.currentTimeMillis(), item.getGroup(), choice);
        CaseData.HistoryData[] list = caseData.getHistoryData();
        System.arraycopy(list, 0, list, 1, list.length - 1);
        list[0] = data;

        for (int i = 0; i < list.length; i++) {
            CaseData.HistoryData tempData = list[i];
            if(tempData != null) {
                if(!instance.sql) {
                    getCustomConfig().getData().set("Data." + caseData.getCaseType() + "." + i + ".Player", tempData.getPlayerName());
                    getCustomConfig().getData().set("Data." + caseData.getCaseType() + "." + i + ".Time", tempData.getTime());
                    getCustomConfig().getData().set("Data." + caseData.getCaseType() + "." + i + ".Group", tempData.getGroup());
                    getCustomConfig().getData().set("Data." + caseData.getCaseType() + "." + i + ".Item", tempData.getItem());
                    getCustomConfig().getData().set("Data." + caseData.getCaseType() + "." + i + ".Action", tempData.getAction());
                } else {
                    if(instance.mysql != null) instance.mysql.setHistoryData(caseData.getCaseType(), i, tempData);
                }
            }
        }
        CaseData finalCase = getCase(caseData.getCaseType());
        if(finalCase != null) finalCase.setHistoryData(list);

        getCustomConfig().saveData();
    }
    private static String getChoice(CaseData.Item item) {
        String endCommand = "";
        Random random = new Random();
        int maxChance = 0;
        int from = 0;
        for (String command : item.getRandomActions().keySet()) {
            CaseData.Item.RandomAction randomAction = item.getRandomAction(command);
            if(randomAction == null) continue;
            maxChance += randomAction.getChance();
        }
        int rand = random.nextInt(maxChance);
        for (String command : item.getRandomActions().keySet()) {
            CaseData.Item.RandomAction randomAction = item.getRandomAction(command);
            if(randomAction == null) continue;
            int itemChance = randomAction.getChance();
            if (from <= rand && rand < from + itemChance) {
                endCommand = command;
                break;
            }
            from += itemChance;
        }
        return endCommand;
    }

    private static void executeActions(OfflinePlayer player, CaseData caseData, CaseData.Item item, String choice, boolean alternative) {
        final String[] replacementRegex = {
                "%player%:" + player.getName(),
                "%casename%:" + caseData.getCaseType(), "%casedisplayname%:" + caseData.getCaseDisplayName(), "%casetitle%:" + caseData.getCaseTitle(),
                "%group%:" + item.getGroup(), "%groupdisplayname%:" + item.getMaterial().getDisplayName()
        };
        List<String> actions = alternative ? item.getAlternativeActions() : item.getActions();
        if(choice != null) {
            CaseData.Item.RandomAction randomAction = item.getRandomAction(choice);
            if(randomAction != null) actions = randomAction.getActions();
        }
        for (String action : actions) {
            if (instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                action = PAPISupport.setPlaceholders(player, action);
            }
            action = Tools.rc(action);
            int cooldown = 0;
            Pattern pattern = Pattern.compile("\\[cooldown:(.*?)]");
            Matcher matcher = pattern.matcher(action);
            if (matcher.find()) {
                action = action.replaceFirst("\\[cooldown:(.*?)]", "").trim();
                cooldown = Integer.parseInt(matcher.group(1));
            }
            if (action.startsWith("[command] ")) {
                action = action.replaceFirst("\\[command] ", "");
                String finalAction = action;
                Bukkit.getScheduler().runTaskLater(instance, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        Tools.rt(finalAction, replacementRegex)), 20L * cooldown);
            }
            if (action.startsWith("[message] ")) {
                action = action.replaceFirst("\\[message] ", "");
                String finalAction = action;
                Bukkit.getScheduler().runTaskLater(instance, () -> {
                    if (player.getPlayer() != null) {
                        player.getPlayer().sendMessage(
                                Tools.rt(finalAction, replacementRegex));
                    }
                }, 20L * cooldown);
            }

            if (action.startsWith("[broadcast] ")) {
                action = action.replaceFirst("\\[broadcast] ", "");
                String finalAction = action;
                Bukkit.getScheduler().runTaskLater(instance, () -> {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(
                                Tools.rt(finalAction, replacementRegex));
                    }
                }, 20L * cooldown);
            }
            if (action.startsWith("[title] ")) {
                action = action.replaceFirst("\\[title] ", "");
                String[] args = action.split(";");
                String title = args.length > 0 ? args[0] : "";
                String subTitle = args.length > 1 ? args[1] : "";
                Bukkit.getScheduler().runTaskLater(instance, () -> {
                    if (player.getPlayer() != null) {
                        player.getPlayer().sendTitle(
                                Tools.rt(title, replacementRegex),
                                Tools.rt(subTitle, replacementRegex), 10, 70, 20);
                    }
                }, 20L * cooldown);
            }
        }
    }



    /**
     * Get case location (in Cases.yml) by block location
     * @param blockLocation Block location
     * @return case location in Cases.yml (with yaw and pitch)
     */
    public static Location getCaseLocationByBlockLocation(Location blockLocation) {
        ConfigurationSection casesSection = getCustomConfig().getCases().getConfigurationSection("DonatCase.Cases");
        if(casesSection == null) return null;

        for(String name : casesSection.getValues(false).keySet()) {
            ConfigurationSection caseSection = casesSection.getConfigurationSection(name);
            if (caseSection == null) continue;
            String location = caseSection.getString("location");
            if (location == null) continue;
            String[] worldLocation = location.split(";");
            World world = Bukkit.getWorld(worldLocation[0]);
            Location temp = new Location(world, Double.parseDouble(worldLocation[1]), Double.parseDouble(worldLocation[2]), Double.parseDouble(worldLocation[3]));
            if (temp.equals(blockLocation)) {
                Location result = temp.clone();
                result.setPitch(Float.parseFloat(worldLocation[4]));
                result.setYaw(Float.parseFloat(worldLocation[5]));
                return result;
            }
        }
        return null;
    }

    /** Get plugin configuration manager
     * @return configuration manager instance
     */
    public static @NotNull CustomConfig getCustomConfig() {
        return getInstance().customConfig;
    }

    /**
     * Open case gui
     * @param p Player
     * @param caseData Case type
     * @param blockLocation Block location
     */
    public static Inventory openGui(Player p, CaseData caseData, Location blockLocation) {
        Inventory inventory = null;
        if (!playersGui.containsKey(p.getUniqueId())) {
            playersGui.put(p.getUniqueId(), new PlayerOpenCase(blockLocation, caseData.getCaseType(), p.getUniqueId()));
            inventory = new CaseGui(p, caseData).getInventory();
        } else {
            instance.getLogger().warning("Player " + p.getName() + " already opened case!");
        }
        return inventory;
    }

    /**
     * Is there a case with a name?
     * @param c Case name
     * @return Boolean
     */
    @Deprecated
    public static boolean hasCase(@NotNull String c) {
        return caseData.containsKey(c);
    }

    /**
     * Get a case with the name
     * @param c Case name
     * @return Case data
     */
    @Nullable
    public static CaseData getCase(@NotNull String c) {
        return caseData.getOrDefault(c, null);
    }

    /**
     * Get sorted history data from all cases
     * @return list of HistoryData (sorted by time)
     */
    public static List<CaseData.HistoryData> getSortedHistoryData() {
        if(!instance.sql) {
            return caseData.values().stream()
                    .filter(Objects::nonNull)
                    .flatMap(data -> {
                        CaseData.HistoryData[] historyData = data.getHistoryData();
                        return historyData != null ? Arrays.stream(historyData) : Stream.empty();
                    })
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingLong(CaseData.HistoryData::getTime).reversed())
                    .collect(Collectors.toList());
        } else {
            if(instance.mysql == null) return new ArrayList<>();
            return instance.mysql.getHistoryData().stream().filter(Objects::nonNull)
                    .sorted(Comparator.comparingLong(CaseData.HistoryData::getTime).reversed())
                    .collect(Collectors.toList());
        }
    }

    /**
     * Get sorted history data by case
     * @param historyData HistoryData from all cases (or not all)
     * @param caseType type of case for filtering
     * @return list of case HistoryData
     */
    public static List<CaseData.HistoryData> sortHistoryDataByCase(List<CaseData.HistoryData> historyData, String caseType) {
        return historyData.stream().filter(Objects::nonNull)
                .filter(data -> data.getCaseType().equals(caseType))
                .sorted(Comparator.comparingLong(CaseData.HistoryData::getTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get case location by custom name (/dc create (type) (customname)
     * @param name Case custom name
     * @return Case name
     */
    @Nullable
    public static Location getCaseLocationByCustomName(String name) {
        String location = getCustomConfig().getCases().getString("DonatCase.Cases." + name + ".location");
        if (location == null) return null;
        String[] worldLocation = location.split(";");
        World world = Bukkit.getWorld(worldLocation[0]);
        return new Location(world, Double.parseDouble(worldLocation[1]), Double.parseDouble(worldLocation[2]), Double.parseDouble(worldLocation[3]));
    }

    /**
     * Get case type by custom name
     * @param name Case custom name
     * @return case type
     */
    public static String getCaseTypeByCustomName(String name) {
        return getCustomConfig().getCases().getString("DonatCase.Cases." + name + ".type");
    }

    /**
     * Get player primary group from Vault or LuckPerms
     * @param player Bukkit player
     * @return player primary group
     */
    public static String getPlayerGroup(Player player) {
        String group = null;
        if(instance.permissionDriver == PermissionDriver.vault) if(instance.permission != null) group = instance.permission.getPrimaryGroup(player);
        if(instance.permissionDriver == PermissionDriver.luckperms) if(instance.luckPerms != null) group = instance.luckPerms.getPlayerAdapter(Player.class).getUser(player).getPrimaryGroup();
        return group;
    }

    /**
     * Get map of default LevelGroup from Config.yml
     * @return map of LevelGroup
     */
    public static Map<String, Integer> getDefaultLevelGroup() {
        Map<String, Integer> levelGroup = new HashMap<>();
        boolean isEnabled = getCustomConfig().getConfig().getBoolean("DonatCase.LevelGroup");
        if(isEnabled) {
            ConfigurationSection section = getCustomConfig().getConfig().getConfigurationSection("DonatCase.LevelGroups");
            if (section != null) {
                for (String group : section.getKeys(false)) {
                    int level = section.getInt(group);
                    levelGroup.put(group, level);
                }
            }
        }
        return levelGroup;
    }

    /**
     * Check for alternative actions
     * @param levelGroups map of LevelGroups (can be from case config or default Config.yml)
     * @param playerGroup player primary group
     * @param winGroup player win group
     * @return boolean
     */
    public static boolean isAlternative(Map<String, Integer> levelGroups, String playerGroup, String winGroup) {
        if(levelGroups.containsKey(playerGroup) && levelGroups.containsKey(winGroup)) {
            int playerGroupLevel = levelGroups.get(playerGroup);
            int winGroupLevel = levelGroups.get(winGroup);
            return playerGroupLevel >= winGroupLevel;
        }
        return false;
    }
}
