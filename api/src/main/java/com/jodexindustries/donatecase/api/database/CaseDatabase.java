package com.jodexindustries.donatecase.api.database;

import com.jodexindustries.donatecase.api.caching.SimpleCache;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.database.DatabaseType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class CaseDatabase {

    public final static SimpleCache<String, List<CaseData.History>> cache = new SimpleCache<>(20);

    public abstract void connect();

    /**
     * Connect via SQLITE
     *
     * @param path Database absolute path
     */
    public abstract void connect(String path);

    /**
     * Connect via MySQL
     *
     * @param database Database name
     * @param port     MySQL port
     * @param host     MySQL host
     * @param user     Database user
     * @param password User password
     */
    public abstract void connect(String database, int port, String host, String user, String password);

    public abstract CompletableFuture<Map<String, Integer>> getKeys(String player);

    public abstract CompletableFuture<Integer> getKeys(String name, String player);

    public abstract CompletableFuture<DatabaseStatus> setKeys(String name, String player, int keys);

    public abstract CompletableFuture<DatabaseStatus> delAllKeys();

    public abstract CompletableFuture<DatabaseStatus> delKeys(String caseType);

    /**
     * Get count of opened cases by player
     *
     * @param player   Player, who opened
     * @param caseType Case type
     * @return number of opened cases
     */
    public abstract CompletableFuture<Integer> getOpenCount(String player, String caseType);

    public abstract CompletableFuture<Map<String, Integer>> getOpenCount(String player);

    /**
     * Set count of opened cases by player
     *
     * @param caseType Case type
     * @param player   Player, who opened
     * @param count    Number of opened cases
     */
    public abstract CompletableFuture<DatabaseStatus> setCount(String caseType, String player, int count);

    public abstract CompletableFuture<DatabaseStatus> addHistory(String caseType, CaseData.History newEntry, int maxSize);

    /**
     * Sets history data for specific case type and index
     * @param caseType Case type
     * @param index Data index, if less than 0 - selects all data
     * @param data History data, if null - deletes
     * @return Future of DatabaseStatus
     */
    public abstract CompletableFuture<DatabaseStatus> setHistoryData(String caseType, int index, CaseData.History data);

    public abstract CompletableFuture<DatabaseStatus> removeHistoryData(String caseType);

    public abstract CompletableFuture<DatabaseStatus> removeHistoryData(String caseType, int index);

    public abstract CompletableFuture<List<CaseData.History>> getHistoryData();

    public abstract CompletableFuture<List<CaseData.History>> getHistoryData(String caseType);

    /**
     * Returns no-cached, if mysql disabled
     * @return list of all history data
     */
    public abstract List<CaseData.History> getCache();

    public abstract List<CaseData.History> getCache(String caseType);

    public abstract void close();

    public abstract DatabaseType getType();
}
