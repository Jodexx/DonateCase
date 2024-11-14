package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.data.animation.JavaAnimation;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import com.jodexindustries.donatecase.api.database.CaseDatabase;
import com.jodexindustries.donatecase.api.manager.*;

/**
 * Interface for managing various components of the Donate Case API (DCAPI) system.
 * Provides access to different managers responsible for handling actions, add-ons,
 * animations, case keys, case openings, GUI typed items, materials, and sub-commands.
 *
 * @param <Player>   the type of player
 * @param <M>  the type of CaseDataMaterial representing materials associated with cases
 * @param <G>   the type of case gui
 * @param <E>   the type of case gui click event
 * @param <I>   the type of item stack
 * @param <S>   the type of command sender, used in the sub-command manager
 * @param <L>   the type of location
 * @param <C>   the type of CaseData
 */
public interface DCAPI<Player, A extends JavaAnimation<M, I>, M extends CaseDataMaterial<I>, G, E, I, S, L, C>  {

    /**
     * Gets the ActionManager responsible for handling actions within the system.
     *
     * @return the action manager instance
     */
    ActionManager<Player> getActionManager();

    /**
     * Gets the AddonManager responsible for managing add-ons in the system.
     *
     * @return the add-on manager instance
     */
    AddonManager getAddonManager();

    /**
     * Gets the AnimationManager responsible for handling animations using JavaAnimation instances.
     *
     * @return the animation manager instance
     */
    AnimationManager<A, M, I, Player, L, C> getAnimationManager();

    /**
     * Gets the CaseKeyManager responsible for managing keys associated with cases.
     *
     * @return the case key manager instance
     */
    CaseKeyManager getCaseKeyManager();

    /**
     * Gets the CaseOpenManager responsible for handling case-opening functionality.
     *
     * @return the case open manager instance
     */
    CaseOpenManager getCaseOpenManager();

    /**
     * Gets the GUITypedItemManager responsible for managing GUI-typed items.
     *
     * @return the GUI-typed item manager instance
     */
    GUITypedItemManager<M, G, E> getGuiTypedItemManager();

    /**
     * Gets the MaterialManager responsible for handling materials associated with cases.
     *
     * @return the material manager instance
     */
    MaterialManager<I> getMaterialManager();

    /**
     * Gets the SubCommandManager responsible for handling sub-commands for a specific sender type.
     *
     * @return the sub-command manager instance
     */
    SubCommandManager<S> getSubCommandManager();

    CaseDatabase getDatabase();
    }
