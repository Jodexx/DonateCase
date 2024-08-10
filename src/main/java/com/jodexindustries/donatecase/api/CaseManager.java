package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.addon.external.ExternalJavaAddon;
import com.jodexindustries.donatecase.api.holograms.HologramManager;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Class for getting all DonateCase managers
 */
public class CaseManager {
    private final AddonManager addonManager;
    private final AnimationManager animationManager;
    private final SubCommandManager subCommandManager;
    private final ActionManager actionManager;
    private final MaterialManager materialManager;
    private final GUITypedItemManager guiTypedItemManager;
    private final Addon addon;

    /**
     * Constructor for initializing addon's CaseManager
     * @param addon Internal addon
     */
    public CaseManager(@NotNull Addon addon) {
        this.addon = addon;
        this.addonManager = new AddonManager(addon);
        this.subCommandManager = new SubCommandManager(addon);
        this.animationManager = new AnimationManager(addon);
        this.actionManager = new ActionManager(addon);
        this.materialManager = new MaterialManager(addon);
        this.guiTypedItemManager = new GUITypedItemManager(addon);
    }

    /**
     * Constructor for initializing addon's CaseManager
     * @param plugin External addon
     */
    public CaseManager(@NotNull Plugin plugin) {
        this.addon = new ExternalJavaAddon(plugin);
        this.addonManager = new AddonManager(addon);
        this.subCommandManager = new SubCommandManager(addon);
        this.animationManager = new AnimationManager(addon);
        this.actionManager = new ActionManager(addon);
        this.materialManager = new MaterialManager(addon);
        this.guiTypedItemManager = new GUITypedItemManager(addon);
    }

    /**
     * Get plugin instance
     * @return DonateCase instance
     */
    @NotNull
    public DonateCase getInstance() {
        return Case.getInstance();
    }

    /**
     * Get addon manager for addons manipulate
     * @return AddonManager instance
     */
    @NotNull
    public AddonManager getAddonManager() {
        return this.addonManager;
    }

    /**
     * Get animation manager for animations manipulate
     * @return AnimationManager instance
     */
    @NotNull
    public AnimationManager getAnimationManager() {
        return this.animationManager;
    }

    /**
     * Get sub command manager
     * @return SubCommandManager instance
     */
    @NotNull
    public SubCommandManager getSubCommandManager() {
        return this.subCommandManager;
    }

    /**
     * Get hologram manager
     * @return HologramManager instance
     */
    @Nullable
    public static HologramManager getHologramManager() {
        return Case.getInstance().hologramManager;
    }

    /**
     * Get case action manager
     * @return ActionManager instance
     */
    @NotNull
    public ActionManager getActionManager() {
        return actionManager;
    }

    /**
     * Get case material manager
     * @return MaterialManager instance
     * @since 2.2.4.8
     */
    @NotNull
    public MaterialManager getMaterialManager() {
        return materialManager;
    }

    /**
     * Get GUI typed item manager
     * @return GUITypedItemManager instance
     * @since 2.2.4.9
     */
    @NotNull
    public GUITypedItemManager getGuiTypedItemManager() {
        return guiTypedItemManager;
    }

    /**
     * Get addon object
     * Can be cast to Addon from DonateCase if it's really loaded addon by DonateCase
     * @return Addon object
     */
    @NotNull
    public Addon getAddon() {
        return addon;
    }
}
