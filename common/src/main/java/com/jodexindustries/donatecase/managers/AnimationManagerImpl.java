package com.jodexindustries.donatecase.managers;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.*;
import com.jodexindustries.donatecase.api.data.animation.Animation;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.casedata.*;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.manager.AnimationManager;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.api.tools.ProbabilityCollection;
import com.jodexindustries.donatecase.platform.BackendPlatform;
import net.luckperms.api.model.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * Animation control class, registration, playing
 */
public class AnimationManagerImpl implements AnimationManager {
    /**
     * Map of registered animations
     */
    private final static Map<String, CaseAnimation> registeredAnimations = new HashMap<>();

    /**
     * Map of active cases
     */
    private final static Map<UUID, ActiveCase> activeCases = new HashMap<>();

    /**
     * Active cases, but by location
     */
    private final static Map<Object, List<UUID>> activeCasesByBlock = new HashMap<>();

    private final DCAPI api;
    private final BackendPlatform platform;

    public AnimationManagerImpl(DCAPI api) {
        this.api = api;
        this.platform = (BackendPlatform) api.getPlatform();
    }

    @Override
    public boolean register(CaseAnimation animation) {
        String name = animation.getName();

        if(!isRegistered(name)) {
            registeredAnimations.put(name, animation);
            return true;
        } else {
            platform.getLogger().warning("Animation " + name + " already registered!");
        }
        return false;
    }

    @Override
    public void unregister(@NotNull String name) {
        if (isRegistered(name)) {
            registeredAnimations.remove(name);
        } else {
            platform.getLogger().warning("Animation with name " + name + " already unregistered!");
        }
    }

    @Override
    public void unregister() {
        List<String> list = new ArrayList<>(registeredAnimations.keySet());
        list.forEach(this::unregister);
    }

    @Override
    public CompletableFuture<UUID> start(@NotNull DCPlayer player, @NotNull CaseLocation location, @NotNull CaseData caseData) {
        return start(player, location, caseData, 0);
    }

    @Override
    public CompletableFuture<UUID> start(@NotNull DCPlayer player, @NotNull CaseLocation location, @NotNull CaseData caseData, int delay) {
        String animation = caseData.getAnimation();
        CaseAnimation caseAnimation = get(animation);

        ConfigurationNode settings = caseData.getAnimationSettings() != null ? caseData.getAnimationSettings() : api.getConfig().getAnimations().node(animation);


        if (!validateStartConditions(caseData, caseAnimation, settings, location, player)) {
            return CompletableFuture.completedFuture(null);
        }

        assert caseAnimation != null;

        caseData = caseData.clone();
        caseData.setItems(DCTools.sortItemsByIndex(caseData.getItems()));


        CaseDataItem winItem = caseData.getRandomItem();
        winItem.getMaterial().setDisplayName(api.getPlatform().getTools().getPAPI().setPlaceholders(player, winItem.getMaterial().getDisplayName()));
//        AnimationPreStartEvent preStartEvent = new AnimationPreStartEvent(player, caseData, block, winItem);
//        Bukkit.getPluginManager().callEvent(preStartEvent);
//
//        winItem = preStartEvent.getWinItem();

        UUID uuid = UUID.randomUUID();
        ActiveCase activeCase = new ActiveCase(uuid, location, player, winItem, caseData.getCaseType());
        activeCase.setLocked(caseAnimation.isRequireBlock());

        CompletableFuture<UUID> animationCompletion = new CompletableFuture<>();

        if (caseAnimation.isRequireBlock()) {
//            if (BukkitDonateCase.instance.hologramManager != null && caseData.getHologram().isEnabled()) {
//                BukkitDonateCase.instance.hologramManager.removeHologram(block);
//            }

//            Location tempLocation = Case.getCaseLocationByBlockLocation(block.getLocation());
//            if (tempLocation != null) caseLocation = tempLocation;

            for (CaseGuiWrapper gui : api.getGUIManager().getMap().values()) {
                if (gui.getLocation().equals(location)) {
                    gui.getPlayer().closeInventory();
                }
            }
        }

        Class<? extends Animation> animationClass = caseAnimation.getAnimation();

        try {

            Animation javaAnimation = animationClass.getDeclaredConstructor().newInstance();
            javaAnimation.init(player, location, uuid, caseData, winItem, settings);


            api.getPlatform().runSync(() -> {
                try {
                    javaAnimation.start();
                    animationCompletion.complete(uuid);
                } catch (Throwable t) {
                    platform.getLogger().log(Level.WARNING, "Error with starting animation " + animation, t);
                    if (caseAnimation.isRequireBlock()) activeCasesByBlock.remove(location);
                    animationCompletion.complete(null);
                }
            });

        } catch (Throwable t) {
            platform.getLogger().log(Level.WARNING, "Error with starting animation " + animation, t);
            if (caseAnimation.isRequireBlock()) activeCasesByBlock.remove(location);
            animationCompletion.complete(null);
        }

        activeCases.put(uuid, activeCase);
        activeCasesByBlock.computeIfAbsent(location, k -> new ArrayList<>()).add(uuid);
        return animationCompletion;
    }

