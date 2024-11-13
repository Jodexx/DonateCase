package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.data.animation.JavaAnimation;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import com.jodexindustries.donatecase.api.manager.*;

/**
 * Interface for managing various components of the Donate Case API (DCAPI) system.
 * Provides access to different managers responsible for handling actions, add-ons,
 * animations, case keys, case openings, GUI typed items, materials, and sub-commands.
 *
 * @param <ACM> the type of ActionManager for handling actions within the system
 * @param <ADM> the type of AddonManager for managing add-ons in the system
 * @param <ANM> the type of AnimationManager for handling animations with specific JavaAnimation types
 * @param <CKM> the type of CaseKeyManager for managing keys associated with cases
 * @param <COM> the type of CaseOpenManager for tracking, updating, and caching the count of opened cases for players
 * @param <GIM> the type of GUITypedItemManager for managing GUI-typed items associated with cases
 * @param <SCM> the type of SubCommandManager for handling sub-commands
 * @param <MAM> the type of MaterialManager for managing materials associated with cases
 * @param <MA>  the type of CaseDataMaterial representing materials associated with cases
 * @param <JA>  the type of JavaAnimation, used in animations within the system
 * @param <S>   the type of command sender, used in the sub-command manager
 * @param <P>   the type of player, used in the action manager
 * @param <I>   the type of item stack, used in the material manager
 * @param <G>   the type of case gui, used in the gui typed item manager
 * @param <E>   the type of case gui click event, used in the gui typed item manager
 */
public interface DCAPI<ACM extends ActionManager<P>, ADM extends AddonManager,
        ANM extends AnimationManager<JA, MA>, CKM extends CaseKeyManager, COM extends CaseOpenManager,
        GIM extends GUITypedItemManager<MA, G, E>, SCM extends SubCommandManager<S>, MAM extends MaterialManager<I>,
        MA extends CaseDataMaterial, JA extends JavaAnimation<MA>, S, P, I, G, E> {

    /**
     * Gets the ActionManager responsible for handling actions within the system.
     *
     * @return the action manager instance
     */
    ACM getActionManager();

    /**
     * Gets the AddonManager responsible for managing add-ons in the system.
     *
     * @return the add-on manager instance
     */
    ADM getAddonManager();

    /**
     * Gets the AnimationManager responsible for handling animations using JavaAnimation instances.
     *
     * @return the animation manager instance
     */
    ANM getAnimationManager();

    /**
     * Gets the CaseKeyManager responsible for managing keys associated with cases.
     *
     * @return the case key manager instance
     */
    CKM getCaseKeyManager();

    /**
     * Gets the CaseOpenManager responsible for handling case-opening functionality.
     *
     * @return the case open manager instance
     */
    COM getCaseOpenManager();

    /**
     * Gets the GUITypedItemManager responsible for managing GUI-typed items.
     *
     * @return the GUI-typed item manager instance
     */
    GIM getGUITypedItemManager();

    /**
     * Gets the MaterialManager responsible for handling materials associated with cases.
     *
     * @return the material manager instance
     */
    MAM getMaterialManager();

    /**
     * Gets the SubCommandManager responsible for handling sub-commands for a specific sender type.
     *
     * @return the sub-command manager instance
     */
    SCM getSubCommandManager();
}
