package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.config.Loadable;
import com.jodexindustries.donatecase.api.database.CaseDatabase;
import com.jodexindustries.donatecase.api.event.EventBus;
import com.jodexindustries.donatecase.api.manager.*;
import com.jodexindustries.donatecase.api.platform.Platform;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for managing various components of the DonateCase API (DCAPI) system.
 * Provides centralized access to all key managers, utilities, and configurations required for case operations.
 *
 */
public abstract class DCAPI {

    @Getter
    protected static DCAPI instance;

    /**
     * Retrieves the {@link ActionManager} responsible for handling custom actions in the system.
     *
     * @return The action manager instance.
     */
    public abstract @NotNull ActionManager getActionManager();

    /**
     * Retrieves the {@link AddonManager} responsible for managing add-ons within the system.
     *
     * @return The add-on manager instance.
     */
    public abstract @NotNull AddonManager getAddonManager();

    /**
     * Retrieves the {@link AnimationManager} for managing animations associated with cases.
     *
     * @return The animation manager instance.
     */
    public abstract @NotNull AnimationManager getAnimationManager();

    /**
     * Retrieves the {@link CaseKeyManager}, which manages case keys and related functionality.
     *
     * @return The case key manager instance.
     */
    public abstract @NotNull CaseKeyManager getCaseKeyManager();

    /**
     * Retrieves the {@link CaseManager}, responsible for handling case definitions and data.
     *
     * @return The case manager instance.
     */
    public abstract @NotNull CaseManager getCaseManager();

    /**
     * Retrieves the {@link CaseOpenManager}, which handles case-opening logic and functionality.
     *
     * @return The case open manager instance.
     */
    public abstract @NotNull CaseOpenManager getCaseOpenManager();

    /**
     * Retrieves the {@link GUIManager}, which handles GUI creation and interactions.
     *
     * @return The GUI manager instance.
     */
    public abstract @NotNull GUIManager getGUIManager();

    /**
     * Retrieves the {@link GUITypedItemManager}, responsible for managing items displayed in GUIs.
     *
     * @return The GUI-typed item manager instance.
     */
    public abstract @NotNull GUITypedItemManager getGuiTypedItemManager();

    /**
     * Retrieves the {@link MaterialManager}, responsible for managing case materials.
     *
     * @return The material manager instance.
     */
    public abstract @NotNull MaterialManager getMaterialManager();

    /**
     * Retrieves the {@link SubCommandManager}, which handles sub-commands for command processing.
     *
     * @return The sub-command manager instance.
     */
    public abstract @NotNull SubCommandManager getSubCommandManager();

    public abstract @NotNull HologramManager getHologramManager();

    /**
     * Retrieves the {@link CaseDatabase}, which provides access to the database for storing case-related data.
     *
     * @return The case database instance.
     */
    public abstract @NotNull CaseDatabase getDatabase();

    /**
     * Retrieves the configuration system used by the DCAPI.
     *
     * @return The configuration system instance.
     */
    public abstract @NotNull ConfigManager getConfigManager();

    public abstract @NotNull Loadable getCaseLoader();

    public abstract @NotNull EventBus getEventBus();

    /**
     * Should return the DonateCase platform instance
     *
     * @return The DonateCase platform instance.
     */
    public abstract @NotNull Platform getPlatform();

    /**
     * Returns the total number of case openings for all cases and all players.
     */
    public int getGlobalOpenCount() {
        return getCaseOpenManager().getGlobalOpenCount();
    }

    /**
     * Returns the total number of openings for a specific case type for all players.
     */
    public int getGlobalOpenCount(String caseType) {
        return getCaseOpenManager().getGlobalOpenCount(caseType);
    }

    @ApiStatus.Experimental
    public abstract void clear();
}
