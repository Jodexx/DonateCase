package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.PlayerOpenCase;
import com.jodexindustries.donatecase.api.events.AnimationEndEvent;
import com.jodexindustries.donatecase.api.holograms.HologramManager;
import com.jodexindustries.donatecase.tools.CustomConfig;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;


/**
 * The main class for API interaction with DonateCase, this is where most of the functions are located.
 */
public class CaseAPI {
    private final AddonManager addonManager;
    private final AnimationManager animationManager;
    private final SubCommandManager subCommandManager;
    private final Addon addon;
    public CaseAPI(Addon addon) {
        this.addon = addon;
        addonManager = new AddonManager(addon);
        subCommandManager = new SubCommandManager(addon);
        this.animationManager = new AnimationManager(addon);
    }
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
    public void saveLocation(String caseName, String type, Location lv) {}

    /**
     * Set case keys to a specific player
     * @param caseName Case name
     * @param player Player name
     * @param keys Number of keys
     */
    public void setKeys(String caseName, String player, int keys) {
    }

    /**
     * Set null case keys to a specific player
     * @param caseName Case name
     * @param player Player name
     */
    public void setNullKeys(String caseName, String player) {
    }

    /**
     * Add case keys to a specific player
     * @param caseName Case name
     * @param player Player name
     * @param keys Number of keys
     */
    public void addKeys(String caseName, String player, int keys) {
        setKeys(caseName, player, getKeys(caseName, player) + keys);
    }

    /**
     * Delete case keys for a specific player
     * @param caseName Case name
     * @param player Player name
     * @param keys Number of keys
     */

    public void removeKeys(String caseName, String player, int keys) {
        setKeys(caseName, player, getKeys(caseName, player) - keys);
    }

    /**
     * Get the keys to a certain player's case
     * @param name Case name
     * @param player Player name
     * @return Number of keys
     */

    public int getKeys(String name, String player) {
        return getKeys(name, player);
    }

    /**
     * Delete case by location in Cases.yml
     * @param loc Case location
     */
    public void deleteCaseByLocation(Location loc) {
    }

    /**
     * Delete case by name in Cases.yml
     * @param name Case name
     */
    public void deleteCaseByName(String name) {
    }

    /**
     * Check if case has by location
     * @param loc Case location
     * @return Boolean
     */

    public boolean hasCaseByLocation(Location loc) {
        return hasCaseByLocation(loc);
    }

    /**
     * Get case type by location
     * @param loc Case location
     * @return Case type
     */
    public String getCaseTypeByLocation(Location loc) {
        return getCaseTypeByLocation(loc);
    }


    /**
     * Get case name by location
     * @param loc Case location
     * @return Case name
     */
    public String getCaseCustomNameByLocation(Location loc) {
        return null;
    }
    /**
     * Is there a case with a name?
     * @param name Case name
     * @return true/false
     */
    public boolean hasCaseByType(String name) {
        if(caseData.isEmpty()) {
            return false;
        }
        return caseData.containsKey(name);
    }
    /**
     * Are there cases that have been created?
     * @param name Case name
     * @return true/false
     */
    public boolean hasCaseTypeByCustomName(String name) {
        return hasCaseTypeByCustomName(name);
    }

    /**
     * Are there cases with a specific title?
     * @param title Case title
     * @return true/false
     */
    public boolean hasCaseByTitle(String title) {
        for (CaseData data : caseData.values()) {
            if(data.getCaseTitle().equalsIgnoreCase(title)) return true;
        }

        return false;
    }
    /**
     * Get all cases in config
     * @return cases
     */
    public Map<String, YamlConfiguration> getCases() {
        return getCases();
    }

    /**
     * Start animation at a specific location
     * @param player The player who opened the case
     * @param location Location where to start the animation
     * @param caseName Case name
     */
    public void startAnimation(Player player, Location location, String caseName) {
        CaseData caseData = getCase(caseName).clone();
        String animation = caseData.getAnimation();
        if(animation != null) {
            if(animationManager.isRegistered(animation)) {
                animationManager.playAnimation(animation, player, location, caseData);
            } else {
                Tools.msg(player, Tools.rc("&cAn error occurred while opening the case!"));
                Tools.msg(player, Tools.rc("&cContact the project administration!"));
                getInstance().getLogger().log(Level.WARNING, "Case animation "  + animation + " does not exist!");
            }
        } else {
            Tools.msg(player, Tools.rc("&cAn error occurred while opening the case!"));
            Tools.msg(player, Tools.rc("&cContact the project administration!"));
            getInstance().getLogger().log(Level.WARNING, "Case animation name does not exist!");
        }
    }
    /**
     * Get random group from case
     * @param c Case data
     * @return Item data
     */
    public CaseData.Item getRandomItem(CaseData c) {
        return Tools.getRandomGroup(c);
    }

    /**
     * Get plugin instance
     * @return DonateCase instance
     */
    public JavaPlugin getInstance() {
        return getInstance();
    }

