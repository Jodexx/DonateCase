package com.jodexindustries.donatecase.api.database;

import com.jodexindustries.donatecase.api.caching.SimpleCache;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.database.DatabaseType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * This abstract class defines the contract for interacting with the case database.
 * It provides methods to manage keys, case open counts, and history records,
 * supporting both MySQL and SQLite backends.
 */
public abstract class CaseDatabase {
    /**
     * A simple in-memory cache for storing recent case open history.
     * Key: case type, Value: list of {@link CaseData.History} entries
     */
    public final static SimpleCache<String, List<CaseData.History>> cache = new SimpleCache<>(20);

    /**
     * Connects to the database using the default settings.
     */
    public abstract void connect();

    /**
     * Connects to a SQLite database.
     *
     * @param path absolute path to the SQLite database file
     */
    public abstract void connect(String path);

    /**
     * Connects to a MySQL database.
     *
     * @param database database name
     * @param port     port number
     * @param host     host address
     * @param user     username
     * @param password password
     */
    public abstract void connect(String database, int port, String host, String user, String password);

    /**
     * Retrieves the number of keys the player has for all case types.
     *
     * @param player the player's name
     * @return a {@link CompletableFuture} completing with a map of caseType -> keys
     */
    public abstract CompletableFuture<Map<String, Integer>> getKeys(String player);

    /**
     * Retrieves the number of keys a player has for a specific case type.
     *
     * @param name   case type
     * @param player the player's name
     * @return a {@link CompletableFuture} completing with the number of keys
     */
    public abstract CompletableFuture<Integer> getKeys(String name, String player);

    /**
     * Sets the number of keys a player has for a specific case type.
     *
     * @param name   case type
     * @param player the player's name
     * @param keys   number of keys to set
     * @return a {@link CompletableFuture} completing with the result status
     */
    public abstract CompletableFuture<DatabaseStatus> setKeys(String name, String player, int keys);

    /**
     * Deletes all key data for all players and case types.
     *
     * @return a {@link CompletableFuture} completing with the result status
     */
    public abstract CompletableFuture<DatabaseStatus> delAllKeys();

    /**
     * Deletes key data for a specific case type.
     *
     * @param caseType the case type to delete keys for
     * @return a {@link CompletableFuture} completing with the result status
     */
    public abstract CompletableFuture<DatabaseStatus> delKeys(String caseType);

    /**
     * Retrieves the number of times a player has opened a specific case type.
     *
     * @param player   the player's name
     * @param caseType the case type
     * @return a {@link CompletableFuture} completing with the open count
     */
    public abstract CompletableFuture<Integer> getOpenCount(String player, String caseType);

    /**
     * Retrieves all case open counts for a player, grouped by case type.
     *
     * @param player the player's name
     * @return a {@link CompletableFuture} completing with a map of caseType -> open count
     */
    public abstract CompletableFuture<Map<String, Integer>> getOpenCount(String player);

    /**
     * Retrieves global case open counts for all players and case types.
     *
     * @return a {@link CompletableFuture} completing with a map where:
     *         - key is the player name,
     *         - value is another map of caseType -> open count
     */
    public abstract CompletableFuture<Map<String, Map<String, Integer>>> getGlobalOpenCount();

    /**
     * Retrieves the open count of a specific case type across all players.
     *
     * @param caseType the case type
     * @return a {@link CompletableFuture} completing with a map of player -> open count
     */
    public abstract CompletableFuture<Map<String, Integer>> getGlobalOpenCount(String caseType);


    /**
     * Sets the number of times a player has opened a specific case type.
     *
     * @param caseType the case type
     * @param player   the player's name
     * @param count    the number of times opened
     * @return a {@link CompletableFuture} completing with the result status
     */
    public abstract CompletableFuture<DatabaseStatus> setCount(String caseType, String player, int count);

    /**
     * Adds a new entry to the case opening history for a specific case type.
     * Limits the total size to {@code maxSize}.
     *
     * @param caseType the case type
     * @param newEntry the new history entry
     * @param maxSize  maximum size of the history list
     * @return a {@link CompletableFuture} completing with the result status
     */
    public abstract CompletableFuture<DatabaseStatus> addHistory(String caseType, CaseData.History newEntry, int maxSize);

    /**
     * Sets a history entry at a specific index or the full list if index &lt; 0.
     *
     * @param caseType the case type
     * @param index    index of the history entry (use -1 to overwrite all)
     * @param data     the history entry to set; if {@code null}, the entry is removed
     * @return a {@link CompletableFuture} completing with the result status
     */
    public abstract CompletableFuture<DatabaseStatus> setHistoryData(String caseType, int index, CaseData.History data);

    /**
     * Removes all history entries for a given case type.
     *
     * @param caseType the case type
     * @return a {@link CompletableFuture} completing with the result status
     */
    public abstract CompletableFuture<DatabaseStatus> removeHistoryData(String caseType);

    /**
     * Removes a history entry at a specific index for a given case type.
     *
     * @param caseType the case type
     * @param index    index of the entry to remove
     * @return a {@link CompletableFuture} completing with the result status
     */
    public abstract CompletableFuture<DatabaseStatus> removeHistoryData(String caseType, int index);

    /**
     * Retrieves all case opening history entries.
     *
     * @return a {@link CompletableFuture} completing with a list of all history entries
     */
    public abstract CompletableFuture<List<CaseData.History>> getHistoryData();

    /**
     * Retrieves case opening history entries for a specific case type.
     *
     * @param caseType the case type
     * @return a {@link CompletableFuture} completing with a list of history entries
     */
    public abstract CompletableFuture<List<CaseData.History>> getHistoryData(String caseType);

    /**
     * Retrieves all cached history data.
     * <p>Returns uncached data if MySQL is disabled.</p>
     *
     * @return a list of all cached history entries
     */
    public abstract List<CaseData.History> getCache();

    /**
     * Retrieves cached history data for a specific case type.
     * <p>Returns uncached data if MySQL is disabled.</p>
     *
     * @param caseType the case type
     * @return a list of cached history entries
     */
    public abstract List<CaseData.History> getCache(String caseType);

    /**
     * Closes the database connection and frees any allocated resources.
     */
    public abstract void close();

    /**
     * Returns the current database type (e.g., SQLITE or MYSQL).
     *
     * @return the {@link DatabaseType}
     */
    public abstract DatabaseType getType();
}
