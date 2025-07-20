package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.caching.SimpleCache;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * This abstract class provides methods for managing the number of opened cases by players.
 * It supports synchronous and asynchronous access to stored values,
 * caching, and persistence through database operations.
 */
public abstract class CaseOpenManager {

    /**
     * A simple in-memory cache that stores case open counts per player.
     * Key: player name, Value: map of caseType -> open count
     */
    public final static SimpleCache<String, Map<String, Integer>> cache = new SimpleCache<>(20);

    /**
     * Gets the number of times a specific case type has been opened by a player (synchronously).
     *
     * @param caseType the case type
     * @param player   the player's name
     * @return the number of opened cases
     */
    @ApiStatus.Experimental
    public abstract int get(String caseType, String player);

    /**
     * Gets the number of opened cases grouped by case type for a specific player (synchronously).
     *
     * @param player the player's name
     * @return a map of caseType -> open count
     */
    @ApiStatus.Experimental
    public abstract Map<String, Integer> get(String player);

    /**
     * Gets the number of times a specific case type has been opened by a player (asynchronously).
     *
     * @param caseType the case type
     * @param player   the player's name
     * @return a {@link CompletableFuture} completing with the open count
     */
    public abstract CompletableFuture<Integer> getAsync(String caseType, String player);

    /**
     * Gets all opened case counts grouped by case type for a specific player (asynchronously).
     *
     * @param player the player's name
     * @return a {@link CompletableFuture} completing with a map of caseType -> open count
     */
    public abstract CompletableFuture<Map<String, Integer>> getAsync(String player);

    /**
     * Gets all opened case counts for all players (asynchronously).
     *
     * @return a {@link CompletableFuture} completing with a map where:
     *         - key is the player name,
     *         - value is a map of caseType -> open count
     */
    public abstract CompletableFuture<Map<String, Map<String, Integer>>> getGlobalAsync();

    /**
     * Gets the number of times a specific case type has been opened by all players (asynchronously).
     *
     * @param caseType the case type
     * @return a {@link CompletableFuture} completing with a map where:
     *         - key is the player name,
     *         - value is the number of times that case type has been opened
     */
    public abstract CompletableFuture<Map<String, Integer>> getGlobalAsync(String caseType);

    /**
     * Gets the cached number of times a specific case type has been opened by a player.
     * If MySQL is disabled, no cache is used.
     *
     * @param caseType the case type
     * @param player   the player's name
     * @return the cached open count
     */
    public abstract int getCache(String caseType, String player);

    /**
     * Gets all cached opened case counts grouped by case type for a specific player.
     * If MySQL is disabled, no cache is used.
     *
     * @param player the player's name
     * @return a map of caseType -> open count
     */
    public abstract Map<String, Integer> getCache(String player);

    /**
     * Sets the number of opened cases for a specific player and case type (asynchronously).
     *
     * @param caseType  the case type
     * @param player    the player's name
     * @param openCount the number of opened cases to set
     * @return a {@link CompletableFuture} completing with the result of the database operation
     */
    public abstract CompletableFuture<DatabaseStatus> set(String caseType, String player, int openCount);

    /**
     * Increments the number of opened cases for a specific player and case type (asynchronously).
     *
     * @param caseType  the case type
     * @param player    the player's name
     * @param openCount the number of opened cases to add
     * @return a {@link CompletableFuture} completing with the result of the database operation
     */
    public abstract CompletableFuture<DatabaseStatus> add(String caseType, String player, int openCount);

}