    /**
     * Animation end method for custom animations is called to completely end the animation
     * @param item Item data
     * @param c Case data
     * @param animation Animation name
     * @param player Player who opened
     * @param uuid Active case uuid
     */
    public void animationEnd(CaseData c, String animation, Player player, UUID uuid, CaseData.Item item) {
        ActiveCase activeCase = activeCases.get(uuid);
        Location location = activeCase.getLocation();
        AnimationEndEvent animationEndEvent = new AnimationEndEvent(player, animation, c, location, item);
        Bukkit.getServer().getPluginManager().callEvent(animationEndEvent);
        activeCasesByLocation.remove(location.getBlock().getLocation());
        activeCases.remove(uuid);
        if(getHologramManager() != null && c.getHologram().isEnabled()) {
            getHologramManager().createHologram(location.getBlock(), c);
        }
    }

    /**
     * Case open finish method for custom animations is called to grant a group, send a message, and more
     * @param caseData Case data
     * @param player Player who opened
     * @param needSound Boolean sound
     * @param item Win item data
     */
    public void onCaseOpenFinish(CaseData caseData, Player player, boolean needSound, CaseData.Item item) {}
    private String getChoice(CaseData.Item item) {
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

    private void executeActions(Player player, CaseData caseData, CaseData.Item item, String choice, boolean alternative) {
    }



    /**
     * Get case location (in Cases.yml) by block location
     * @param blockLocation Block location
     * @return case location in Cases.yml (with yaw and pitch)
     */
    public Location getCaseLocationByBlockLocation(Location blockLocation) {
        return null;
    }

    /** Get plugin configuration manager
     * @return configuration manager instance
     */
    public @NotNull CustomConfig getCustomConfig() {
        return getCustomConfig();
    }

    /**
     * Open case gui
     * @param p Player
     * @param caseData Case type
     * @param blockLocation Block location
     */
    public Inventory openGui(Player p, CaseData caseData, Location blockLocation) {
        return openGui(p,caseData,blockLocation);
    }

    /**
     * Is there a case with a name?
     * @param c Case name
     * @return Boolean
     */
    @Deprecated
    public boolean hasCase(@NotNull String c) {
        return caseData.containsKey(c);
    }

    /**
     * Get a case with the name
     * @param c Case name
     * @return Case data
     */
    @Nullable
    public CaseData getCase(@NotNull String c) {
        return caseData.getOrDefault(c, null);
    }

    /**
     * Unregister all animations
     */
    public void unregisterAnimations() {
        List<String> list = new ArrayList<>(animationManager.getRegisteredAnimations().keySet());
        for (String s : list) {
            animationManager.unregisterAnimation(s);
        }
    }

    /**
     * Get sorted history data from all cases
     * @return list of HistoryData (sorted by time)
     */
    public List<CaseData.HistoryData> getSortedHistoryData() {
        return getSortedHistoryData();
    }

    /**
     * Get sorted history data by case
     * @param historyData HistoryData from all cases (or not all)
     * @param caseType type of case for filtering
     * @return list of case HistoryData
     */
    public List<CaseData.HistoryData> sortHistoryDataByCase(List<CaseData.HistoryData> historyData, String caseType) {
        return historyData.stream().filter(Objects::nonNull)
                .filter(data -> data.getCaseType().equals(caseType))
                .sorted(Comparator.comparingLong(CaseData.HistoryData::getTime).reversed())
                .collect(Collectors.toList());
    }


    /**
     * Get addon manager for addons manipulate
     * @return AddonManager instance
     */
    public AddonManager getAddonManager() {
        return this.addonManager;
    }

    public AnimationManager getAnimationManager() {
        return this.animationManager;
    }


    /**
     * Get sub command manager
     * @return SubCommandManager instance
     */
    public SubCommandManager getSubCommandManager() {
        return this.subCommandManager;
    }

    public HologramManager getHologramManager() {
        return getHologramManager();
    }

    /**
     * Get case location by custom name (/dc create (type) (customname)
     * @param name Case custom name
     * @return Case name
     */
    @Nullable
    public Location getCaseLocationByCustomName(String name) {
        return getCaseLocationByCustomName(name);
    }

    /**
     * Get case type by custom name
     * @param name Case custom name
     * @return case type
     */
    public String getCaseTypeByCustomName(String name) {
        return getCaseTypeByCustomName(name);
    }

    /**
     * Get player primary group from Vault or LuckPerms
     * @param player Bukkit player
     * @return player primary group
     */
    public static String getPlayerGroup(Player player) {
        return null;
    }

    /**
     * Get map of default LevelGroup from Config.yml
     * @return map of LevelGroup
     */
    public static Map<String, Integer> getDefaultLevelGroup() {
        return getDefaultLevelGroup();
    }

    /**
     * Check for alternative actions
     * @param levelGroups map of LevelGroups (can be from case config or default Config.yml)
     * @param playerGroup player primary group
     * @param winGroup player win group
     * @return boolean
     */
    public boolean isAlternative(Map<String, Integer> levelGroups, String playerGroup, String winGroup) {
        if(levelGroups.containsKey(playerGroup) && levelGroups.containsKey(winGroup)) {
            int playerGroupLevel = levelGroups.get(playerGroup);
            int winGroupLevel = levelGroups.get(winGroup);
            return playerGroupLevel >= winGroupLevel;
        }
        return false;
    }

    public Addon getAddon() {
        return addon;
    }
}
