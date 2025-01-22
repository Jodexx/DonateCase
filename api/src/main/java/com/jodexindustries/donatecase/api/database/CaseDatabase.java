package com.jodexindustries.donatecase.api.database;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.database.DatabaseType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface CaseDatabase {

    void connect();

    /**
     * Connect via SQLITE
     *
     * @param path Database absolute path
     */
    void connect(String path);

    /**
     * Connect via MySQL
     *
     * @param database Database name
     * @param port     MySQL port
     * @param host     MySQL host
     * @param user     Database user
     * @param password User password
     */
    void connect(String database, String port, String host, String user, String password);

    CompletableFuture<Map<String, Integer>> getKeys(String player);

    CompletableFuture<Integer> getKeys(String name, String player);

    CompletableFuture<DatabaseStatus> setKeys(String name, String player, int keys);

    CompletableFuture<DatabaseStatus> delAllKeys();

    /**
     * Get count of opened cases by player
     *
     * @param player   Player, who opened
     * @param caseType Case type
     * @return number of opened cases
     */
    CompletableFuture<Integer> getOpenCount(String player, String caseType);

    CompletableFuture<Map<String, Integer>> getOpenCount(String player);

    /**
     * Set count of opened cases by player
     *
     * @param caseType Case type
     * @param player   Player, who opened
     * @param count    Number of opened cases
     */
    CompletableFuture<DatabaseStatus> setCount(String caseType, String player, int count);

    void setHistoryData(CaseData.History[] historyData);

    CompletableFuture<DatabaseStatus> setHistoryData(String caseType, CaseData.History data);

    /**
     * Sets history data for specific case type and index
     * @param caseType Case type
     * @param index Data index, if less than 0 - selects all data
     * @param data History data, if null - deletes
     * @return Future of DatabaseStatus
     */
    CompletableFuture<DatabaseStatus> setHistoryData(String caseType, int index, CaseData.History data);

    CompletableFuture<DatabaseStatus> removeHistoryData(String caseType);

    CompletableFuture<DatabaseStatus> removeHistoryData(String caseType, int index);

    CompletableFuture<List<CaseData.History>> getHistoryData();

    CompletableFuture<List<CaseData.History>> getHistoryData(String caseType);

    /**
     * Returns no-cached, if mysql disabled
     * @return list of all history data
     */
    List<CaseData.History> getCache();

    List<CaseData.History> getCache(String caseType);

    void close();

    DatabaseType getType();
}
