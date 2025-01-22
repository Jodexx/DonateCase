package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
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
 */
public interface AnimationManager {

    /**
     * Registers a custom animation to the system.
     *
     * @param animation The {@link CaseAnimation} object to register.
     * @return True if the registration was successful, false otherwise.
     */
    boolean register(CaseAnimation animation);

    /**
     * Unregisters a specific animation from the system by its name.
     *
     * @param name The name of the animation to unregister.
     */
    void unregister(@NotNull String name);

    default void unregister(Addon addon) {
        List<CaseAnimation> list = new ArrayList<>(get(addon));
        list.stream().map(CaseAnimation::getName).forEach(this::unregister);
    }

    /**
     * Unregisters all animations currently registered in the system.
     */
    void unregister();

    /**
     * Starts an animation at a specified location.
     *
     * @param player   The player who triggered the animation.
     * @param location The location where the animation should start.
     * @param caseData The case data associated with the animation.
     * @return A {@link CompletableFuture} that completes when the animation starts.
     */
    CompletableFuture<UUID> start(@NotNull DCPlayer player, @NotNull CaseLocation location, @NotNull CaseData caseData);

    /**
     * Starts an animation at a specified location after a delay.
     *
     * @param player   The player who triggered the animation.
     * @param location The location where the animation should start.
     * @param caseData The case data associated with the animation.
     * @param delay    The delay in ticks before starting the animation.
     * @return A {@link CompletableFuture} that completes when the animation starts.
     */
    CompletableFuture<UUID> start(@NotNull DCPlayer player, @NotNull CaseLocation location, @NotNull CaseData caseData, int delay);

    /**
     * Prepares for the end of an animation by granting rewards, sending messages, or performing other actions.
     * @param uuid The unique ID of the active case.
     */
    void preEnd(UUID uuid);

    /**
     * Prepares for the end of an animation by granting rewards, sending messages, or performing other actions.
     *
     * @param caseData The case data associated with the animation.
     * @param player   The player interacting with the animation (can be offline).
     * @param location The location of the active case block.
     * @param item     The item data associated with the animation's result.
     */
    void preEnd(CaseData caseData, DCPlayer player, CaseLocation location, CaseDataItem item);

    /**
     * Completes the animation process and performs cleanup tasks.
     *
     * @param uuid The unique ID of the active case.
     */
    void end(UUID uuid);

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
    CaseAnimation get(String animation);

    /**
     * Retrieves all registered animations by addon.
     * @param addon The addon instance
     * @return List of animations
     */
    default List<CaseAnimation> get(Addon addon) {
        return getMap().values().stream().filter(animation ->
                animation.getAddon().equals(addon)).collect(Collectors.toList());
    }

    /**
     * Retrieves a map of all registered animations.
     *
     * @return A map where keys are animation names and values are {@link CaseAnimation} instances.
     */
    Map<String, CaseAnimation> getMap();

    /**
     * Retrieves a map of all active cases currently running in the system.
     *
     * @return A map where keys are UUIDs and values are {@link ActiveCase} instances associated with blocks.
     */
    Map<UUID, ActiveCase> getActiveCases();

    /**
     * Retrieves a map of active cases by their associated blocks.
     *
     * @return A map where keys are blocks and values are UUIDs of the active cases.
     */
    Map<Object, List<UUID>> getActiveCasesByBlock();

    /**
     * Gets active case by block
     * @param block Block to check
     * @return active case by block
     */
    default List<ActiveCase> getActiveCasesByBlock(Object block) {
        List<ActiveCase> activeCases = new ArrayList<>();

        List<UUID> uuids = getActiveCasesByBlock().get(block);
        if(uuids == null) return activeCases;

        activeCases = uuids.stream().map(uuid -> getActiveCases().get(uuid)).collect(Collectors.toList());

        return activeCases;
    }

    /**
     * Check if block locked
     * @param block Block to check
     * @return true if block is locked by DonateCase
     */
    default boolean isLocked(Object block) {
        return getActiveCasesByBlock(block).stream().anyMatch(ActiveCase::isLocked);
    }
}