    @Override
    public void preEnd(UUID uuid) {
        ActiveCase activeCase = activeCases.get(uuid);
        if(activeCase == null) {
            platform.getLogger().warning("Animation with uuid: " + uuid + " not found!");
            return;
        }

        CaseData caseData = api.getCaseManager().get(activeCase.getCaseType());
        if (caseData != null) {
            preEnd(caseData, activeCase.getPlayer(), activeCase.getBlock(), activeCase.getWinItem());
        }
    }

    @Override
    public void preEnd(CaseData caseData, DCPlayer player, CaseLocation location, CaseDataItem item) {
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
                choice = getRandomActionChoice(item);
                executeActions(player, caseData, item, choice, false);
            }
        }

        saveOpenInfo(caseData, player, item, choice);
    }

    @Override
    public void end(UUID uuid) {
        ActiveCase activeCase = activeCases.get(uuid);
        if(activeCase == null) {
            platform.getLogger().warning("Animation with uuid: " + uuid + " not found!");
            return;
        }

        animationEnd(activeCase);
    }

    private void animationEnd(@NotNull ActiveCase activeCase) {
        CaseData caseData = api.getCaseManager().get(activeCase.getCaseType());
        if(caseData == null) return;

        CaseAnimation caseAnimation = get(caseData.getAnimation());
        if(caseAnimation == null) return;

//        CaseDataItem item = activeCase.getWinItem();
        DCPlayer player = activeCase.getPlayer();

        if(!activeCase.isKeyRemoved()) api.getCaseKeyManager().remove(caseData.getCaseType(), player.getName(), 1);

        CaseLocation location = activeCase.getBlock();
        activeCases.remove(activeCase.getUuid());
        activeCasesByBlock.remove(location);

        // TODO Hologram creating
//        if(caseAnimation.isRequireBlock()) {
//            if (BukkitDonateCase.instance.hologramManager != null && caseData.getHologram().isEnabled()) {
//                BukkitDonateCase.instance.hologramManager.createHologram(block, caseData);
//            }
//        }
        // TODO AnimationEndEvent
//        AnimationEndEvent animationEndEvent = new AnimationEndEvent(player, caseData, block, item);
//        Bukkit.getScheduler().runTask(api.getDonateCase(), () -> Bukkit.getServer().getPluginManager().callEvent(animationEndEvent));
    }

    @Override
    public boolean isRegistered(String name) {
        return registeredAnimations.containsKey(name);
    }

    @Nullable
    @Override
    public CaseAnimation get(String animation) {
        return registeredAnimations.get(animation);
    }

    @Override
    public Map<String, CaseAnimation> getMap() {
        return registeredAnimations;
    }

    @Override
    public Map<UUID, ActiveCase> getActiveCases() {
        return activeCases;
    }

    @Override
    public Map<Object, List<UUID>> getActiveCasesByBlock() {
        return activeCasesByBlock;
    }

    private boolean validateStartConditions(CaseData caseData, CaseAnimation animation,
                                            ConfigurationNode settings, CaseLocation location, DCPlayer player) {
        if (animation == null) {
            platform.getLogger().log(Level.WARNING, "Case animation " + caseData.getAnimation() + " does not exist!");
            return false;
        }

            if (isLocked(location)) {
                platform.getLogger().log(Level.WARNING, "Player " + player.getName() +
                        " trying to start animation while another animation is running in case: " + caseData.getCaseType());
                return false;
            }

        if (animation.isRequireSettings() && settings == null) {
            platform.getLogger().log(Level.WARNING, "Animation " + animation + " requires settings for starting!");
            return false;
        }

        if (caseData.getItems().isEmpty()) {
            platform.getLogger().log(Level.WARNING, "Player " + player.getName() +
                            " trying to start animation without items in case: " + caseData.getCaseType());
            return false;
        }

        if(!caseData.hasRealItems()) {
            platform.getLogger().log(Level.WARNING, "Player " + player.getName() +
                            " trying to start animation without real (chance > 0) items in case: " + caseData.getCaseType());
            return false;
        }

        return true;
    }

    private void saveOpenInfo(CaseData caseData, DCPlayer player, CaseDataItem item, String choice) {
        CompletableFuture.runAsync(() -> {

            CaseData.History data = new CaseData.History(item.getItemName(), caseData.getCaseType(), player.getName(), System.currentTimeMillis(), item.getGroup(), choice);
            CaseData.History[] historyData = caseData.getHistoryData();

            if (historyData.length > 0) {
                List<CaseData.History> databaseData = api.getDatabase().getHistoryData(caseData.getCaseType()).join();
                if (!databaseData.isEmpty())
                    historyData = databaseData.toArray(new CaseData.History[historyData.length]);

                System.arraycopy(historyData, 0, historyData, 1, historyData.length - 1);
                historyData[0] = data;

                for (int i = 0; i < historyData.length; i++) {
                    CaseData.History tempData = historyData[i];
                    if (tempData != null) {
                        api.getDatabase().setHistoryData(caseData.getCaseType(), i, tempData);
                    }
                }

                // Set history data in memory
                Objects.requireNonNull(api.getCaseManager().get(caseData.getCaseType())).setHistoryData(historyData);
            }
            api.getCaseOpenManager().add(caseData.getCaseType(), player.getName(), 1);
        });
    }

    /**
     * Get random choice from item random action list
     * @param item Case item
     * @return random action name
     */
    public static String getRandomActionChoice(CaseDataItem item) {
        ProbabilityCollection<String> collection = new ProbabilityCollection<>();
        for (String name : item.getRandomActions().keySet()) {
            CaseDataItem.RandomAction randomAction = item.getRandomActions().get(name);
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
    public void executeActions(DCPlayer player, CaseData caseData, CaseDataItem item, String choice, boolean alternative) {
        final String[] replacementRegex = {
                "%player%:" + player.getName(),
                "%casename%:" + caseData.getCaseType(),
                "%casedisplayname%:" + caseData.getCaseDisplayName(),
                "%casetitle%:" + caseData.getCaseGui().getTitle(),
                "%group%:" + item.getGroup(),
                "%groupdisplayname%:" + item.getMaterial().getDisplayName()
        };

        List<String> actions = DCTools.rt(getActionsBasedOnChoice(item, choice, alternative), replacementRegex);

        api.getActionManager().execute(player, actions);
    }

    /**
     * Get player primary group from Vault or LuckPerms
     * @param player Player
     * @return player primary group
     */
    public String getPlayerGroup(DCPlayer player) {
        String group = null;
        if(platform.getLuckPerms() != null) {
            User user = platform.getLuckPerms().getUserManager().getUser(player.getUniqueId());
            if(user != null) group = user.getPrimaryGroup();
        }
        return group;
    }

    /**
     * Get map of default LevelGroup from Config.yml
     * @return map of LevelGroup
     */
    private Map<String, Integer> getDefaultLevelGroup() {
        Map<String, Integer> levelGroup = new HashMap<>();
        boolean isEnabled = api.getConfig().getConfig().node("DonateCase", "LevelGroup").getBoolean();
        if(isEnabled) {
            ConfigurationNode section = api.getConfig().getConfig().node("DonateCase", "LevelGroups");

            if (section != null) {
                for (Map.Entry<Object, ? extends ConfigurationNode> entry : section.childrenMap().entrySet()) {
                    int level = entry.getValue().getInt();
                    levelGroup.put(String.valueOf(entry.getKey()), level);
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
    public static List<String> getActionsBasedOnChoice(CaseDataItem item, String choice, boolean alternative) {
        if (choice != null) {
            CaseDataItem.RandomAction randomAction = item.getRandomActions().get(choice);
            if (randomAction != null) {
                return randomAction.getActions();
            }
        }
        return alternative ? item.getAlternativeActions() : item.getActions();
    }
}