package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.data.*;
import com.jodexindustries.donatecase.api.data.action.ActionExecutor;
import com.jodexindustries.donatecase.api.events.AnimationEndEvent;
import com.jodexindustries.donatecase.config.CasesConfig;
import com.jodexindustries.donatecase.config.Config;
import com.jodexindustries.donatecase.gui.CaseGui;
import com.jodexindustries.donatecase.tools.*;
import com.jodexindustries.donatecase.api.caching.SimpleCache;
import com.jodexindustries.donatecase.api.caching.entry.InfoEntry;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jodexindustries.donatecase.DonateCase.*;


/**
 * The main class for API interaction with DonateCase, this is where most of the functions are located.
 */ 
public class Case {
    /**
     * Active cases
     */
    public final static HashMap<UUID, ActiveCase> activeCases = new HashMap<>();

    /**
     * Active cases, but by location
     */
    public final static HashMap<Location, UUID> activeCasesByLocation = new HashMap<>();


    /**
     * Players, who opened cases (open gui)
     */
    public final static HashMap<UUID, PlayerOpenCase> playersGui = new HashMap<>();

    /**
     * Loaded cases in runtime
     */
    public final static HashMap<String, CaseData> caseData = new HashMap<>();

    /**
     * Cache map for storing number of player's keys
     */
    public final static SimpleCache<InfoEntry, Integer> keysCache = new SimpleCache<>(20);

    /**
     * Cache map for storing number of player's cases opens
     */
    public final static SimpleCache<InfoEntry, Integer> openCache = new SimpleCache<>(20);

    /**
     * Save case location
     * @param caseName Case name (custom)
     * @param type Case type (config)
     * @param location Case location
     */
    public static void saveLocation(String caseName, String type, Location location) {
        CaseData caseData = getCase(type);
        if(location.getWorld() == null) {
            instance.getLogger().warning("Error with saving location: world not found!");
            return;
        }
        if(CaseManager.getHologramManager() != null && (caseData != null && caseData.getHologram().isEnabled())) CaseManager.getHologramManager().createHologram(location.getBlock(), caseData);
        String tempLocation = location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getPitch() + ";" + location.getYaw();
        getConfig().getCases().set("DonatCase.Cases." + caseName + ".location", tempLocation);
        getConfig().getCases().set("DonatCase.Cases." + caseName + ".type", type);
        getConfig().saveCases();
    }

    /**
     * Set case keys to a specific player
     * @param caseType Case type
     * @param player Player name
     * @param keys Number of keys
     */
    public static void setKeys(String caseType, String player, int keys) {
        if (!instance.sql) {
            getConfig().getKeys().set("DonatCase.Cases." + caseType + "." + player, keys == 0 ? null : keys);
            getConfig().saveKeys();
        } else {
            if(instance.mysql != null) instance.mysql.setKeys(caseType, player, keys);
        }

    }

    /**
     * Set null case keys to a specific player
     * @param caseType Case type
     * @param player Player name
     * @deprecated Use {@link #setKeys(String, String, int)} instead
     */
    @Deprecated
    public static void setNullKeys(String caseType, String player) {
        if (!instance.sql) {
            getConfig().getKeys().set("DonatCase.Cases." + caseType + "." + player, 0);
            getConfig().saveKeys();
        } else {
            if(instance.mysql != null) instance.mysql.setKeys(caseType, player, 0);
        }

    }

    /**
     * Add case keys to a specific player (async)
     * @param caseType Case type
     * @param player Player name
     * @param keys Number of keys
     */
    public static void addKeys(String caseType, String player, int keys) {
        getKeysAsync(caseType, player).thenAcceptAsync(integer -> setKeys(caseType, player, integer + keys));
    }

    /**
     * Delete case keys for a specific player (async)
     * @param caseType Case name
     * @param player Player name
     * @param keys Number of keys
     */

    public static void removeKeys(String caseType, String player, int keys) {
        getKeysAsync(caseType, player).thenAcceptAsync(integer -> setKeys(caseType, player, integer - keys));
    }

