package com.jodexindustries.donatecase.common.managers;

import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.common.DonateCase;
import com.jodexindustries.donatecase.common.animations.RandomAnimation;
import com.jodexindustries.donatecase.api.data.animation.Animation;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.storage.CaseInfo;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.event.animation.AnimationEndEvent;
import com.jodexindustries.donatecase.api.event.animation.AnimationPreStartEvent;
import com.jodexindustries.donatecase.api.event.animation.AnimationStartEvent;
import com.jodexindustries.donatecase.api.manager.AnimationManager;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.api.tools.ProbabilityCollection;
import com.jodexindustries.donatecase.common.platform.BackendPlatform;
import net.luckperms.api.model.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class AnimationManagerImpl implements AnimationManager {

    private final static Map<String, CaseAnimation> registeredAnimations = new HashMap<>();
    private final static Map<UUID, ActiveCase> activeCases = new HashMap<>();
    private final static Map<Object, List<UUID>> activeCasesByBlock = new HashMap<>();

    private final DonateCase api;
    private final BackendPlatform backend;

    public AnimationManagerImpl(DonateCase api) {
        this.api = api;
        this.backend = api.getPlatform();

        List<? extends CaseAnimation> defaultAnimations = Arrays.asList(
                CaseAnimation.builder()
                        .name("RANDOM")
                        .addon(backend)
                        .animation(RandomAnimation.class)
                        .description("Selects the random animation from config")
                        .requireSettings(true)
                        .requireBlock(true)
                        .build()
        );

        defaultAnimations.forEach(this::register);
    }

    @Override
    public boolean register(CaseAnimation animation) {
        String name = animation.getName();

        if(!isRegistered(name)) {
            registeredAnimations.put(name, animation);
            return true;
        } else {
            backend.getLogger().warning("Animation " + name + " already registered!");
        }
        return false;
    }

    @Override
    public void unregister(@NotNull String name) {
        if (isRegistered(name)) {
            registeredAnimations.remove(name);
        } else {
            backend.getLogger().warning("Animation with name " + name + " already unregistered!");
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

        ConfigurationNode settings = caseData.getAnimationSettings().isNull() ? api.getConfigManager().getAnimations().node(animation) : caseData.getAnimationSettings();

        CaseLocation temp = location.clone();

        if (!validateStartConditions(caseData, caseAnimation, settings, temp, player)) {
            return CompletableFuture.completedFuture(null);
        }

        assert caseAnimation != null;

        caseData = caseData.clone();
        caseData.setItems(DCTools.sortItemsByIndex(caseData.getItems()));


        CaseDataItem winItem = caseData.getRandomItem();
        winItem.getMaterial().setDisplayName(api.getPlatform().getPAPI().setPlaceholders(player, winItem.getMaterial().getDisplayName()));

        AnimationPreStartEvent event = new AnimationPreStartEvent(player, caseData, temp, winItem);
        api.getEventBus().post(event);

        winItem = event.getWinItem();

        UUID uuid = UUID.randomUUID();
        ActiveCase activeCase = new ActiveCase(uuid, temp, player, winItem, caseData.getCaseType());
        activeCase.setLocked(caseAnimation.isRequireBlock());

        CompletableFuture<UUID> animationCompletion = new CompletableFuture<>();

        if (caseAnimation.isRequireBlock()) {
            CaseInfo info = api.getConfigManager().getCaseStorage().get(temp);
            if (info != null) {
                CaseLocation caseLocation = info.getLocation();
                temp.setPitch(caseLocation.getPitch());
                temp.setYaw(caseLocation.getYaw());
            }

            CaseData.Hologram hologram = caseData.getHologram();
            if (hologram != null && hologram.isEnabled()) api.getHologramManager().remove(temp);

            for (CaseGuiWrapper gui : api.getGUIManager().getMap().values()) {
                if (gui.getLocation().equals(temp)) {
                    gui.getPlayer().closeInventory();
                }
            }
        }

        Class<? extends Animation> animationClass = caseAnimation.getAnimation();

        try {

            Animation javaAnimation = animationClass.getDeclaredConstructor().newInstance();
            javaAnimation.init(player, temp.clone(), uuid, caseData, winItem, settings);

            activeCases.put(uuid, activeCase);
            activeCasesByBlock.computeIfAbsent(temp, k -> new ArrayList<>()).add(uuid);

            api.getPlatform().getScheduler().run(backend, () -> {
                try {
                    javaAnimation.start();
                    animationCompletion.complete(uuid);
                    api.getEventBus().post(new AnimationStartEvent(player, activeCase));
                } catch (Throwable t) {
                    backend.getLogger().log(Level.WARNING, "Error with starting animation " + animation, t);
                    if (caseAnimation.isRequireBlock()) activeCasesByBlock.remove(temp);
                    activeCases.remove(uuid);
                    animationCompletion.complete(null);
                }
            }, delay);

        } catch (Throwable t) {
            backend.getLogger().log(Level.WARNING, "Error with starting animation " + animation, t);
            if (caseAnimation.isRequireBlock()) activeCasesByBlock.remove(location);
            animationCompletion.complete(null);
        }

        return animationCompletion;
    }

    @Override
    public void preEnd(UUID uuid) {
        ActiveCase activeCase = activeCases.get(uuid);
        if(activeCase == null) {
            backend.getLogger().warning("Animation with uuid: " + uuid + " not found!");
            return;
        }

        CaseData caseData = api.getCaseManager().get(activeCase.getCaseType());
        if (caseData != null) {
            preEnd(caseData, activeCase.getPlayer(), activeCase.getWinItem());
        }
    }

    @Override
    public void preEnd(CaseData caseData, DCPlayer player, CaseDataItem item) {
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
            backend.getLogger().warning("Animation with uuid: " + uuid + " not found!");
            return;
        }

        animationEnd(activeCase);
    }

    private void animationEnd(@NotNull ActiveCase activeCase) {
        CaseData caseData = api.getCaseManager().get(activeCase.getCaseType());
        if(caseData == null) return;

        CaseAnimation caseAnimation = get(caseData.getAnimation());
        if(caseAnimation == null) return;

        DCPlayer player = activeCase.getPlayer();

        if(!activeCase.isKeyRemoved()) api.getCaseKeyManager().remove(caseData.getCaseType(), player.getName(), 1);

        CaseLocation block = activeCase.getBlock();
        activeCases.remove(activeCase.getUuid());
        activeCasesByBlock.remove(block);

        if(caseAnimation.isRequireBlock()) {
            CaseData.Hologram hologram = caseData.getHologram();
            if (hologram != null && hologram.isEnabled()) api.getHologramManager().create(block, hologram);
        }

        api.getEventBus().post(new AnimationEndEvent(player, activeCase));
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
            backend.getLogger().log(Level.WARNING, "Case animation " + caseData.getAnimation() + " does not exist!");
            return false;
        }

            if (isLocked(location)) {
                backend.getLogger().log(Level.WARNING, "Player " + player.getName() +
                        " trying to start animation while another animation is running in case: " + caseData.getCaseType());
                return false;
            }

        if (animation.isRequireSettings() && settings == null) {
            backend.getLogger().log(Level.WARNING, "Animation " + animation + " requires settings for starting!");
            return false;
        }

        if (caseData.getItems().isEmpty()) {
            backend.getLogger().log(Level.WARNING, "Player " + player.getName() +
                            " trying to start animation without items in case: " + caseData.getCaseType());
            return false;
        }

        if(!caseData.hasRealItems()) {
            backend.getLogger().log(Level.WARNING, "Player " + player.getName() +
                            " trying to start animation without real (chance > 0) items in case: " + caseData.getCaseType());
            return false;
        }

        return true;
    }

    private void saveOpenInfo(CaseData caseData, DCPlayer player, CaseDataItem item, String choice) {
        backend.getScheduler().async(backend, () -> {

            CaseData.History data = new CaseData.History(
                    item.getName(),
                    caseData.getCaseType(),
                    player.getName(),
                    System.currentTimeMillis(),
                    item.getGroup(),
                    choice
            );

            List<CaseData.History> databaseData = api.getDatabase().getHistoryData(caseData.getCaseType()).join();
            if (!databaseData.isEmpty()) {
                CaseData.History[] historyData = new CaseData.History[databaseData.size()];

                System.arraycopy(databaseData.toArray(new CaseData.History[0]), 0, historyData, 1, databaseData.size() - 1);

                historyData[0] = data;

                for (int i = 0; i < historyData.length; i++) {
                    if (historyData[i] != null) {
                        api.getDatabase().setHistoryData(caseData.getCaseType(), i, historyData[i]);
                    }
                }
            } else {
                api.getDatabase().setHistoryData(caseData.getCaseType(), 0, data);
            }

            api.getCaseOpenManager().add(caseData.getCaseType(), player.getName(), 1);
        }, 0L);
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
        if(backend.getLuckPerms() != null) {
            User user = backend.getLuckPerms().getUserManager().getUser(player.getUniqueId());
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
        boolean isEnabled = api.getConfigManager().getConfig().node("DonateCase", "LevelGroup").getBoolean();
        if(isEnabled) {
            ConfigurationNode section = api.getConfigManager().getConfig().node("DonateCase", "LevelGroups");

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