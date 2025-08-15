package com.jodexindustries.donatecase.common.managers;

import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.GiveType;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseItem;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseSettings;
import com.jodexindustries.donatecase.api.scheduler.DCFuture;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class AnimationManagerImpl implements AnimationManager {

    public final Map<String, CaseAnimation> registeredAnimations = new ConcurrentHashMap<>();
    public final Map<UUID, ActiveCase> activeCases = new ConcurrentHashMap<>();
    public final Map<CaseLocation, List<UUID>> activeCasesByBlock = new ConcurrentHashMap<>();

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
    public DCFuture<UUID> start(@NotNull DCPlayer player, @NotNull CaseLocation location, @NotNull CaseDefinition definition) {
        return start(player, location, definition, definition.settings().cooldownBeforeAnimation());
    }

    @Override
    public DCFuture<UUID> start(@NotNull DCPlayer player, @NotNull CaseLocation location, @NotNull CaseDefinition definition, int delay) {
        return start(player, location, definition, false, delay);
    }

    @Override
    public DCFuture<UUID> start(@NotNull DCPlayer player, @NotNull CaseLocation location, @NotNull CaseDefinition caseDefinition, boolean keyRemoved, int delay) {
        DCFuture<UUID> animationCompletion = new DCFuture<>();

        CaseDefinition definition = caseDefinition.clone();

        String animation;
        if (!definition.settings().animation().equalsIgnoreCase("RANDOM")) {
            animation = definition.settings().animation();
        } else {
            animation = getRandomAnimation(getSettings(definition));
            definition.settings().animation(animation);
        }

        ConfigurationNode settings = getSettings(definition);

        CaseLocation temp = location.clone();

        CaseAnimation caseAnimation = get(animation);

        if (!validateStartConditions(definition, caseAnimation, settings, temp, player)) {
            animationCompletion.complete(null);
            return animationCompletion;
        }

        assert caseAnimation != null;

        definition.items().items(DCTools.sortItemsByIndex(definition.items().items()));

        CaseItem winItem = definition.items().getRandomItem();
        winItem.material().displayName(api.getPlatform().getPAPI().setPlaceholders(player, winItem.material().displayName()));

        AnimationPreStartEvent event = new AnimationPreStartEvent(player, definition, temp, winItem);
        api.getEventBus().post(event);

        winItem = event.winItem();

        UUID uuid = UUID.randomUUID();

        if (caseAnimation.isRequireBlock()) {
            CaseInfo info = api.getConfigManager().getCaseStorage().get(temp);
            if (info != null) {
                CaseLocation caseLocation = info.location();
                temp.pitch(caseLocation.pitch());
                temp.yaw(caseLocation.yaw());
            }

            CaseSettings.Hologram hologram = definition.settings().hologram();
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
            javaAnimation.init(player, temp.clone(), uuid, definition, winItem, settings);

            ActiveCase activeCase = new ActiveCase(uuid, temp, player, winItem, caseDefinition.clone(), javaAnimation);
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
        if (activeCase == null) {
            backend.getLogger().warning("Animation with uuid: " + uuid + " not found!");
            return;
        }

        Optional<CaseDefinition> optional = api.getCaseManager().getByType(activeCase.caseType());
        optional.ifPresent(definition -> preEnd(definition, activeCase.player(), activeCase.winItem()));
    }

    @Override
    public void preEnd(CaseDefinition definition, DCPlayer player, CaseItem item) {
        CaseItem.RandomAction randomAction = item.giveType() == GiveType.ONE ? null : item.getRandomAction();
        CaseSettings.LevelGroups levelGroups = api.getConfigManager().getConfig().levelGroups();
        if (!definition.settings().levelGroups().map().isEmpty()) levelGroups = definition.settings().levelGroups();

        String primaryGroup = backend.getLuckPermsSupport().getPrimaryGroup(player.getUniqueId());

        boolean alternative = levelGroups.isBetterOrEqual(primaryGroup, item.group());

        List<String> actions = alternative ? item.alternativeActions() : randomAction == null ? item.actions() : randomAction.actions();

        executeActions(player, definition, item, actions);

        saveOpenInfo(definition, player, item, randomAction);
    }

    @Override
    public void end(UUID uuid) {
        ActiveCase activeCase = activeCases.get(uuid);
        if (activeCase == null) {
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
        if (!activeCase.keyRemoved()) api.getCaseKeyManager().remove(activeCase.caseType(), player.getName(), 1);

        api.getEventBus().post(new AnimationEndEvent(activeCase));

        CaseDefinition definition = activeCase.definition();

        CaseAnimation caseAnimation = get(definition.settings().animation());
        if (caseAnimation != null && caseAnimation.isRequireBlock())
            api.getHologramManager().create(block, definition.settings().hologram());
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

    private boolean validateStartConditions(CaseDefinition definition, CaseAnimation animation,
                                            ConfigurationNode settings, CaseLocation location, DCPlayer player) {
        if (animation == null) {
            backend.getLogger().log(Level.WARNING, "Case animation " + definition.settings().animation() + " does not exist!");
            return false;
        }

        if (location.getWorld() == null || location.getWorld().name() == null) {
            backend.getLogger().warning("Player " + player.getName() +
                    " trying to start animation without world name in case: " + definition.settings().type() +
                    " Check the Cases.yml file!");
            return false;
        }

        if (isLocked(location)) {
            backend.getLogger().warning("Player " + player.getName() +
                    " trying to start animation while another animation is running in case: " + definition.settings().type());
            return false;
        }

        if (animation.isRequireSettings() && settings == null) {
            backend.getLogger().warning("Animation " + animation + " requires settings for starting!");
            return false;
        }

        if (definition.items().items().isEmpty()) {
            backend.getLogger().warning("Player " + player.getName() +
                    " trying to start animation without items in case: " + definition.settings().type());
            return false;
        }

        if (!definition.items().hasRealItems()) {
            backend.getLogger().warning("Player " + player.getName() +
                    " trying to start animation without real (chance > 0) items in case: " + definition.settings().type());
            return false;
        }

        return true;
    }

    private void saveOpenInfo(@NotNull CaseDefinition definition,
                              @NotNull DCPlayer player,
                              @NotNull CaseItem item,
                              @Nullable CaseItem.RandomAction action) {
        backend.getScheduler().async(backend, () -> {
            CaseData.History newEntry = new CaseData.History(
                    item.name(),
                    definition.settings().type(),
                    player.getName(),
                    System.currentTimeMillis(),
                    item.group(),
                    action == null ? null : action.name()
            );

            api.getDatabase().addHistory(definition.settings().type(), newEntry, definition.settings().historyDataSize());

            api.getCaseOpenManager().add(definition.settings().type(), player.getName(), 1);
        }, 0L);
    }

    public void executeActions(DCPlayer player, CaseDefinition caseData, CaseItem item, List<String> actions) {
        Collection<LocalPlaceholder> placeholders = LocalPlaceholder.of(caseData);
        placeholders.add(LocalPlaceholder.of("%player%", player.getName()));
        placeholders.addAll(LocalPlaceholder.of(item));

        api.getActionManager().execute(player, DCTools.rt(actions, placeholders));
    }

    public String getRandomAnimation(ConfigurationNode settings) {
        ProbabilityCollection<String> collection = new ProbabilityCollection<>();
        settings.childrenMap().forEach((key, value) ->
                collection.add((String) key, value.getInt()));
        return collection.get();
    }

    private ConfigurationNode getSettings(CaseDefinition definition) {
        return definition.settings().animationSettings().isNull() ?
                api.getConfigManager().getAnimations().node(definition.settings().animation()) : definition.settings().animationSettings();
    }

}