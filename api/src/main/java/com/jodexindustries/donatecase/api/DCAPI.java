package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.data.animation.JavaAnimation;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import com.jodexindustries.donatecase.api.database.CaseDatabase;
import com.jodexindustries.donatecase.api.manager.*;
import com.jodexindustries.donatecase.api.tools.DCTools;

/**
 * Interface for managing various components of the DonateCase API (DCAPI) system.
 * Provides centralized access to all key managers, utilities, and configurations required for case operations.
 *
 * @param <P>        The type representing the player interacting with the system.
 * @param <A>        The type of {@link JavaAnimation} used for animations.
 * @param <M>        The type of {@link CaseDataMaterial} representing materials associated with cases.
 * @param <G>        The type representing a GUI component for cases.
 * @param <E>        The type representing GUI click events.
 * @param <I>        The type of ItemStack.
 * @param <S>        The type representing the command sender, used in the sub-command manager.
 * @param <L>        The type representing a location in the game world.
 * @param <B>        The type representing a block in the game world.
 * @param <C>        The type of case data structure.
 * @param <IY>       The type of inventory representation.
 * @param <CG>       The type of configuration system, extending {@link Config}.
 * @param <T>        The type of tools and utilities, extending {@link DCTools}.
 */
public interface DCAPI<P, A extends JavaAnimation<M, I>, M extends CaseDataMaterial<I>, G, E, I, S, L, B, C, IY, CG extends Config, T extends DCTools> {

    /**
     * Retrieves the {@link ActionManager} responsible for handling custom actions in the system.
     *
     * @return The action manager instance.
     */
    ActionManager<P> getActionManager();

    /**
     * Retrieves the {@link AddonManager} responsible for managing add-ons within the system.
     *
     * @return The add-on manager instance.
     */
    AddonManager getAddonManager();

    /**
     * Retrieves the {@link AnimationManager} for managing animations associated with cases.
     *
     * @return The animation manager instance.
     */
    AnimationManager<A, M, I, P, L, B, C> getAnimationManager();

    /**
     * Retrieves the {@link CaseKeyManager}, which manages case keys and related functionality.
     *
     * @return The case key manager instance.
     */
    CaseKeyManager getCaseKeyManager();

    /**
     * Retrieves the {@link CaseManager}, responsible for handling case definitions and data.
     *
     * @return The case manager instance.
     */
    CaseManager<C> getCaseManager();

    /**
     * Retrieves the {@link CaseOpenManager}, which handles case-opening logic and functionality.
     *
     * @return The case open manager instance.
     */
    CaseOpenManager getCaseOpenManager();

    /**
     * Retrieves the {@link GUIManager}, which handles GUI creation and interactions.
     *
     * @return The GUI manager instance.
     */
    GUIManager<IY, L, P, C, M> getGUIManager();

    /**
     * Retrieves the {@link GUITypedItemManager}, responsible for managing items displayed in GUIs.
     *
     * @return The GUI-typed item manager instance.
     */
    GUITypedItemManager<M, G, E> getGuiTypedItemManager();

    /**
     * Retrieves the {@link MaterialManager}, responsible for managing case materials.
     *
     * @return The material manager instance.
     */
    MaterialManager<I> getMaterialManager();

    /**
     * Retrieves the {@link SubCommandManager}, which handles sub-commands for command processing.
     *
     * @return The sub-command manager instance.
     */
    SubCommandManager<S> getSubCommandManager();

    /**
     * Retrieves the {@link CaseDatabase}, which provides access to the database for storing case-related data.
     *
     * @return The case database instance.
     */
    CaseDatabase getDatabase();

    /**
     * Retrieves the configuration system used by the DCAPI.
     *
     * @return The configuration system instance.
     */
    CG getConfig();

    /**
     * Retrieves the tools and utilities provided by the DCAPI.
     *
     * @return The tools instance.
     */
    T getTools();
}