    /**
     * Get the keys to a certain player's case
     * @param caseType Case type
     * @param player Player name
     * @return Number of keys
     */
    public static int getKeys(String caseType, String player) {
        return getKeysAsync(caseType, player).join();
    }

    /**
     * Get the keys to a certain player's case
     * @param caseType Case type
     * @param player Player name
     * @return CompletableFuture of keys
     */
    public static CompletableFuture<Integer> getKeysAsync(String caseType, String player) {
        return CompletableFuture.supplyAsync(() -> instance.sql ? (instance.mysql == null ? 0 : instance.mysql.getKeys(caseType, player).join()) : getConfig().getKeys().getInt("DonatCase.Cases." + caseType + "." + player));
    }

    /**
     * Get the keys to a certain player's case from cache
     * @param caseType Case type
     * @param player Player name
     * @return Number of keys
     * @since 2.2.3.8
     */
    public static int getKeysCache(String caseType, String player) {
        int keys;
        InfoEntry entry = new InfoEntry(player, caseType);
        Integer cachedKeys = keysCache.get(entry);
        if(cachedKeys == null) {
            // Get previous, if current is null
            Integer previous = keysCache.getPrevious(entry);
            keys = previous != null ? previous : getKeys(caseType, player);

            getKeysAsync(caseType, player).thenAcceptAsync(integer -> keysCache.put(entry, integer));
        } else {
            keys = cachedKeys;
        }
        return keys;
    }

    /**
     * Get count of opened cases by player
     * @param caseType Case type
     * @param player Player, who opened
     * @return opened count
     */
    public static int getOpenCount(String caseType, String player) {
        return getOpenCountAsync(caseType, player).join();
    }

    /**
     * Get count of opened cases by player
     * @param caseType Case type
     * @param player Player, who opened
     * @return CompletableFuture of open count
     */
    public static CompletableFuture<Integer>  getOpenCountAsync(String caseType, String player) {
        return CompletableFuture.supplyAsync(() -> instance.sql ? (instance.mysql == null ? 0 :
                instance.mysql.getOpenCount(player, caseType).join()) :
                getConfig().getData().getOpenCount(player, caseType));
    }

    /**
     * Get count of opened cases by player from cache
     * @param caseType Case type
     * @param player Player, who opened
     * @return opened count
     * @since 2.2.3.8
     */
    public static int getOpenCountCache(String caseType, String player) {
        int openCount;
        InfoEntry entry = new InfoEntry(player, caseType);
        Integer cachedKeys = openCache.get(entry);
        if(cachedKeys == null) {
            getOpenCountAsync(caseType, player).thenAcceptAsync(integer -> openCache.put(entry, integer));
            // Get previous, if current is null
            Integer previous = keysCache.getPrevious(entry);
            openCount = previous != null ? previous : getOpenCount(caseType, player);
        } else {
            openCount = cachedKeys;
        }
        return openCount;
    }

    /**
     * Set case keys to a specific player (async)
     * @param caseType Case type
     * @param player Player name
     * @param openCount Opened count
     * @since 2.2.4.4
     */
    public static void setOpenCount(String caseType, String player, int openCount) {
        if (!instance.sql) {
            getConfig().getData().setOpenCount(player, caseType, openCount);
        } else {
            if(instance.mysql != null) instance.mysql.setCount(caseType, player, openCount);
        }
    }

    /**
     * Add count of opened cases by player (async)
     * @param caseType Case type
     * @param player Player name
     * @param openCount Opened count
     * @since 2.2.4.4
     */
    public static void addOpenCount(String caseType, String player, int openCount) {
        getOpenCountAsync(caseType, player).thenAcceptAsync(integer -> setOpenCount(caseType, player, integer + openCount));
    }

    /**
     * Delete case by location in Cases.yml
     * @param loc Case location
     */
    public static void deleteCaseByLocation(Location loc) {
        getConfig().getCases().set("DonatCase.Cases." + getCaseCustomNameByLocation(loc), null);
        getConfig().saveCases();
    }

