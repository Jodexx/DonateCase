package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.holograms.HologramManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import static com.jodexindustries.donatecase.DonateCase.*;


/**
 * The main class for API interaction with DonateCase, this is where most of the functions are located.
 */
public class CaseManager {
    private final AddonManager addonManager;
    private final AnimationManager animationManager;
    private final SubCommandManager subCommandManager;
    private final Plugin addon;
    public CaseManager(Plugin addon) {
        this.addon = addon;
        addonManager = new AddonManager();
        subCommandManager = new SubCommandManager(addon);
        this.animationManager = new AnimationManager(addon);
    }

    /**
     * Get plugin instance
     * @return DonateCase instance
     */
    public JavaPlugin getInstance() {
        return instance;
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
        return hologramManager;
    }

    public Plugin getAddon() {
        return addon;
    }
}
