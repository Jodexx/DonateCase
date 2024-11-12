package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;

import java.util.concurrent.CompletableFuture;

/**
 * This interface provides methods for managing the count of opened cases for players.
 * It includes functionality for getting and setting the number of opened cases both asynchronously
 * and synchronously, with support for caching and database operations.
 * It is used to track case openings, modify the count, and interact with persistent storage.
 */
public interface CaseOpenManager {

    /**
     * Get count of opened cases by player
     * @param caseType Case type
     * @param player Player, who opened
     * @return opened count
     */
    int getOpenCount(String caseType, String player);

    /**
     * Get count of opened cases by player
     * @param caseType Case type
     * @param player Player, who opened
     * @return CompletableFuture of open count
     */
    CompletableFuture<Integer> getOpenCountAsync(String caseType, String player);

    /**
     * Get count of opened cases by player from cache <br/>
     * Returns no-cached, if mysql disabled
     * @param caseType Case type
     * @param player Player, who opened
     * @return opened count
     */
    int getOpenCountCache(String caseType, String player);

    /**
     * Set case keys to a specific player (async)
     *
     * @param caseType  Case type
     * @param player    Player name
     * @param openCount Opened count
     * @return Completable future of completes
     */
    CompletableFuture<DatabaseStatus> setOpenCount(String caseType, String player, int openCount);

    /**
     * Add count of opened cases by player (async)
     *
     * @param caseType  Case type
     * @param player    Player name
     * @param openCount Opened count
     * @return Completable future of completes
     */
    CompletableFuture<DatabaseStatus> addOpenCount(String caseType, String player, int openCount);
}
