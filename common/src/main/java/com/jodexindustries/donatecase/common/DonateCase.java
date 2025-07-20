package com.jodexindustries.donatecase.common;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.PowerReason;
import com.jodexindustries.donatecase.api.database.CaseDatabase;
import com.jodexindustries.donatecase.api.manager.CaseKeyManager;
import com.jodexindustries.donatecase.api.manager.CaseOpenManager;
import com.jodexindustries.donatecase.common.config.CaseLoader;
import com.jodexindustries.donatecase.common.config.ConfigManagerImpl;
import com.jodexindustries.donatecase.common.database.CaseDatabaseImpl;
import com.jodexindustries.donatecase.common.event.EventBusImpl;
import com.jodexindustries.donatecase.common.event.EventListener;
import com.jodexindustries.donatecase.common.managers.*;
import com.jodexindustries.donatecase.common.platform.BackendPlatform;
import com.jodexindustries.donatecase.common.tools.updater.UpdateChecker;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class DonateCase extends DCAPI {

    private final ActionManagerImpl actionManager;
    private final AddonManagerImpl addonManager;
    private final AnimationManagerImpl animationManager;
    private final GUIManagerImpl guiManager;
    private final GUITypedItemManagerImpl guiTypedItemManager;
    private final MaterialManagerImpl materialManager;
    private final SubCommandManagerImpl subCommandManager;
    private final CaseKeyManagerImpl caseKeyManager;
    private final CaseOpenManagerImpl caseOpenManager;
    private final CaseManagerImpl caseManager;
    private final HologramManagerImpl hologramManager;

    private final CaseDatabaseImpl database;
    private final ConfigManagerImpl configManager;
    private final CaseLoader caseLoader;
    @Getter
    private final UpdateChecker updateChecker;
    private final EventBusImpl eventBus;
    private final EventListener eventListener;

    private final BackendPlatform platform;

    public DonateCase(BackendPlatform platform) {
        this.platform = platform;

        this.actionManager = new ActionManagerImpl(this);
        this.addonManager = new AddonManagerImpl(this);
        this.animationManager = new AnimationManagerImpl(this);
        this.guiManager = new GUIManagerImpl(this);
        this.guiTypedItemManager = new GUITypedItemManagerImpl(this);
        this.materialManager = new MaterialManagerImpl(this);
        this.subCommandManager = new SubCommandManagerImpl(this);
        this.caseKeyManager = new CaseKeyManagerImpl(this);
        this.caseOpenManager = new CaseOpenManagerImpl(this);
        this.caseManager = new CaseManagerImpl();
        this.hologramManager = new HologramManagerImpl(this);

        this.database = new CaseDatabaseImpl(this);
        this.configManager = new ConfigManagerImpl(platform);
        this.caseLoader = new CaseLoader(this);
        this.updateChecker = new UpdateChecker(this);
        this.eventBus = new EventBusImpl(platform.getLogger());
        this.eventListener = new EventListener(this);
    }

    @ApiStatus.Internal
    public static void setInstance(DCAPI instance) {
        DCAPI.instance = instance;
    }

    public void load() {
        long time = System.currentTimeMillis();
        addonManager.load();

        configManager.load();
        caseLoader.load();
        hologramManager.load();
        updateChecker.check();
        database.connect();
        eventBus.register(eventListener);
        addonManager.enable(PowerReason.DONATE_CASE);

        platform.getLogger().info("Enabled in " + (System.currentTimeMillis() - time) + "ms");
    }

    public void unload() {
        eventBus.unregisterAll();

        addonManager.unload(PowerReason.DONATE_CASE);
        animationManager.unregister();
        subCommandManager.unregister();
        actionManager.unregister();
        materialManager.unregister();
        guiTypedItemManager.unregister();
        hologramManager.remove();

        database.close();

        guiManager.getMap().values().parallelStream().forEach(gui -> gui.getPlayer().closeInventory());

        clear();
        setInstance(null);
    }

    @Override
    public @NotNull ActionManagerImpl getActionManager() {
        return actionManager;
    }

    @Override
    public @NotNull AddonManagerImpl getAddonManager() {
        return addonManager;
    }

    @Override
    public @NotNull AnimationManagerImpl getAnimationManager() {
        return animationManager;
    }

    @Override
    public @NotNull CaseKeyManagerImpl getCaseKeyManager() {
        return caseKeyManager;
    }

    @Override
    public @NotNull CaseManagerImpl getCaseManager() {
        return caseManager;
    }

    @Override
    public @NotNull CaseOpenManagerImpl getCaseOpenManager() {
        return caseOpenManager;
    }

    @Override
    public @NotNull GUIManagerImpl getGUIManager() {
        return guiManager;
    }

    @Override
    public @NotNull GUITypedItemManagerImpl getGuiTypedItemManager() {
        return guiTypedItemManager;
    }

    @Override
    public @NotNull MaterialManagerImpl getMaterialManager() {
        return materialManager;
    }

    @Override
    public @NotNull SubCommandManagerImpl getSubCommandManager() {
        return subCommandManager;
    }

    @Override
    public @NotNull HologramManagerImpl getHologramManager() {
        return hologramManager;
    }

    @Override
    public @NotNull CaseDatabaseImpl getDatabase() {
        return database;
    }

    public @NotNull ConfigManagerImpl getConfigManager() {
        return configManager;
    }

    @Override
    public @NotNull CaseLoader getCaseLoader() {
        return caseLoader;
    }

    @Override
    public @NotNull EventBusImpl getEventBus() {
        return eventBus;
    }

    @Override
    public @NotNull BackendPlatform getPlatform() {
        return platform;
    }

    @Override
    public void clear() {
        getCaseManager().caseDefinitionMap.clear();
        getAnimationManager().activeCases.clear();
        getAnimationManager().activeCasesByBlock.clear();
        CaseOpenManager.cache.clear();
        CaseKeyManager.cache.clear();
        CaseDatabase.cache.clear();
    }
}
