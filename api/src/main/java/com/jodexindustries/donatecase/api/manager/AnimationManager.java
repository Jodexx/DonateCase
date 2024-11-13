package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.animation.JavaAnimation;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface to manage animations within the donate case API.
 * Provides methods for creating, registering, and managing custom animations, as well as
 * checking and retrieving registered animations.
 *
 * @param <A> the type of JavaAnimation
 * @param <M> the type of CaseDataMaterial
 */
public interface AnimationManager<A extends JavaAnimation<M>, M extends CaseDataMaterial> {

    /**
     * Provides a builder for creating a new animation with the specified name.
     *
     * @param name the name of the animation to create
     * @return a builder instance for constructing the CaseAnimation
     */
    @NotNull
    CaseAnimation.Builder<A, M> builder(String name);

    /**
     * Registers a custom animation to the system.
     *
     * @param animation the CaseAnimation object to register
     * @return true if registration was successful, false otherwise
     * @see #builder(String)
     */
    boolean registerAnimation(CaseAnimation<A, M> animation);

    /**
     * Unregisters a custom animation by name, removing it from the system.
     *
     * @param name the name of the animation to unregister
     */
    void unregisterAnimation(String name);

    /**
     * Unregisters all animations from the system.
     */
    void unregisterAnimations();


    /**
     * Checks if an animation with the specified name is registered.
     *
     * @param name the name of the animation
     * @return true if the animation is registered, false otherwise
     */
    boolean isRegistered(String name);

    /**
     * Retrieves a registered animation by its name.
     *
     * @param animation the name of the animation to retrieve
     * @return the CaseAnimation instance if registered, or null if not found
     */
    @Nullable
    CaseAnimation<A, M> getRegisteredAnimation(String animation);
}
