package com.jodexindustries.donatecase.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.Level;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.table.TableUtils;
import com.jodexindustries.donatecase.api.caching.SimpleCache;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataHistory;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.database.DatabaseType;
import com.jodexindustries.donatecase.api.database.CaseDatabase;
import com.jodexindustries.donatecase.api.manager.CaseManager;
import com.jodexindustries.donatecase.database.entities.HistoryDataTable;
import com.jodexindustries.donatecase.database.entities.OpenInfoTable;
import com.jodexindustries.donatecase.database.entities.PlayerKeysTable;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CaseDatabaseImpl<C extends CaseData<M, I>, M extends CaseDataMaterial<I>, I> implements CaseDatabase {
    private Dao<HistoryDataTable, String> historyDataTables;
    private Dao<PlayerKeysTable, String> playerKeysTables;
    private Dao<OpenInfoTable, String> openInfoTables;
    private JdbcConnectionSource connectionSource;

    private final CaseManager<C> api;
    private final Logger logger;
    private DatabaseType databaseType;

    /**
     * Cache map for storing cases histories
     */
    public final static SimpleCache<Integer, List<CaseDataHistory>> historyCache = new SimpleCache<>(20);

    public CaseDatabaseImpl(CaseManager<C> api, Logger logger) {
        this.api = api;
        this.logger = logger;
    }

    /**
     * Connect via SQLITE
     *
     * @param path Database absolute path
     */
    public void connect(String path) {
        try {
            close();
            connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + path + "/database.db");
            databaseType = DatabaseType.SQLITE;
            init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Connect via MySQL
     *
     * @param database Database name
     * @param port     MySQL port
     * @param host     MySQL host
     * @param user     Database user
     * @param password User password
     */
    public void connect(String database, String port, String host, String user, String password) {
        try {
            close();
            connectionSource = new JdbcConnectionSource("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
            databaseType = DatabaseType.MYSQL;
            init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws SQLException {
        com.j256.ormlite.logger.Logger.setGlobalLogLevel(Level.WARNING);

        TableUtils.createTableIfNotExists(connectionSource, HistoryDataTable.class);
        TableUtils.createTableIfNotExists(connectionSource, PlayerKeysTable.class);
        TableUtils.createTableIfNotExists(connectionSource, OpenInfoTable.class);
        historyDataTables = DaoManager.createDao(connectionSource, HistoryDataTable.class);
        playerKeysTables = DaoManager.createDao(connectionSource, PlayerKeysTable.class);
        openInfoTables = DaoManager.createDao(connectionSource, OpenInfoTable.class);
    }

    public CompletableFuture<Integer> getKeys(String name, String player) {
        return CompletableFuture.supplyAsync(() -> {
            int keys = 0;
            try {
                List<PlayerKeysTable> results = playerKeysTables.queryBuilder()
                        .where()
                        .eq("player", player)
                        .and()
                        .eq("case_name", name)
                        .query();

                if (!results.isEmpty()) {
                    keys = results.get(0).getKeys();
                }
            } catch (SQLException e) {
                logger.warning(e.getMessage());
            }
            return keys;
        });
    }

    public CompletableFuture<DatabaseStatus> setKeys(String name, String player, int keys) {
        return CompletableFuture.supplyAsync(() -> {

            try {
                List<PlayerKeysTable> results = playerKeysTables.queryBuilder()
                        .where()
                        .eq("player", player)
                        .and()
                        .eq("case_name", name)
                        .query();
                PlayerKeysTable playerKeysTable = null;
                if (!results.isEmpty()) playerKeysTable = results.get(0);
                if (playerKeysTable == null) {
                    playerKeysTable = new PlayerKeysTable();
                    playerKeysTable.setPlayer(player);
                    playerKeysTable.setCaseType(name);
                    playerKeysTable.setKeys(keys);
                    playerKeysTables.create(playerKeysTable);
                } else {
                    UpdateBuilder<PlayerKeysTable, String> updateBuilder = playerKeysTables.updateBuilder();
                    updateBuilder.updateColumnValue("keys", keys);
                    updateBuilder.where().eq("player", player).and().eq("case_name", name);
                    updateBuilder.update();
                }
            } catch (SQLException e) {
                logger.warning(e.getMessage());
                return DatabaseStatus.FAIL;
            }
            return DatabaseStatus.COMPLETE;
        });
    }

    /**
     * Get count of opened cases by player
     *
     * @param player   Player, who opened
     * @param caseType Case type
     * @return number of opened cases
     */
    public CompletableFuture<Integer> getOpenCount(String player, String caseType) {
        return CompletableFuture.supplyAsync(() -> {
            OpenInfoTable openInfoTable = null;
            try {
                List<OpenInfoTable> results = openInfoTables.queryBuilder()
                        .where()
                        .eq("player", player)
                        .and()
                        .eq("case_type", caseType)
                        .query();
                if (!results.isEmpty()) openInfoTable = results.get(0);
            } catch (SQLException e) {
                logger.warning(e.getMessage());
            }
            if (openInfoTable != null) return (openInfoTable.getCount());
            return 0;
        });
    }

    /**
     * Set count of opened cases by player
     *
     * @param caseType Player, who opened
     * @param player   Case type
     * @param count    Number of opened cases
     */
    public CompletableFuture<DatabaseStatus> setCount(String caseType, String player, int count) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<OpenInfoTable> results = openInfoTables.queryBuilder()
                        .where()
                        .eq("player", player)
                        .and()
                        .eq("case_type", caseType)
                        .query();
                OpenInfoTable openInfoTable = null;
                if (!results.isEmpty()) openInfoTable = results.get(0);
                if (openInfoTable == null) {
                    openInfoTable = new OpenInfoTable();
                    openInfoTable.setPlayer(player);
                    openInfoTable.setCaseType(caseType);
                    openInfoTable.setCount(count);
                    openInfoTables.create(openInfoTable);
                } else {
                    UpdateBuilder<OpenInfoTable, String> updateBuilder = openInfoTables.updateBuilder();
                    updateBuilder.updateColumnValue("count", count);
                    updateBuilder.where().eq("player", player).and().eq("case_type", caseType);
                    updateBuilder.update();
                }
            } catch (SQLException e) {
                logger.warning(e.getMessage());
                return DatabaseStatus.FAIL;
            }
            return DatabaseStatus.COMPLETE;
        });
    }

    public void setHistoryData(CaseDataHistory[] historyData) {
        for (int index = 0; index < historyData.length; index++) {
            CaseDataHistory data = historyData[index];
            if (data == null) continue;

            setHistoryData(data.getCaseType(), index, data);
        }
    }

    public CompletableFuture<DatabaseStatus> setHistoryData(String caseType, int index, CaseDataHistory data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<HistoryDataTable> results = historyDataTables.queryBuilder()
                        .where()
                        .eq("id", index)
                        .and()
                        .eq("case_type", caseType)
                        .query();

                HistoryDataTable historyDataTable = results.isEmpty() ? null : results.get(0);

                if (historyDataTable == null) {
                    data.setId(index);
                    historyDataTables.create(new HistoryDataTable(data));
                } else {
                    UpdateBuilder<HistoryDataTable, String> updateBuilder = historyDataTables.updateBuilder();
                    updateBuilder.updateColumnValue("item", data.getItem());
                    updateBuilder.updateColumnValue("player_name", data.getPlayerName());
                    updateBuilder.updateColumnValue("time", data.getTime());
                    updateBuilder.updateColumnValue("group", data.getGroup());
                    updateBuilder.updateColumnValue("action", data.getAction());
                    updateBuilder.where().eq("id", index).and().eq("case_type", caseType);
                    updateBuilder.update();
                }

            } catch (SQLException e) {
                logger.warning(e.getMessage());
                return DatabaseStatus.FAIL;
            }
            return DatabaseStatus.COMPLETE;
        });

    }

    @Override
    public CompletableFuture<List<CaseDataHistory>> getHistoryData() {
        List<CaseDataHistory> result = new ArrayList<>();
        return CompletableFuture.supplyAsync(() -> {
            try {
                for (HistoryDataTable historyDataTable : historyDataTables.queryForAll()) {
                    CaseDataHistory historyData = historyDataTable.toHistoryData();
                    result.add(historyData);
                }
            } catch (SQLException e) {
                logger.warning(e.getMessage());
            }
            return result;
        });
    }

    @Override
    public CompletableFuture<List<CaseDataHistory>> getAsyncSortedHistoryData() {
        return CompletableFuture.supplyAsync(() -> databaseType == DatabaseType.SQLITE ?
                api.getMap().values().stream()
                        .filter(Objects::nonNull)
                        .flatMap(data -> {
                            CaseDataHistory[] temp = data.getHistoryData();
                            return temp != null ? Arrays.stream(temp) : Stream.empty();
                        })
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparingLong(CaseDataHistory::getTime).reversed())
                        .collect(Collectors.toList()) : getHistoryData().join().stream().filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(CaseDataHistory::getTime).reversed())
                .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<List<CaseDataHistory>> getHistoryDataByCaseType(String caseType) {
        List<CaseDataHistory> result = new ArrayList<>();
        return CompletableFuture.supplyAsync(() -> {
            try {
                for (HistoryDataTable historyDataTable : historyDataTables.queryBuilder()
                        .where()
                        .eq("case_type", caseType)
                        .query()) {
                    result.add(historyDataTable.toHistoryData());
                }
            } catch (SQLException e) {
                logger.warning(e.getMessage());
            }
            return result;
        });
    }

    @Override
    public List<CaseDataHistory> getSortedHistoryDataCache() {
        if (databaseType == DatabaseType.SQLITE) {
            return getAsyncSortedHistoryData().join();
        }

        List<CaseDataHistory> cachedList = historyCache.get(1);

        if (cachedList != null) {
            return cachedList;
        }

        List<CaseDataHistory> previousList = historyCache.getPrevious(1);

        getAsyncSortedHistoryData().thenAcceptAsync(historyData -> historyCache.put(1, historyData));

        return (previousList != null) ? previousList : getAsyncSortedHistoryData().join();
    }

    /**
     * Get sorted history data by case
     * @param historyData HistoryData from all cases (or not all)
     * @param caseType type of case for filtering
     * @return list of case HistoryData
     */
    public static List<CaseDataHistory> sortHistoryDataByCase(List<CaseDataHistory> historyData, String caseType) {
        return historyData.stream().filter(Objects::nonNull)
                .filter(data -> data.getCaseType().equals(caseType))
                .sorted(Comparator.comparingLong(CaseDataHistory::getTime).reversed())
                .collect(Collectors.toList());
    }

    public CompletableFuture<DatabaseStatus> delAllKeys() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                playerKeysTables.deleteBuilder().delete();
            } catch (SQLException e) {
                logger.warning(e.getMessage());
                return DatabaseStatus.FAIL;
            }
            return DatabaseStatus.COMPLETE;
        });
    }

    public void close() {
        if (connectionSource != null) {
            try {
                connectionSource.close();
            } catch (Exception e) {
                logger.warning(e.getMessage());
            }
        }
    }
}