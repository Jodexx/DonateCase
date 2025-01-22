package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.caching.SimpleCache;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for managing case keys for players in a Donate Case system. Provides methods
 * to set, modify, add, and remove keys, with both synchronous and asynchronous options.
 * Supports direct interaction with persistent storage and caching mechanisms.
 */
public interface CaseKeyManager {

    /**
     * Directly set case keys to a specific player (bypassing addition/subtraction)
     *
     * @param caseType Case type
     * @param player   Player name
     * @param keys     Number of keys
     * @return CompletableFuture of completion status
     */
    CompletableFuture<DatabaseStatus> set(String caseType, String player, int keys);

    /**
     * Modify case keys for a specific player
     *
     * @param caseType Case type
     * @param player   Player name
     * @param keys     Number of keys to modify (positive to add, negative to remove)
     * @return Completable future of completion status
     */
    CompletableFuture<DatabaseStatus> modify(String caseType, String player, int keys);

    /**
     * Add case keys to a specific player (async)
     *
     * @param caseType Case type
     * @param player   Player name
     * @param keys     Number of keys
     * @return Completable future of completes
     * @see #modify(String, String, int)
     */
    default CompletableFuture<DatabaseStatus> add(String caseType, String player, int keys) {
        return modify(caseType, player, keys);
    }

    /**
     * Delete case keys for a specific player (async)
     *
     * @param caseType Case name
     * @param player   Player name
     * @param keys     Number of keys
     * @return Completable future of completes
     * @see #modify(String, String, int)
     */
    default CompletableFuture<DatabaseStatus> remove(String caseType, String player, int keys) {
        return modify(caseType, player, -keys);
    }

    CompletableFuture<DatabaseStatus> delete();

    /**
     * Get the keys to a certain player's case
     * @param caseType Case type
     * @param player Player name
     * @return Number of keys
     */
    default int get(String caseType, String player) {
        return getAsync(caseType, player).join();
    }

    /**
     * Get the map of all player's keys
     * @param player Player name
     * @return Map of the keys. Key - Case type, Value - Number of keys
     */
    default Map<String, Integer> get(String player) {
        return getAsync(player).join();
    }

    /**
     * Get the keys to a certain player's case
     * @param caseType Case type
     * @param player Player name
     * @return CompletableFuture of keys
     */
    CompletableFuture<Integer> getAsync(String caseType, String player);

    /**
     * Get the map of all player's keys
     * @param player Player name
     * @return Map of the keys. Key - Case type, Value - Number of keys
     */
    CompletableFuture<Map<String, Integer>> getAsync(String player);

    /**
     * Get the keys to a certain player's case from cache <br/>
     * Returns no-cached, if mysql disabled
     * @param caseType Case type
     * @param player Player name
     * @return Number of keys
     */
    int getCache(String caseType, String player);

    Map<String, Integer> getCache(String player);

    SimpleCache<String, Map<String, Integer>> getCache();
}
