package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.animation.JavaAnimation;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Interface to manage animations within the donate case API.
 * Provides methods for creating, registering, and managing custom animations, as well as
 * checking and retrieving registered animations.
 *
 * @param <A> the type of JavaAnimation
 * @param <M> the type of CaseDataMaterial
 */
public interface AnimationManager<A extends JavaAnimation<M, I>, M extends CaseDataMaterial<I>, I, Player, L, B, C> {

    /**
     * Provides a builder for creating a new animation with the specified name.
     *
     * @param name the name of the animation to create
     * @return a builder instance for constructing the CaseAnimation
     */
    @NotNull
    CaseAnimation.Builder<A, M, I> builder(String name);

    /**
     * Registers a custom animation to the system.
     *
     * @param animation the CaseAnimation object to register
     * @return true if registration was successful, false otherwise
     * @see #builder(String)
     */
    boolean registerAnimation(CaseAnimation<A, M, I> animation);

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
     * Start animation at a specific location
     *
     * @param player   The player who opened the case
     * @param location Location where to start the animation
     * @param caseData Case data
     * @return Completable future of completes (when started)
     */
    CompletableFuture<Boolean> startAnimation(@NotNull Player player, @NotNull L location, @NotNull C caseData);

    /**
     * Start animation at a specific location with delay
     *
     * @param player   The player who opened the case
     * @param location Location where to start the animation
     * @param caseData Case data
     * @param delay Delay in ticks
     * @return Completable future of completes (when started)
     */
    CompletableFuture<Boolean> startAnimation(@NotNull Player player, @NotNull L location, @NotNull C caseData, int delay);

    /**
     * Animation pre end method for custom animations is called to grant a group, send a message, and more
     * @param caseData Case data
     * @param player Player who opened (offline player)
     * @param uuid Active case uuid
     * @param item Item data
     */
    void animationPreEnd(C caseData, Player player, UUID uuid, CaseDataItem<M, I> item);

    /**
     * Animation pre end method for custom animations is called to grant a group, send a message, and more
     * @param caseData Case data
     * @param player Player who opened (offline player)
     * @param location Active case block location
     * @param item Item data
     */
     void animationPreEnd(C caseData, Player player, L location, CaseDataItem<M, I> item);

    /**
     * Animation end method for custom animations is called to completely end the animation
     * @param item Item data
     * @param caseData Case data
     * @param player Player who opened (offline player)
     * @param uuid Active case uuid
     */
    void animationEnd(C caseData, Player player, UUID uuid, CaseDataItem<M, I> item);

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
    CaseAnimation<A, M, I> getRegisteredAnimation(String animation);

    Map<String, CaseAnimation<A, M, I>> getRegisteredAnimations();

    Map<UUID, ActiveCase<B>> getActiveCases();

    Map<B, UUID> getActiveCasesByBlock();
}
