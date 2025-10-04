package com.jodexindustries.donatecase.common.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.Level;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.table.TableUtils;
import com.jodexindustries.donatecase.api.config.converter.ConvertOrder;
import com.jodexindustries.donatecase.api.data.config.ConfigData;
import com.jodexindustries.donatecase.api.scheduler.DCFuture;
import com.jodexindustries.donatecase.common.DonateCase;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.database.DatabaseType;
import com.jodexindustries.donatecase.api.database.CaseDatabase;
import com.jodexindustries.donatecase.common.config.ConfigManagerImpl;
import com.jodexindustries.donatecase.common.database.entities.OpenInfoTable;
import com.jodexindustries.donatecase.common.database.entities.PlayerKeysTable;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CaseDatabaseImpl extends CaseDatabase {

    private Dao<CaseData.History, String> historyDataTables;
    private Dao<PlayerKeysTable, String> playerKeysTables;
    private Dao<OpenInfoTable, String> openInfoTables;
    private JdbcConnectionSource connectionSource;

    private final DonateCase api;
    private final Logger logger;
    private DatabaseType databaseType;

    public CaseDatabaseImpl(DonateCase api) {
        this.api = api;
        this.logger = api.getPlatform().getLogger();
    }

    @Deprecated
    @Override
    public void connect(String path) {
        DatabaseType databaseType = DatabaseType.SQLITE;

        try {
            connect(databaseType, new JdbcConnectionSource("jdbc:sqlite:" + path + "/database.db"));
        } catch (SQLException e) {
            logger.log(
                    java.util.logging.Level.SEVERE, "Error while building url connection (" + databaseType + ")", e
            );
        }
    }

    @Deprecated
    @Override
    public void connect(String database, int port, String host, String user, String password) {
        DatabaseType databaseType = DatabaseType.MYSQL;

        try {
            connect(databaseType, new JdbcConnectionSource("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password));
        } catch (SQLException e) {
            logger.log(
                    java.util.logging.Level.SEVERE, "Error while building url connection (" + databaseType + ")", e
            );
        }
    }

    @Override
    public void connect() {
        ConfigManagerImpl configManager = api.getConfigManager();

        ConfigData.Database database = configManager.getConfig().database();

        DatabaseType databaseType = database.type();

        try {
            connect(databaseType, databaseType.build(api, database.settings()));
        } catch (Exception e) {
            logger.log(
                    java.util.logging.Level.SEVERE, "Error while building url connection (" + databaseType + ")", e
            );
        }

        configManager.getConverter().convert(ConvertOrder.ON_DATABASE);
    }

    private void connect(DatabaseType databaseType, JdbcConnectionSource connectionSource) {
        try {
            close();

            this.connectionSource = connectionSource;
            this.databaseType = databaseType;

            init();

            logger.info("Using " + databaseType + " database type!");
        } catch (Exception e) {
            logger.log(
                    java.util.logging.Level.SEVERE, "Error while loading to database (" + databaseType + ")", e
            );
        }
    }

    private void init() throws SQLException {
        com.j256.ormlite.logger.Logger.setGlobalLogLevel(Level.WARNING);

        TableUtils.createTableIfNotExists(connectionSource, CaseData.History.class);
        TableUtils.createTableIfNotExists(connectionSource, PlayerKeysTable.class);
        TableUtils.createTableIfNotExists(connectionSource, OpenInfoTable.class);
        historyDataTables = DaoManager.createDao(connectionSource, CaseData.History.class);
        playerKeysTables = DaoManager.createDao(connectionSource, PlayerKeysTable.class);
        openInfoTables = DaoManager.createDao(connectionSource, OpenInfoTable.class);
    }

    @Override
    public DCFuture<Map<String, Integer>> getKeys(String player) {
        return DCFuture.supplyAsync(() -> {
            Map<String, Integer> keys = new HashMap<>();
            try {
                List<PlayerKeysTable> results = playerKeysTables.queryBuilder()
                        .where()
                        .eq("player", player)
                        .query();

                for (PlayerKeysTable result : results) {
                    keys.put(result.getCaseType(), result.getKeys());
                }
            } catch (SQLException e) {
                warning(e);
            }
            return keys;
        });
    }

    @Override
    public DCFuture<Integer> getKeys(String name, String player) {
        return DCFuture.supplyAsync(() -> {
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
                warning(e);
            }
            return keys;
        });
    }

    @Override
    public DCFuture<DatabaseStatus> setKeys(String name, String player, int keys) {
        return DCFuture.supplyAsync(() -> {

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
                warning(e);
                return DatabaseStatus.FAIL;
            }
            return DatabaseStatus.COMPLETE;
        });
    }

    public DCFuture<DatabaseStatus> setKeysBulk(String caseName, Map<String, Integer> playerKeysMap) {
        return DCFuture.supplyAsync(() -> {
            try {
                playerKeysTables.callBatchTasks(() -> {
                    for (Map.Entry<String, Integer> entry : playerKeysMap.entrySet()) {
                        String player = entry.getKey();
                        int keys = entry.getValue();

                        List<PlayerKeysTable> results = playerKeysTables.queryBuilder()
                                .where()
                                .eq("player", player)
                                .and()
                                .eq("case_name", caseName)
                                .query();

                        if (results.isEmpty()) {
                            PlayerKeysTable newEntry = new PlayerKeysTable();
                            newEntry.setPlayer(player);
                            newEntry.setCaseType(caseName);
                            newEntry.setKeys(keys);
                            playerKeysTables.create(newEntry);
                        } else {
                            UpdateBuilder<PlayerKeysTable, String> updateBuilder = playerKeysTables.updateBuilder();
                            updateBuilder.updateColumnValue("keys", keys);
                            updateBuilder.where()
                                    .eq("player", player)
                                    .and()
                                    .eq("case_name", caseName);
                            updateBuilder.update();
                        }
                    }
                    return null;
                });
            } catch (Exception e) {
                warning(e);
                return DatabaseStatus.FAIL;
            }
            return DatabaseStatus.COMPLETE;
        });
    }


    @Override
    public DCFuture<Integer> getOpenCount(String player, String caseType) {
        return DCFuture.supplyAsync(() -> {
            try {
                List<OpenInfoTable> results = openInfoTables.queryBuilder()
                        .where()
                        .eq("player", player)
                        .and()
                        .eq("case_type", caseType)
                        .query();

                return results.stream().mapToInt(OpenInfoTable::getCount).sum();
            } catch (SQLException e) {
                warning(e);
                return 0;
            }
        });
    }

    @Override
    public DCFuture<Map<String, Integer>> getOpenCount(String player) {
        return DCFuture.supplyAsync(() -> {
            Map<String, Integer> opens = new HashMap<>();
            try {
                List<OpenInfoTable> results = openInfoTables.queryBuilder()
                        .where()
                        .eq("player", player)
                        .query();
                for (OpenInfoTable result : results) {
                    opens.merge(result.getCaseType(), result.getCount(), Integer::sum);
                }
            } catch (SQLException e) {
                warning(e);
            }
            return opens;
        });
    }

    @Override
    public DCFuture<Map<String, Map<String, Integer>>> getGlobalOpenCount() {
        return DCFuture.supplyAsync(() -> {
            Map<String, Map<String, Integer>> globalMap = new HashMap<>();
            try {
                List<OpenInfoTable> results = openInfoTables.queryForAll();
                for (OpenInfoTable result : results) {
                    globalMap
                            .computeIfAbsent(result.getPlayer(), k -> new HashMap<>())
                            .merge(result.getCaseType(), result.getCount(), Integer::sum);
                }
            } catch (SQLException e) {
                warning(e);
            }
            return globalMap;
        });
    }

    @Override
    public DCFuture<Map<String, Integer>> getGlobalOpenCount(String caseType) {
        return DCFuture.supplyAsync(() -> {
            Map<String, Integer> opens = new HashMap<>();
            try {
                List<OpenInfoTable> results = openInfoTables.queryBuilder()
                        .where()
                        .eq("case_type", caseType)
                        .query();
                opens = results.stream().collect(Collectors.toMap(
                        OpenInfoTable::getPlayer,
                        OpenInfoTable::getCount,
                        Integer::sum
                ));
            } catch (SQLException e) {
                warning(e);
            }
            return opens;
        });
    }

    @Override
    public DCFuture<DatabaseStatus> setCount(String caseType, String player, int count) {
        return DCFuture.supplyAsync(() -> {
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
                warning(e);
                return DatabaseStatus.FAIL;
            }
            return DatabaseStatus.COMPLETE;
        });
    }

    @Override
    public DCFuture<DatabaseStatus> addHistory(String caseType, CaseData.History newEntry, int maxSize) {
        return DCFuture.supplyAsync(() -> {
            try {
                List<CaseData.History> entries = historyDataTables.queryBuilder().orderBy("time", true)
                        .where()
                        .eq("case_type", caseType)
                        .query();

                if (entries.size() >= maxSize) {
                    CaseData.History oldest = entries.get(0);
                    DeleteBuilder<CaseData.History, String> deleteBuilder = historyDataTables.deleteBuilder();
                    deleteBuilder.where().eq("time", oldest.time()).and().eq("case_type", caseType);
                    deleteBuilder.delete();
                }

                historyDataTables.create(newEntry);

                return DatabaseStatus.COMPLETE;
            } catch (SQLException e) {
                warning(e);
                return DatabaseStatus.FAIL;
            }
        });
    }


    private void setHistoryDataTable(CaseData.History historyDataTable, CaseData.History data) throws SQLException {
        if (historyDataTable == null) {
            historyDataTables.create(data);
        } else {
            UpdateBuilder<CaseData.History, String> updateBuilder = historyDataTables.updateBuilder();
            updateBuilder.updateColumnValue("player_name", data.playerName());
            updateBuilder.updateColumnValue("time", data.time());
            updateBuilder.updateColumnValue("group", data.group());
            updateBuilder.updateColumnValue("action", data.action());
            updateBuilder.where().eq("case_type", data.caseType()).and().eq("time", historyDataTable.time());
            updateBuilder.update();
        }
    }

    @Override
    public DCFuture<DatabaseStatus> setHistoryData(String caseType, int index, CaseData.History data) {
        return DCFuture.supplyAsync(() -> {
            try {
                QueryBuilder<CaseData.History, String> queryBuilder = historyDataTables.queryBuilder();
                queryBuilder.where().eq("case_type", caseType);

                List<CaseData.History> results = queryBuilder.query();
                CaseData.History historyDataTable = results.isEmpty() ? null : results.get(index);
                setHistoryDataTable(historyDataTable, data);
            } catch (SQLException e) {
                warning(e);
                return DatabaseStatus.FAIL;
            }
            return DatabaseStatus.COMPLETE;
        });
    }

    @Override
    public DCFuture<DatabaseStatus> removeHistoryData(String caseType) {
        return DCFuture.supplyAsync(() -> {
            try {
                DeleteBuilder<CaseData.History, String> deleteBuilder = historyDataTables.deleteBuilder();
                deleteBuilder.where().eq("case_type", caseType);
                deleteBuilder.delete();
                return DatabaseStatus.COMPLETE;
            } catch (SQLException e) {
                warning(e);
                return DatabaseStatus.FAIL;
            }
        });
    }

    @Override
    public DCFuture<DatabaseStatus> removeHistoryData(String caseType, int index) {
        return DCFuture.supplyAsync(() -> {
            try {
                DeleteBuilder<CaseData.History, String> deleteBuilder = historyDataTables.deleteBuilder();
                deleteBuilder.where().eq("case_type", caseType).and().eq("id", index);
                deleteBuilder.delete();
                return DatabaseStatus.COMPLETE;
            } catch (SQLException e) {
                warning(e);
                return DatabaseStatus.FAIL;
            }
        });
    }

    @Override
    public DCFuture<List<CaseData.History>> getHistoryData() {
        List<CaseData.History> result = new ArrayList<>();
        return DCFuture.supplyAsync(() -> {
            try {
                result.addAll(historyDataTables.queryForAll());
            } catch (SQLException e) {
                warning(e);
            }
            return result;
        });
    }

    @Override
    public DCFuture<List<CaseData.History>> getHistoryData(String caseType) {
        List<CaseData.History> result = new ArrayList<>();
        return DCFuture.supplyAsync(() -> {
            try {
                result.addAll(historyDataTables.queryBuilder().orderBy("time", true)
                        .where()
                        .eq("case_type", caseType)
                        .query());
            } catch (SQLException e) {
                warning(e);
            }
            return result;
        });
    }


    @Override
    public List<CaseData.History> getCache() {
        if (databaseType == DatabaseType.SQLITE) return getHistoryData().join();

        List<CaseData.History> cachedList = cache.get("all!");

        if (cachedList != null) {
            return cachedList;
        }

        List<CaseData.History> previousList = cache.getPrevious("all!");

        getHistoryData().thenAcceptAsync(historyData -> cache.put("all!", historyData));

        return (previousList != null) ? previousList : getHistoryData().join();
    }

    @Override
    public List<CaseData.History> getCache(String caseType) {
        if (databaseType == DatabaseType.SQLITE) return getHistoryData(caseType).join();

        List<CaseData.History> cachedList = cache.get(caseType);

        if (cachedList != null) {
            return cachedList;
        }

        List<CaseData.History> previousList = cache.getPrevious(caseType);

        getHistoryData(caseType).thenAcceptAsync(historyData -> cache.put(caseType, historyData));

        return (previousList != null) ? previousList : getHistoryData(caseType).join();
    }

    @Override
    public DCFuture<DatabaseStatus> delAllKeys() {
        return DCFuture.supplyAsync(() -> {
            try {
                playerKeysTables.deleteBuilder().delete();
            } catch (SQLException e) {
                warning(e);
                return DatabaseStatus.FAIL;
            }
            return DatabaseStatus.COMPLETE;
        });
    }

    @Override
    public DCFuture<DatabaseStatus> delKeys(String caseType) {
        return DCFuture.supplyAsync(() -> {
            try {
                DeleteBuilder<PlayerKeysTable, String> deleteBuilder = playerKeysTables.deleteBuilder();
                deleteBuilder.where().eq("case_name", caseType);
                deleteBuilder.delete();
            } catch (SQLException e) {
                warning(e);
                return DatabaseStatus.FAIL;
            }
            return DatabaseStatus.COMPLETE;
        });
    }

    @Override
    public void close() {
        if (connectionSource != null) {
            try {
                connectionSource.close();
            } catch (Exception e) {
                logger.warning(e.getMessage());
            }
        }
    }

    @Override
    public DatabaseType getType() {
        return databaseType;
    }

    private void warning(Throwable e) {
        logger.log(java.util.logging.Level.WARNING, "Error with database query:", e);
    }
}
