package com.jodexindustries.donatecase.common.managers;

import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.tools.ProbabilityCollection;
import com.jodexindustries.donatecase.common.DonateCase;
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
import com.jodexindustries.donatecase.common.platform.BackendPlatform;
import com.jodexindustries.donatecase.common.tools.LocalPlaceholder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class AnimationManagerImpl implements AnimationManager {

    private final static Map<String, CaseAnimation> registeredAnimations = new ConcurrentHashMap<>();
    private final static Map<UUID, ActiveCase> activeCases = new ConcurrentHashMap<>();
    private final static Map<CaseLocation, List<UUID>> activeCasesByBlock = new ConcurrentHashMap<>();

    private final DonateCase api;
    private final BackendPlatform backend;

    public AnimationManagerImpl(DonateCase api) {
        this.api = api;
        this.backend = api.getPlatform();
    }

    @Override
    public boolean register(CaseAnimation animation) {
        String name = animation.getName();

        if (!isRegistered(name)) {
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
        return start(player, location, caseData, false, delay);
    }

    @Override
    public CompletableFuture<UUID> start(@NotNull DCPlayer player, @NotNull CaseLocation location, @NotNull CaseData caseData, boolean keyRemoved, int delay) {
        CaseData data = caseData.clone();

        String animation;
        if (!data.animation().equalsIgnoreCase("RANDOM")) {
            animation = data.animation();
        } else {
            animation = getRandomAnimation(getSettings(data));
            data.animation(animation);
        }

        ConfigurationNode settings = getSettings(data);

        CaseLocation temp = location.clone();

        CaseAnimation caseAnimation = get(animation);

        if (!validateStartConditions(data, caseAnimation, settings, temp, player)) {
            return CompletableFuture.completedFuture(null);
        }

        assert caseAnimation != null;

        data.items(DCTools.sortItemsByIndex(data.items()));


        CaseDataItem winItem = data.getRandomItem();
        winItem.material().displayName(api.getPlatform().getPAPI().setPlaceholders(player, winItem.material().displayName()));

        AnimationPreStartEvent event = new AnimationPreStartEvent(player, data, temp, winItem);
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

            CaseData.Hologram hologram = data.hologram();
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
            javaAnimation.init(player, temp.clone(), uuid, data, winItem, settings);

            ActiveCase activeCase = new ActiveCase(uuid, temp, player, winItem, data.caseType(), javaAnimation);
            activeCase.locked(caseAnimation.isRequireBlock());
            activeCase.keyRemoved(keyRemoved);

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
        CaseDataItem.RandomAction randomAction = item.giveType().equalsIgnoreCase("ONE") ? null : item.getRandomAction();
        Map<String, Integer> levelGroups = api.getConfigManager().getConfig().levelGroups();
        if (!caseData.levelGroups().isEmpty()) levelGroups = caseData.levelGroups();

        String primaryGroup = backend.getLuckPermsSupport().getPrimaryGroup(player.getUniqueId());

        executeActions(player, caseData, item, randomAction,
                isBetterOrEqual(
                        levelGroups, primaryGroup, item.group()
                )
        );

        saveOpenInfo(caseData, player, item, randomAction);
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

    private void saveOpenInfo(@NotNull CaseData caseData,
                              @NotNull DCPlayer player,
                              @NotNull CaseDataItem item,
                              @Nullable CaseDataItem.RandomAction action) {
        backend.getScheduler().async(backend, () -> {
            CaseData.History newEntry = new CaseData.History(
                    item.getName(),
                    caseData.caseType(),
                    player.getName(),
                    System.currentTimeMillis(),
                    item.group(),
                    action == null ? null : action.getName()
            );

            api.getDatabase().addHistory(caseData.caseType(), newEntry, caseData.historyDataSize());

            api.getCaseOpenManager().add(caseData.caseType(), player.getName(), 1);
        }, 0L);
    }

    public void executeActions(DCPlayer player, CaseData caseData, CaseDataItem item, CaseDataItem.RandomAction randomAction, boolean alternative) {
        Collection<LocalPlaceholder> placeholders = LocalPlaceholder.of(caseData);
        placeholders.add(LocalPlaceholder.of("%player%", player.getName()));
        placeholders.addAll(LocalPlaceholder.of(item));

        List<String> actions = DCTools.rt(item.getActionsBasedOnChoice(randomAction, alternative), placeholders);

        api.getActionManager().execute(player, actions);
    }

    public static boolean isBetterOrEqual(Map<String, Integer> groupLevels, String playerGroup, String rewardGroup) {
        Integer playerLevel = groupLevels.get(playerGroup);
        Integer rewardLevel = groupLevels.get(rewardGroup);

        return playerLevel != null && rewardLevel != null && playerLevel >= rewardLevel;
    }

    public String getRandomAnimation(ConfigurationNode settings) {
        ProbabilityCollection<String> collection = new ProbabilityCollection<>();
        settings.childrenMap().forEach((key, value) ->
                collection.add((String) key, value.getInt()));
        return collection.get();
    }

    private ConfigurationNode getSettings(CaseData caseData) {
        return caseData.animationSettings().isNull() ? api.getConfigManager().getAnimations().node(caseData.animation()) : caseData.animationSettings();
    }

}