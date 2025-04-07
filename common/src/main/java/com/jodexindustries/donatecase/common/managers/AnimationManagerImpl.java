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
import com.jodexindustries.donatecase.common.tools.LocalPlaceholder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class AnimationManagerImpl implements AnimationManager {

    private final static Map<String, CaseAnimation> registeredAnimations = new HashMap<>();
    private final static Map<UUID, ActiveCase> activeCases = new HashMap<>();
    private final static Map<CaseLocation, List<UUID>> activeCasesByBlock = new HashMap<>();

    private final DonateCase api;
    private final BackendPlatform backend;

    public AnimationManagerImpl(DonateCase api) {
        this.api = api;
        this.backend = api.getPlatform();

        List<? extends CaseAnimation> defaultAnimations = Collections.singletonList(
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
        return start(player, location, caseData, caseData.cooldownBeforeStart());
    }

    @Override
    public CompletableFuture<UUID> start(@NotNull DCPlayer player, @NotNull CaseLocation location, @NotNull CaseData caseData, int delay) {
        String animation = caseData.animation();
        CaseAnimation caseAnimation = get(animation);

        ConfigurationNode settings = caseData.animationSettings().isNull() ? api.getConfigManager().getAnimations().node(animation) : caseData.animationSettings();

        CaseLocation temp = location.clone();

        if (!validateStartConditions(caseData, caseAnimation, settings, temp, player)) {
            return CompletableFuture.completedFuture(null);
        }

        assert caseAnimation != null;

        caseData = caseData.clone();
        caseData.items(DCTools.sortItemsByIndex(caseData.items()));


        CaseDataItem winItem = caseData.getRandomItem();
        winItem.material().displayName(api.getPlatform().getPAPI().setPlaceholders(player, winItem.material().displayName()));

        AnimationPreStartEvent event = new AnimationPreStartEvent(player, caseData, temp, winItem);
        api.getEventBus().post(event);

        winItem = event.winItem();

        UUID uuid = UUID.randomUUID();

        CompletableFuture<UUID> animationCompletion = new CompletableFuture<>();

        if (caseAnimation.isRequireBlock()) {
            CaseInfo info = api.getConfigManager().getCaseStorage().get(temp);
            if (info != null) {
                CaseLocation caseLocation = info.location();
                temp.pitch(caseLocation.pitch());
                temp.yaw(caseLocation.yaw());
            }

            CaseData.Hologram hologram = caseData.hologram();
            if (hologram != null && hologram.enabled()) api.getHologramManager().remove(temp);

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

            ActiveCase activeCase = new ActiveCase(uuid, temp, player, winItem, caseData.caseType(), javaAnimation);
            activeCase.locked(caseAnimation.isRequireBlock());

            activeCases.put(uuid, activeCase);
            activeCasesByBlock.computeIfAbsent(temp, k -> new ArrayList<>()).add(uuid);

            api.getPlatform().getScheduler().run(backend, () -> {
                try {
                    javaAnimation.start();
                    animationCompletion.complete(uuid);
                    api.getEventBus().post(new AnimationStartEvent(activeCase));
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

        CaseData caseData = api.getCaseManager().get(activeCase.caseType());
        if (caseData != null) {
            preEnd(caseData, activeCase.player(), activeCase.winItem());
        }
    }

    @Override
    public void preEnd(CaseData caseData, DCPlayer player, CaseDataItem item) {
        String choice = "";
        Map<String, Integer> levelGroups = getDefaultLevelGroup();
        if(!caseData.levelGroups().isEmpty()) levelGroups = caseData.levelGroups();

        String primaryGroup = backend.getLuckPermsSupport().getPrimaryGroup(player.getUniqueId());
        if(isAlternative(levelGroups, primaryGroup, item.group())) {
            executeActions(player, caseData, item, null, true);
        } else {
            if (item.giveType().equalsIgnoreCase("ONE")) {
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
        CaseLocation block = activeCase.block();
        activeCases.remove(activeCase.uuid());
        activeCasesByBlock.remove(block);

        DCPlayer player = activeCase.player();
        if(!activeCase.keyRemoved()) api.getCaseKeyManager().remove(activeCase.caseType(), player.getName(), 1);

        api.getEventBus().post(new AnimationEndEvent(activeCase));

        CaseData caseData = api.getCaseManager().get(activeCase.caseType());
        if(caseData == null) return;

        CaseAnimation caseAnimation = get(caseData.animation());
        if(caseAnimation == null) return;

        if(caseAnimation.isRequireBlock()) {
            CaseData.Hologram hologram = caseData.hologram();
            if (hologram != null && hologram.enabled()) api.getHologramManager().create(block, hologram);
        }

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
    public Map<CaseLocation, List<UUID>> getActiveCasesByBlock() {
        return activeCasesByBlock;
    }

    private boolean validateStartConditions(CaseData caseData, CaseAnimation animation,
                                            ConfigurationNode settings, CaseLocation location, DCPlayer player) {
        if (animation == null) {
            backend.getLogger().log(Level.WARNING, "Case animation " + caseData.animation() + " does not exist!");
            return false;
        }

        if (location.getWorld() == null || location.getWorld().name() == null) {
            backend.getLogger().warning("Player " + player.getName() +
                    " trying to start animation without world name in case: " + caseData.caseType() +
                    " Check the Cases.yml file!");
            return false;
        }

        if (isLocked(location)) {
            backend.getLogger().warning("Player " + player.getName() +
                    " trying to start animation while another animation is running in case: " + caseData.caseType());
            return false;
        }

        if (animation.isRequireSettings() && settings == null) {
            backend.getLogger().warning("Animation " + animation + " requires settings for starting!");
            return false;
        }

        if (caseData.items().isEmpty()) {
            backend.getLogger().warning("Player " + player.getName() +
                    " trying to start animation without items in case: " + caseData.caseType());
            return false;
        }

        if (!caseData.hasRealItems()) {
            backend.getLogger().warning("Player " + player.getName() +
                    " trying to start animation without real (chance > 0) items in case: " + caseData.caseType());
            return false;
        }

        return true;
    }

    private void saveOpenInfo(CaseData caseData, DCPlayer player, CaseDataItem item, String choice) {
        backend.getScheduler().async(backend, () -> {

            CaseData.History data = new CaseData.History(
                    item.getName(),
                    caseData.caseType(),
                    player.getName(),
                    System.currentTimeMillis(),
                    item.group(),
                    choice
            );

            List<CaseData.History> databaseData = api.getDatabase().getHistoryData(caseData.caseType()).join();
            if (!databaseData.isEmpty()) {
                CaseData.History[] historyData = new CaseData.History[databaseData.size()];

                System.arraycopy(databaseData.toArray(new CaseData.History[0]), 0, historyData, 1, databaseData.size() - 1);

                historyData[0] = data;

                for (int i = 0; i < historyData.length; i++) {
                    if (historyData[i] != null) {
                        api.getDatabase().setHistoryData(caseData.caseType(), i, historyData[i]);
                    }
                }
            } else {
                api.getDatabase().setHistoryData(caseData.caseType(), 0, data);
            }

            api.getCaseOpenManager().add(caseData.caseType(), player.getName(), 1);
        }, 0L);
    }

    /**
     * Get random choice from item random action list
     * @param item Case item
     * @return random action name
     */
    public static String getRandomActionChoice(CaseDataItem item) {
        ProbabilityCollection<String> collection = new ProbabilityCollection<>();
        for (String name : item.randomActions().keySet()) {
            CaseDataItem.RandomAction randomAction = item.randomActions().get(name);
            if(randomAction == null) continue;
            collection.add(name, randomAction.chance());
        }
        return collection.get();
    }

    /**
     * Execute actions after case opening
     * @param player Player, who opened
     * @param caseData Case that was opened
     * @param item The prize that was won
     * @param choice In fact, these are actions that were selected from the RandomActions section
     * @param alternative If true, the item's alternative actions will be selected. (Same as {@link CaseDataItem#randomActions()})
     */
    public void executeActions(DCPlayer player, CaseData caseData, CaseDataItem item, String choice, boolean alternative) {
        Collection<LocalPlaceholder> placeholders = LocalPlaceholder.of(caseData);
        placeholders.add(LocalPlaceholder.of("%player%", player.getName()));
        placeholders.addAll(LocalPlaceholder.of(item));

        List<String> actions = DCTools.rt(getActionsBasedOnChoice(item, choice, alternative), placeholders);

        api.getActionManager().execute(player, actions);
    }

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
     * @param alternative If true, the item's alternative actions will be selected. (Same as {@link CaseDataItem#alternativeActions()})
     * @return list of selected actions
     */
    public static List<String> getActionsBasedOnChoice(CaseDataItem item, String choice, boolean alternative) {
        if (choice != null) {
            CaseDataItem.RandomAction randomAction = item.randomActions().get(choice);
            if (randomAction != null) {
                return randomAction.actions();
            }
        }
        return alternative ? item.alternativeActions() : item.actions();
    }
}