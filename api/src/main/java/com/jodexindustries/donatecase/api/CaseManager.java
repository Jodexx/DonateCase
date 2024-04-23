package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.holograms.HologramManager;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Class for interaction with DonateCase managers
 */
public class CaseManager {
    private final AddonManager addonManager;
    private final AnimationManager animationManager;
    private final SubCommandManager subCommandManager;
    private final Addon addon;
    public CaseManager(Addon addon) {
        this.addon = addon;
        addonManager = new AddonManager(addon);
        subCommandManager = new SubCommandManager(addon);
        this.animationManager = new AnimationManager(addon);
    }

    /**
     * Get plugin instance
     * @return DonateCase instance
     */
    public JavaPlugin getInstance() {
        return getInstance();
    }

    /**
     * Get addon manager for addons manipulate
     * @return AddonManager instance
     */
    public AddonManager getAddonManager() {
        return this.addonManager;
    }

    public AnimationManager getAnimationManager() {
        return this.animationManager;
    }

    /**
     * Get sub command manager
     * @return SubCommandManager instance
     */
    public SubCommandManager getSubCommandManager() {
        return this.subCommandManager;
    }

    public static HologramManager getHologramManager() {
        return getHologramManager();
    }

    public Addon getAddon() {
        return addon;
    }
}
