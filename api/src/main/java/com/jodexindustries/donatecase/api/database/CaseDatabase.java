package com.jodexindustries.donatecase.api.database;

import com.jodexindustries.donatecase.api.data.casedata.CaseDataHistory;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CaseDatabase {

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

    /**
     * Set count of opened cases by player
     *
     * @param caseType Case type
     * @param player   Player, who opened
     * @param count    Number of opened cases
     */
    CompletableFuture<DatabaseStatus> setCount(String caseType, String player, int count);

    void setHistoryData(CaseDataHistory[] historyData);

    CompletableFuture<DatabaseStatus> setHistoryData(String caseType, CaseDataHistory data);

    /**
     * Sets history data for specific case type and index
     * @param caseType Case type
     * @param index Data index, if less than 0 - selects all data
     * @param data History data, if null - deletes
     * @return Future of DatabaseStatus
     */
    CompletableFuture<DatabaseStatus> setHistoryData(String caseType, int index, CaseDataHistory data);

    CompletableFuture<DatabaseStatus> removeHistoryData(String caseType);

    CompletableFuture<DatabaseStatus> removeHistoryData(String caseType, int index);

    CompletableFuture<List<CaseDataHistory>> getHistoryData();

    CompletableFuture<List<CaseDataHistory>> getAsyncSortedHistoryData();

    CompletableFuture<List<CaseDataHistory>> getHistoryDataByCaseType(String caseType);

    /**
     * Returns no-cached, if mysql disabled
     * @return list of history data
     */
    List<CaseDataHistory> getSortedHistoryDataCache();

    void close();
}