    /**
     * Delete case by name in Cases.yml
     * @param name Case name
     */
    public static void deleteCaseByName(String name) {
        getConfig().getCases().set("DonatCase.Cases." + name, null);
        getConfig().saveCases();
    }

    /**
     * Check if case has by location
     * @param loc Case location
     * @return Boolean
     */
    public static boolean hasCaseByLocation(Location loc) {
        ConfigurationSection casesSection = getConfig().getCases().getConfigurationSection("DonatCase.Cases");
        if(casesSection == null) return false;

        for (String name : casesSection.getValues(false).keySet()) {
            ConfigurationSection caseSection = getConfig().getCases().getConfigurationSection("DonatCase.Cases." + name);
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
     * @param infoType Information type ("type", "name" or "location")
     * @return Case information
     */
    private static <T> T getCaseInfoByLocation(Location loc, String infoType, Class<T> clazz) {
        T object = null;
        ConfigurationSection casesSection = getConfig().getCases().getConfigurationSection("DonatCase.Cases");
        if (casesSection == null) return object;

        for (String name : casesSection.getValues(false).keySet()) {
            ConfigurationSection caseSection = casesSection.getConfigurationSection(name);
            if (caseSection == null) continue;

            String location = caseSection.getString("location");
            if (location == null) continue;

            String[] worldLocation = location.split(";");
            World world = Bukkit.getWorld(worldLocation[0]);
            try {
                Location temp = new Location(world, Double.parseDouble(worldLocation[1]), Double.parseDouble(worldLocation[2]), Double.parseDouble(worldLocation[3]));

                if (temp.equals(loc)) {
                    switch (infoType) {
                        case "type":
                            object = clazz.cast(caseSection.getString("type"));
                            break;
                        case "name":
                            object = clazz.cast(name);
                            break;
                        case "location": {
                            Location result = temp.clone();
                            result.setPitch(Float.parseFloat(worldLocation[4]));
                            result.setYaw(Float.parseFloat(worldLocation[5]));
                            object = clazz.cast(result);
                            break;
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        return object;
    }

    /**
     * Get case type by location
     * @param loc Case location
     * @return Case type
     */
    public static String getCaseTypeByLocation(Location loc) {
        return getCaseInfoByLocation(loc, "type", String.class);
    }

    /**
     * Get case name by location
     * @param loc Case location
     * @return Case name
     */
    public static String getCaseCustomNameByLocation(Location loc) {
        return getCaseInfoByLocation(loc, "name", String.class);
    }

    /**
     * Get case location (in Cases.yml) by block location
     * @param loc Block location
     * @return case location in Cases.yml (with yaw and pitch)
     */
    public static Location getCaseLocationByBlockLocation(Location loc) {
        return getCaseInfoByLocation(loc, "location", Location.class);
    }

    /**
     * Is there a case with a type?
     * @param caseType Case type
     * @return true - if case found in memory
     */
    public static boolean hasCaseByType(String caseType) {
        return !caseData.isEmpty() && caseData.containsKey(caseType);
    }

    /**
     * Is there a case with a specific custom name?
     * <p>
     * In other words, whether a case has been created
     * @param name Case name
     * @return true - if case created on the server
     */
    public static boolean hasCaseByCustomName(String name) {
        ConfigurationSection section = getConfig().getCases().getConfigurationSection("DonatCase.Cases");
        if(section == null) return false;

        return getConfig().getCases().getConfigurationSection("DonatCase.Cases") != null
                && section.contains(name);
    }

    /**
     * Is there a case with a specific title?
     * @param title Case title
     * @return true - if case found in memory
     */
    public static boolean hasCaseByTitle(String title) {
        return caseData.values().stream().anyMatch(data -> data.getCaseTitle().equalsIgnoreCase(title));
    }

    /**
     * @deprecated  Use {@link Config#getCasesConfig()} instead
     * @return CasesConfig object
     */
    @Deprecated
    public static CasesConfig getCasesConfig() {
        return instance.config.getCasesConfig();
    }

    /**
     * Get random group from case
     * @deprecated Use {@link CaseData#getRandomItem()} instead
     * @param c Case data
     * @return Item data
     */
    @Deprecated
    public static CaseData.Item getRandomItem(CaseData c) {
        return c.getRandomItem();
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
        animationEnd(caseData, (OfflinePlayer) player, uuid, item);
    }

    /**
     * Animation end method for custom animations is called to completely end the animation
     * @param item Item data
     * @param caseData Case data
     * @param player Player who opened (offline player)
     * @param uuid Active case uuid
     */
    public static void animationEnd(CaseData caseData, OfflinePlayer player, UUID uuid, CaseData.Item item) {
        ActiveCase activeCase = activeCases.get(uuid);
        Location location = activeCase.getLocation();
        activeCasesByLocation.remove(location.getBlock().getLocation());
        activeCases.remove(uuid);
        if (CaseManager.getHologramManager() != null && caseData.getHologram().isEnabled()) {
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
     * @deprecated Use {@link #animationPreEnd(CaseData, OfflinePlayer, Location, CaseData.Item)} instead
     */
    @Deprecated
    public static void animationPreEnd(CaseData caseData, Player player, boolean needSound, CaseData.Item item) {
        animationPreEnd(caseData, player, needSound, item, Bukkit.getWorlds().get(0).getSpawnLocation());
    }

    /**
     * Animation pre end method for custom animations is called to grant a group, send a message, and more
     * @param caseData Case data
     * @param player Player who opened
     * @param ignoredNeedSound Boolean sound
     * @param item Win item data
     * @param location Location where case was opened
     * @deprecated Use {@link #animationPreEnd(CaseData, OfflinePlayer, Location, CaseData.Item)} instead
     */
    @Deprecated
    public static void animationPreEnd(CaseData caseData, OfflinePlayer player, boolean ignoredNeedSound, CaseData.Item item, Location location) {
        animationPreEnd(caseData, player, activeCasesByLocation.get(location.getBlock().getLocation()), item);
    }

    /**
     * Animation pre end method for custom animations is called to grant a group, send a message, and more
     * @param caseData Case data
     * @param player Player who opened (offline player)
     * @param uuid Active case uuid
     * @param item Item data
     * @since 2.2.4.4
     */
    public static void animationPreEnd(CaseData caseData, OfflinePlayer player, UUID uuid, CaseData.Item item) {
        ActiveCase activeCase = activeCases.get(uuid);
        Location location = activeCase != null ? activeCase.getLocation() : null;
        animationPreEnd(caseData, player, location, item);
    }

    /**
     * Animation pre end method for custom animations is called to grant a group, send a message, and more
     * @param caseData Case data
     * @param player Player who opened (offline player)
     * @param location Active case block location
     * @param item Item data
     * @since 2.2.4.4
     */
    public static void animationPreEnd(CaseData caseData, OfflinePlayer player, Location location, CaseData.Item item) {
        World world = location != null ? location.getWorld() : null;
        if(world == null) world = Bukkit.getWorlds().get(0);

        String choice = "";
        Map<String, Integer> levelGroups = getDefaultLevelGroup();
        if(!caseData.getLevelGroups().isEmpty()) levelGroups = caseData.getLevelGroups();

        String playerGroup = getPlayerGroup(world.getName(), player);
        if(isAlternative(levelGroups, playerGroup, item.getGroup())) {
            executeActions(player, caseData, item, null, true);
        } else {
            if (item.getGiveType().equalsIgnoreCase("ONE")) {
                executeActions(player, caseData, item, null, false);
            } else {
                choice = getRandomActionChoice(item);
                executeActions(player, caseData, item, choice, false);
            }
        }

        saveOpenInfo(caseData, player, item, choice);
    }

    /**
     * Saving case open information
     * Called in {@link Case#animationPreEnd} method
     * @param caseData Case data
     * @param player Player who opened
     * @param item Win item
     * @param choice In fact, these are actions that were selected from the RandomActions section
     */
    private static void saveOpenInfo(CaseData caseData, OfflinePlayer player, CaseData.Item item, String choice) {
        CaseData.HistoryData data = new CaseData.HistoryData(item.getItemName(), caseData.getCaseType(), player.getName(), System.currentTimeMillis(), item.getGroup(), choice);
        CaseData.HistoryData[] list = caseData.getHistoryData();
        System.arraycopy(list, 0, list, 1, list.length - 1);
        list[0] = data;

        for (int i = 0; i < list.length; i++) {
            CaseData.HistoryData tempData = list[i];
            if(tempData != null) {
                if(!instance.sql) {
                    getConfig().getData().setHistoryData(caseData.getCaseType(), i, tempData);
                } else {
                    if(instance.mysql != null) instance.mysql.setHistoryData(caseData.getCaseType(), i, tempData);
                }
            }
        }

        // Set history data in memory
        CaseData finalCase = getCase(caseData.getCaseType());
        if(finalCase != null) finalCase.setHistoryData(list);

        addOpenCount(player.getName(), caseData.getCaseType(), 1);
    }

    /**
     * Get random choice from item random action list
     * @param item Case item
     * @return random action name
     */
    public static String getRandomActionChoice(CaseData.Item item) {
        ProbabilityCollection<String> collection = new ProbabilityCollection<>();
        for (String name : item.getRandomActions().keySet()) {
            CaseData.Item.RandomAction randomAction = item.getRandomAction(name);
            if(randomAction == null) continue;
            collection.add(name, randomAction.getChance());
        }
        return collection.get();
    }

    /**
     * Execute actions after case opening
     * @param player Player, who opened
     * @param caseData Case that was opened
     * @param item The prize that was won
     * @param choice In fact, these are actions that were selected from the RandomActions section
     * @param alternative If true, the item's alternative actions will be selected. (Same as {@link CaseData.Item#getAlternativeActions()})
     */
   public static void executeActions(OfflinePlayer player, CaseData caseData, CaseData.Item item, String choice, boolean alternative) {
       final String[] replacementRegex = {
               "%player%:" + player.getName(),
               "%casename%:" + caseData.getCaseType(),
               "%casedisplayname%:" + caseData.getCaseDisplayName(),
               "%casetitle%:" + caseData.getCaseTitle(),
               "%group%:" + item.getGroup(),
               "%groupdisplayname%:" + item.getMaterial().getDisplayName()
       };

       List<String> actions = Tools.rt(getActionsBasedOnChoice(item, choice, alternative), replacementRegex);

       executeActions(player, actions);
   }

    /**
     * Get actions from case item
     * @param item Case item
     * @param choice In fact, these are actions that were selected from the RandomActions section
     * @param alternative If true, the item's alternative actions will be selected. (Same as {@link CaseData.Item#getAlternativeActions()})
     * @return list of selected actions
     */
    public static List<String> getActionsBasedOnChoice(CaseData.Item item, String choice, boolean alternative) {
        if (choice != null) {
            CaseData.Item.RandomAction randomAction = item.getRandomAction(choice);
            if (randomAction != null) {
                return randomAction.getActions();
            }
        }
        return alternative ? item.getAlternativeActions() : item.getActions();
    }

    /**
     * Extract cooldown from action string
     * @param action Action string. Format [cooldown:int]
     * @return cooldown
     */
    public static int extractCooldown(String action) {
        Pattern pattern = Pattern.compile("\\[cooldown:(.*?)]");
        Matcher matcher = pattern.matcher(action);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    /**
     * Execute actions
     * @param player Player, who opened case (maybe another reason)
     * @param actions List of actions
     * @since 2.2.4.3
     */
    public static void executeActions(OfflinePlayer player, List<String> actions) {
        for (String action : actions) {

            action = Tools.rc(PAPISupport.setPlaceholders(player, action));
            int cooldown = extractCooldown(action);
            action = action.replaceFirst("\\[cooldown:(.*?)]", "");

            executeAction(player, action, cooldown);
        }
    }

    /**
     * Execute action with specific cooldown
     * @param player Player, who opened case (maybe another reason)
     * @param action Action to be executed
     * @param cooldown Cooldown in seconds
     */
    public static void executeAction(OfflinePlayer player, String action, int cooldown) {
        String temp = ActionManager.getByStart(action);
        if(temp == null) return;

        String context = action.replace(temp, "").trim();

        ActionExecutor actionExecutor = ActionManager.getRegisteredAction(temp);
        if(actionExecutor == null) return;

        actionExecutor.execute(player, context, cooldown);
    }

    /** Get plugin configuration manager
     * @return configuration manager instance
     * @since 2.2.3.8
     */
    @NotNull
    public static Config getConfig() {
        return getInstance().config;
    }

    /** Get plugin configuration manager
     * @deprecated Use {@link #getConfig()} instead
     * @return configuration manager instance
     */
    @Deprecated
    public static Config getCustomConfig() {
        return getInstance().config;
    }

    /**
     * Open case gui
     * @param p Player
     * @param caseData Case type
     * @param blockLocation Block location
     * @return opened inventory object
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
        return getAsyncSortedHistoryData().join();
    }

    /**
     * Get sorted history data from all cases with CompletableFuture
     * @return list of HistoryData (sorted by time)
     */
    public static CompletableFuture<List<CaseData.HistoryData>> getAsyncSortedHistoryData() {
        CompletableFuture<List<CaseData.HistoryData>> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            List<CaseData.HistoryData> historyData;
            if(!instance.sql) {
                historyData = caseData.values().stream()
                        .filter(Objects::nonNull)
                        .flatMap(data -> {
                            CaseData.HistoryData[] temp = data.getHistoryData();
                            return temp != null ? Arrays.stream(temp) : Stream.empty();
                        })
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparingLong(CaseData.HistoryData::getTime).reversed())
                        .collect(Collectors.toList());
            } else {
                historyData = instance.mysql.getHistoryData().join().stream().filter(Objects::nonNull)
                        .sorted(Comparator.comparingLong(CaseData.HistoryData::getTime).reversed())
                        .collect(Collectors.toList());
            }
            future.complete(historyData);
        });
        return future;
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
        String location = getConfig().getCases().getString("DonatCase.Cases." + name + ".location");
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
        return getConfig().getCases().getString("DonatCase.Cases." + name + ".type");
    }

    /**
     * Get player primary group from Vault or LuckPerms
     * @param world Player world
     * @param player Bukkit player
     * @return player primary group
     */
    public static String getPlayerGroup(String world, OfflinePlayer player) {
        String group = null;
        if(instance.permissionDriver == PermissionDriver.vault) if(instance.permission != null) group = instance.permission.getPrimaryGroup(world, player);
        if(instance.permissionDriver == PermissionDriver.luckperms) if(instance.luckPerms != null) {
            User user = instance.luckPerms.getUserManager().getUser(player.getUniqueId());
            if(user != null) group = user.getPrimaryGroup();
        }
        return group;
    }

    /**
     * Get map of default LevelGroup from Config.yml
     * @return map of LevelGroup
     */
    public static Map<String, Integer> getDefaultLevelGroup() {
        Map<String, Integer> levelGroup = new HashMap<>();
        boolean isEnabled = getConfig().getConfig().getBoolean("DonatCase.LevelGroup");
        if(isEnabled) {
            ConfigurationSection section = getConfig().getConfig().getConfigurationSection("DonatCase.LevelGroups");
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

    /**
     * Trying to clean all entities with "case" metadata value,
     * all loaded cases in runtime,
     * all active cases, keys and open caches
     * @since 2.2.3.8
     */
    public static void cleanCache() {
        Bukkit.getWorlds().forEach(world -> world.getEntitiesByClass(ArmorStand.class).stream().filter(stand -> stand.hasMetadata("case")).forEachOrdered(Entity::remove));
        Case.playersGui.clear();
        Case.caseData.clear();
        Case.activeCases.clear();
        Case.activeCasesByLocation.clear();
        keysCache.clear();
        openCache.clear();
    }
}
