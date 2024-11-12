package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.addon.external.ExternalJavaAddon;
import com.jodexindustries.donatecase.api.holograms.HologramManager;
import com.jodexindustries.donatecase.impl.managers.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Class for getting all DonateCase managers
 */
public class CaseManager {
    private final AddonManagerImpl addonManager;
    private final AnimationManagerImpl animationManager;
    private final SubCommandManagerImpl subCommandManager;
    private final ActionManagerImpl actionManager;
    private final MaterialManagerImpl materialManager;
    private final GUITypedItemManagerImpl guiTypedItemManager;
    private final Addon addon;

    /**
     * Constructor for initializing addon's CaseManager
     *
     * @param addon Internal addon
     */
    public CaseManager(@NotNull Addon addon) {
        this.addon = addon;
        this.addonManager = new AddonManagerImpl(addon);
        this.subCommandManager = new SubCommandManagerImpl(addon);
        this.animationManager = new AnimationManagerImpl(addon);
        this.actionManager = new ActionManagerImpl(addon);
        this.materialManager = new MaterialManagerImpl(addon);
        this.guiTypedItemManager = new GUITypedItemManagerImpl(addon);
    }

    /**
     * Constructor for initializing addon's CaseManager
     *
     * @param plugin External addon
     */
    public CaseManager(@NotNull Plugin plugin) {
        this.addon = new ExternalJavaAddon(plugin);
        this.addonManager = new AddonManagerImpl(addon);
        this.subCommandManager = new SubCommandManagerImpl(addon);
        this.animationManager = new AnimationManagerImpl(addon);
        this.actionManager = new ActionManagerImpl(addon);
        this.materialManager = new MaterialManagerImpl(addon);
        this.guiTypedItemManager = new GUITypedItemManagerImpl(addon);
    }

    /**
     * Get plugin instance
     *
     * @return DonateCase instance
     */
    @NotNull
    public DonateCase getInstance() {
        return Case.getInstance();
    }

    /**
     * Get addon manager for addons manipulate
     *
     * @return AddonManager instance
     */
    @NotNull
    public AddonManagerImpl getAddonManager() {
        return this.addonManager;
    }

    /**
     * Get animation manager for animations manipulate
     *
     * @return AnimationManager instance
     */
    @NotNull
    public AnimationManagerImpl getAnimationManager() {
        return this.animationManager;
    }

    /**
     * Get sub command manager
     *
     * @return SubCommandManager instance
     */
    @NotNull
    public SubCommandManagerImpl getSubCommandManager() {
        return this.subCommandManager;
    }

    /**
     * Get hologram manager
     *
     * @return HologramManager instance
     */
    @Nullable
    public static HologramManager getHologramManager() {
        return Case.getInstance().hologramManager;
    }

    /**
     * Get case action manager
     *
     * @return ActionManager instance
     */
    @NotNull
    public ActionManagerImpl getActionManager() {
        return actionManager;
    }

    /**
     * Get case material manager
     *
     * @return MaterialManager instance
     * @since 2.2.4.8
     */
    @NotNull
    public MaterialManagerImpl getMaterialManager() {
        return materialManager;
    }

    /**
     * Get GUI typed item manager
     *
     * @return GUITypedItemManager instance
     * @since 2.2.4.9
     */
    @NotNull
    public GUITypedItemManagerImpl getGuiTypedItemManager() {
        return guiTypedItemManager;
    }

    /**
     * Get addon object
     * Can be cast to Addon from DonateCase if it's really loaded addon by DonateCase
     *
     * @return Addon object
     */
    @NotNull
    public Addon getAddon() {
        return addon;
    }

}
