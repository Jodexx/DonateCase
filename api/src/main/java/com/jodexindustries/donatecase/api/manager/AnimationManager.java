package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Interface to manage animations within the DonateCase API.
 * Provides methods for creating, registering, managing custom animations, and handling active cases.
 *
 * @param <A> The type of JavaAnimation associated with the manager.
 * @param <M> The type of CaseDataMaterial used in the animations.
 * @param <L> The type representing the location in the game world.
 * @param <B> The type representing a block in the game world.
 * @param <C> The type representing case data for animations.
 * @param <P> The type of Player interacting with animations.
 */
public interface AnimationManager<A, M, P, L, B, C> {

    /**
     * Creates a builder for defining and constructing a new custom animation.
     *
     * @param name The name of the animation to be created.
     * @return A {@link CaseAnimation.Builder} instance for building the animation.
     */
    @NotNull
    CaseAnimation.Builder<A> builder(@NotNull String name);

    /**
     * Registers a custom animation to the system.
     *
     * @param animation The {@link CaseAnimation} object to register.
     * @return True if the registration was successful, false otherwise.
     * @see #builder(String)
     */
    boolean registerAnimation(CaseAnimation<A> animation);

    /**
     * Unregisters a specific animation from the system by its name.
     *
     * @param name The name of the animation to unregister.
     */
    void unregisterAnimation(@NotNull String name);

    default void unregisterAnimations(Addon addon) {
        List<CaseAnimation<A>> list = new ArrayList<>(getRegisteredAnimations(addon));
        list.stream().map(CaseAnimation::getName).forEach(this::unregisterAnimation);
    }

    /**
     * Unregisters all animations currently registered in the system.
     */
    void unregisterAnimations();

    /**
     * Starts an animation at a specified location.
     *
     * @param player   The player who triggered the animation.
     * @param location The location where the animation should start.
     * @param caseData The case data associated with the animation.
     * @return A {@link CompletableFuture} that completes when the animation starts.
     */
    CompletableFuture<UUID> startAnimation(@NotNull P player, @NotNull L location, @NotNull C caseData);

    /**
     * Starts an animation at a specified location after a delay.
     *
     * @param player   The player who triggered the animation.
     * @param location The location where the animation should start.
     * @param caseData The case data associated with the animation.
     * @param delay    The delay in ticks before starting the animation.
     * @return A {@link CompletableFuture} that completes when the animation starts.
     */
    CompletableFuture<UUID> startAnimation(@NotNull P player, @NotNull L location, @NotNull C caseData, int delay);

    /**
     * Prepares for the end of an animation by granting rewards, sending messages, or performing other actions.
     * @param uuid The unique ID of the active case.
     */
    void animationPreEnd(UUID uuid);

    /**
     * Prepares for the end of an animation by granting rewards, sending messages, or performing other actions.
     *
     * @param caseData The case data associated with the animation.
     * @param player   The player interacting with the animation (can be offline).
     * @param uuid     The unique ID of the active case.
     * @param item     The item data associated with the animation's result.
     */
    @Deprecated
    void animationPreEnd(C caseData, P player, UUID uuid, CaseDataItem<M> item);

    /**
     * Prepares for the end of an animation by granting rewards, sending messages, or performing other actions.
     *
     * @param caseData The case data associated with the animation.
     * @param player   The player interacting with the animation (can be offline).
     * @param location The location of the active case block.
     * @param item     The item data associated with the animation's result.
     */
    void animationPreEnd(C caseData, P player, L location, CaseDataItem<M> item);

    /**
     * Completes the animation process and performs cleanup tasks.
     *
     * @param uuid The unique ID of the active case.
     */
    void animationEnd(UUID uuid);

    /**
     * Completes the animation process and performs cleanup tasks.
     *
     * @param caseData The case data associated with the animation.
     * @param player   The player interacting with the animation (can be offline).
     * @param uuid     The unique ID of the active case.
     * @param item     The item data associated with the animation's result.
     */
    @Deprecated
    void animationEnd(C caseData, P player, UUID uuid, CaseDataItem<M> item);

    /**
     * Checks whether an animation with the specified name is registered.
     *
     * @param name The name of the animation.
     * @return True if the animation is registered, false otherwise.
     */
    boolean isRegistered(String name);

    /**
     * Retrieves a registered animation by its name.
     *
     * @param animation The name of the animation to retrieve.
     * @return The {@link CaseAnimation} instance if registered, or null if not found.
     */
    @Nullable
    CaseAnimation<A> getRegisteredAnimation(String animation);

    /**
     * Retrieves all registered animations by addon.
     * @param addon The addon instance
     * @return List of animations
     * @since 2.0.2.3
     */
    default List<CaseAnimation<A>> getRegisteredAnimations(Addon addon) {
        return getRegisteredAnimations().values().stream().filter(animation ->
                animation.getAddon().equals(addon)).collect(Collectors.toList());
    }

    /**
     * Retrieves a map of all registered animations.
     *
     * @return A map where keys are animation names and values are {@link CaseAnimation} instances.
     */
    Map<String, CaseAnimation<A>> getRegisteredAnimations();

    /**
     * Retrieves a map of all active cases currently running in the system.
     *
     * @return A map where keys are UUIDs and values are {@link ActiveCase} instances associated with blocks.
     */
    Map<UUID, ActiveCase<B, P, CaseDataItem<M>>> getActiveCases();

    /**
     * Retrieves a map of active cases by their associated blocks.
     *
     * @return A map where keys are blocks and values are UUIDs of the active cases.
     */
    Map<B, UUID> getActiveCasesByBlock();

    /**
     * Gets active case by block
     * @since 2.0.2.5
     * @param block Block to check
     * @return active case by block
     */
    default ActiveCase<B, P, CaseDataItem<M>> getActiveCaseByBlock(B block) {
        UUID uuid = getActiveCasesByBlock().get(block);
        if(uuid == null) return null;

        return getActiveCases().get(uuid);
    }

    /**
     * Check if block locked
     * @since 2.0.2.5
     * @param block Block to check
     * @return true if block is locked by DonateCase
     */
    default boolean isLocked(B block) {
        ActiveCase<B, P, CaseDataItem<M>> activeCase = getActiveCaseByBlock(block);
        return activeCase != null && activeCase.isLocked();
    }
}
