package com.jodexindustries.donatecase.api.database;

import com.jodexindustries.donatecase.api.data.casedata.CaseDataHistory;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;

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
     * @param caseType Player, who opened
     * @param player   Case type
     * @param count    Number of opened cases
     */
    CompletableFuture<DatabaseStatus> setCount(String caseType, String player, int count);

    CompletableFuture<DatabaseStatus> setHistoryData(String caseType, int index, CaseDataHistory data);
}
