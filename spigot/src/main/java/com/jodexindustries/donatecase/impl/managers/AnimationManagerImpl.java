package com.jodexindustries.donatecase.impl.managers;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.*;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.animation.JavaAnimationBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataHistory;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.api.events.*;
import com.jodexindustries.donatecase.api.gui.CaseGui;
import com.jodexindustries.donatecase.api.manager.AnimationManager;
import com.jodexindustries.donatecase.api.tools.ProbabilityCollection;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static com.jodexindustries.donatecase.DonateCase.instance;

/**
 * Animation control class, registration, playing
 */
public class AnimationManagerImpl implements AnimationManager<JavaAnimationBukkit, CaseDataMaterialBukkit,
        Player, Location, Block, CaseDataBukkit> {
    /**
     * Map of registered animations
     */
    private final static Map<String, CaseAnimation<JavaAnimationBukkit>> registeredAnimations = new HashMap<>();

    /**
     * Map of active cases
     */
    private final static Map<UUID, ActiveCase<Block, Player, CaseDataItem<CaseDataMaterialBukkit>>> activeCases = new HashMap<>();

    /**
     * Active cases, but by location
     */
    private final static Map<Block, UUID> activeCasesByBlock = new HashMap<>();

    private final DCAPIBukkit api;
    private final Addon addon;

    /**
     * Default constructor
     *
     * @param api An DCAPI that will manage animations
     */
    public AnimationManagerImpl(@NotNull DCAPIBukkit api) {
        this.api = api;
        this.addon = api.getAddon();
    }

    @NotNull
    @Override
    public CaseAnimation.Builder<JavaAnimationBukkit> builder(@NotNull String name) {
        return new CaseAnimation.Builder<>(name, addon);
    }

    @Override
    public boolean registerAnimation(CaseAnimation<JavaAnimationBukkit> caseAnimation) {
        String name = caseAnimation.getName();
        if(name == null) return false;

        if(!isRegistered(name)) {
            registeredAnimations.put(name, caseAnimation);
            return true;
        } else {
            addon.getLogger().warning("Animation " + name + " already registered!");
        }
        return false;
    }

    @Override
    public void unregisterAnimation(@NotNull String name) {
        if (isRegistered(name)) {
            registeredAnimations.remove(name);
        } else {
            addon.getLogger().warning("Animation with name " + name + " already unregistered!");
        }
    }

    @Override
    public void unregisterAnimations() {
        List<String> list = new ArrayList<>(registeredAnimations.keySet());
        list.forEach(this::unregisterAnimation);
    }

    @Override
    public CompletableFuture<UUID> startAnimation(@NotNull Player player, @NotNull Location location, @NotNull CaseDataBukkit caseData) {
        return startAnimation(player, location, caseData, 0);
    }

    @Override
    public CompletableFuture<UUID> startAnimation(@NotNull Player player, @NotNull Location location, @NotNull CaseDataBukkit caseData, int delay) {
        String animation = caseData.getAnimation();
        CaseAnimation<JavaAnimationBukkit> caseAnimation = getRegisteredAnimation(animation);

        ConfigurationSection settings = caseData.getAnimationSettings() != null ? caseData.getAnimationSettings() : api.getConfig().getAnimations().getConfigurationSection(animation);

        Block block = location.getBlock();


        if (!validateStartConditions(caseData, caseAnimation, settings, block, player)) {
            return CompletableFuture.completedFuture(null);
        }

        assert caseAnimation != null;

        caseData = caseData.clone();
        caseData.setItems(DCToolsBukkit.sortItemsByIndex(caseData.getItems()));


        CaseDataItem<CaseDataMaterialBukkit> winItem = caseData.getRandomItem();
        winItem.getMaterial().setDisplayName(api.getTools().getPAPI().setPlaceholders(player, winItem.getMaterial().getDisplayName()));
        AnimationPreStartEvent preStartEvent = new AnimationPreStartEvent(player, caseData, block, winItem);
        Bukkit.getPluginManager().callEvent(preStartEvent);

        winItem = preStartEvent.getWinItem();

        UUID uuid = UUID.randomUUID();
        ActiveCase<Block, Player, CaseDataItem<CaseDataMaterialBukkit>> activeCase = new ActiveCase<>(uuid, block, player, winItem, caseData.getCaseType());


        CompletableFuture<UUID> animationCompletion = new CompletableFuture<>();
        Location caseLocation = location;

        if(caseAnimation.isRequireBlock()) {
            activeCasesByBlock.put(block, uuid);
            if (instance.hologramManager != null && caseData.getHologram().isEnabled()) {
                instance.hologramManager.removeHologram(block);
            }

            Location tempLocation = Case.getCaseLocationByBlockLocation(block.getLocation());
            if (tempLocation != null) caseLocation = tempLocation;

            for (CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit> gui : api.getGUIManager().getPlayersGUI().values()) {
                if (gui.getLocation().equals(block.getLocation())) {
                    gui.getPlayer().closeInventory();
                }
            }
        }

        Class<? extends JavaAnimationBukkit> animationClass = caseAnimation.getAnimation();

        try {

            if (animationClass != null) {
                JavaAnimationBukkit javaAnimation = animationClass.getDeclaredConstructor().newInstance();
                javaAnimation.init(api, player, caseLocation, uuid, caseData, preStartEvent.getWinItem(), settings);

                Bukkit.getScheduler().runTaskLater(Case.getInstance(), () -> {
                    try {
                        javaAnimation.start();
                        animationCompletion.complete(uuid);
                    } catch (Throwable t) {
                        addon.getLogger().log(Level.WARNING, "Error with starting animation " + animation, t);
                        if(caseAnimation.isRequireBlock()) activeCasesByBlock.remove(block);
                        animationCompletion.complete(null);
                    }
                }, delay);

            } else {
                throw new IllegalArgumentException("Animation executable class does not exist!");
            }

        } catch (Throwable t) {
            addon.getLogger().log(Level.WARNING, "Error with starting animation " + animation, t);
            if(caseAnimation.isRequireBlock()) activeCasesByBlock.remove(block);
            animationCompletion.complete(null);
        }

        activeCases.put(uuid, activeCase);

        // AnimationStart event
        AnimationStartEvent startEvent = new AnimationStartEvent(player, animation, caseData, block, winItem, uuid);
        Bukkit.getPluginManager().callEvent(startEvent);
        return animationCompletion;
    }

    @Override
    public void animationPreEnd(UUID uuid) {
        ActiveCase<Block, Player, CaseDataItem<CaseDataMaterialBukkit>> activeCase = activeCases.get(uuid);
        if(activeCase == null) {
            addon.getLogger().warning("Animation with uuid: " + uuid + " not found!");
            return;
        }

        CaseDataBukkit caseData = api.getCaseManager().getCase(activeCase.getCaseType());
        animationPreEnd(caseData, activeCase.getPlayer(), activeCase.getBlock().getLocation(), activeCase.getWinItem());
    }

    @Override
    public void animationPreEnd(CaseDataBukkit caseData, Player player, UUID uuid, CaseDataItem<CaseDataMaterialBukkit> item) {
        ActiveCase<Block, Player, CaseDataItem<CaseDataMaterialBukkit>> activeCase = activeCases.get(uuid);
        Location location = activeCase != null ? activeCase.getBlock().getLocation() : null;
        animationPreEnd(caseData, player, location, item);
    }

    @Override
    public void animationPreEnd(CaseDataBukkit caseData, Player player, Location location, CaseDataItem<CaseDataMaterialBukkit> item) {
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

    @Override
    public void animationEnd(UUID uuid) {
        ActiveCase<Block, Player, CaseDataItem<CaseDataMaterialBukkit>> activeCase = activeCases.get(uuid);
        if(activeCase == null) {
            addon.getLogger().warning("Animation with uuid: " + uuid + " not found!");
            return;
        }

        animationEnd(activeCase);
    }

    @Override
    public void animationEnd(CaseDataBukkit caseData, Player player, UUID uuid, CaseDataItem<CaseDataMaterialBukkit> item) {
        animationEnd(uuid);
    }

    private void animationEnd(@NotNull ActiveCase<Block, Player, CaseDataItem<CaseDataMaterialBukkit>> activeCase) {
        CaseDataBukkit caseData = Case.getInstance().api.getCaseManager().getCase(activeCase.getCaseType());
        if(caseData == null) return;

        CaseAnimation<JavaAnimationBukkit> caseAnimation = getRegisteredAnimation(caseData.getAnimation());
        if(caseAnimation == null) return;

        CaseDataItem<CaseDataMaterialBukkit> item = activeCase.getWinItem();
        Player player = activeCase.getPlayer();

        if(!activeCase.isKeyRemoved()) Case.getInstance().api.getCaseKeyManager().removeKeys(caseData.getCaseType(), player.getName(), 1);

        Block block = activeCase.getBlock();
        activeCases.remove(activeCase.getUuid());

        if(caseAnimation.isRequireBlock()) {
            activeCasesByBlock.remove(block);
            if (instance.hologramManager != null && caseData.getHologram().isEnabled()) {
                instance.hologramManager.createHologram(block, caseData);
            }
        }
        AnimationEndEvent animationEndEvent = new AnimationEndEvent(player, caseData, block, item);
        Bukkit.getScheduler().runTask(api.getDonateCase(), () -> Bukkit.getServer().getPluginManager().callEvent(animationEndEvent));
    }

    @Override
    public boolean isRegistered(String name) {
        return registeredAnimations.containsKey(name);
    }

    @Nullable
    @Override
    public CaseAnimation<JavaAnimationBukkit> getRegisteredAnimation(String animation) {
        return registeredAnimations.get(animation);
    }

    @Override
    public Map<String, CaseAnimation<JavaAnimationBukkit>> getRegisteredAnimations() {
        return registeredAnimations;
    }

    @Override
    public Map<UUID, ActiveCase<Block, Player, CaseDataItem<CaseDataMaterialBukkit>>> getActiveCases() {
        return activeCases;
    }

    @Override
    public Map<Block, UUID> getActiveCasesByBlock() {
        return activeCasesByBlock;
    }

    private boolean validateStartConditions(CaseDataBukkit caseData, CaseAnimation<JavaAnimationBukkit> animation,
                                            ConfigurationSection settings, Block block, Player player) {
        if (animation == null) {
            addon.getLogger().log(Level.WARNING, "Case animation " + caseData.getAnimation() + " does not exist!");
            return false;
        }

        if (animation.isRequireBlock() && activeCasesByBlock.containsKey(block)) {
            addon.getLogger().log(Level.WARNING, "Player " + player.getName() +
                    " trying to start animation while another animation is running in case: " + caseData.getCaseType());
            return false;
        }

        if (animation.isRequireSettings() && settings == null) {
            addon.getLogger().log(Level.WARNING, "Animation " + animation + " requires settings for starting!");
            return false;
        }

        if (caseData.getItems().isEmpty()) {
            addon.getLogger().log(Level.WARNING, "Player " + player.getName() +
                            " trying to start animation without items in case: " + caseData.getCaseType());
            return false;
        }

        if(!caseData.hasRealItems()) {
            addon.getLogger().log(Level.WARNING, "Player " + player.getName() +
                            " trying to start animation without real (chance > 0) items in case: " + caseData.getCaseType());
            return false;
        }

        return true;
    }

    private static void saveOpenInfo(CaseDataBukkit caseData, OfflinePlayer player, CaseDataItem<CaseDataMaterialBukkit> item, String choice) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            CaseDataHistory data = new CaseDataHistory(item.getItemName(), caseData.getCaseType(), player.getName(), System.currentTimeMillis(), item.getGroup(), choice);
            CaseDataHistory[] historyData = caseData.getHistoryData();

            if(historyData.length > 0) {
                List<CaseDataHistory> databaseData = instance.database.getHistoryData(caseData.getCaseType()).join();
                if (!databaseData.isEmpty())
                    historyData = databaseData.toArray(new CaseDataHistory[historyData.length]);

                System.arraycopy(historyData, 0, historyData, 1, historyData.length - 1);
                historyData[0] = data;

                for (int i = 0; i < historyData.length; i++) {
                    CaseDataHistory tempData = historyData[i];
                    if (tempData != null) {
                        instance.database.setHistoryData(caseData.getCaseType(), i, tempData);
                    }
                }

                // Set history data in memory
                Objects.requireNonNull(instance.api.getCaseManager().getCase(caseData.getCaseType())).setHistoryData(historyData);
            }

            instance.api.getCaseOpenManager().addOpenCount(caseData.getCaseType(), player.getName(), 1);
        });
    }

    /**
     * Get random choice from item random action list
     * @param item Case item
     * @return random action name
     */
    public static String getRandomActionChoice(CaseDataItem<CaseDataMaterialBukkit> item) {
        ProbabilityCollection<String> collection = new ProbabilityCollection<>();
        for (String name : item.getRandomActions().keySet()) {
            CaseDataItem.RandomAction randomAction = item.getRandomAction(name);
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
     * @param alternative If true, the item's alternative actions will be selected. (Same as {@link CaseDataItem#getAlternativeActions()})
     */
    public static void executeActions(Player player, CaseDataBukkit caseData, CaseDataItem<CaseDataMaterialBukkit> item, String choice, boolean alternative) {
        final String[] replacementRegex = {
                "%player%:" + player.getName(),
                "%casename%:" + caseData.getCaseType(),
                "%casedisplayname%:" + caseData.getCaseDisplayName(),
                "%casetitle%:" + caseData.getCaseTitle(),
                "%group%:" + item.getGroup(),
                "%groupdisplayname%:" + item.getMaterial().getDisplayName()
        };

        List<String> actions = DCToolsBukkit.rt(getActionsBasedOnChoice(item, choice, alternative), replacementRegex);

        instance.api.getActionManager().executeActions(player, actions);
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
        boolean isEnabled = instance.config.getConfig().getBoolean("DonateCase.LevelGroup");
        if(isEnabled) {
            ConfigurationSection section = instance.config.getConfig().getConfigurationSection("DonateCase.LevelGroups");
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
     * Get actions from case item
     * @param item Case item
     * @param choice In fact, these are actions that were selected from the RandomActions section
     * @param alternative If true, the item's alternative actions will be selected. (Same as {@link CaseDataItem#getAlternativeActions()})
     * @return list of selected actions
     */
    public static List<String> getActionsBasedOnChoice(CaseDataItem<CaseDataMaterialBukkit> item, String choice, boolean alternative) {
        if (choice != null) {
            CaseDataItem.RandomAction randomAction = item.getRandomAction(choice);
            if (randomAction != null) {
                return randomAction.getActions();
            }
        }
        return alternative ? item.getAlternativeActions() : item.getActions();
    }
}