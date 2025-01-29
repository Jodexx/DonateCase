package com.jodexindustries.donatecase.common.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.Level;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.table.TableUtils;
import com.jodexindustries.donatecase.common.DonateCase;
import com.jodexindustries.donatecase.api.caching.SimpleCache;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.database.DatabaseType;
import com.jodexindustries.donatecase.api.database.CaseDatabase;
import com.jodexindustries.donatecase.common.database.entities.OpenInfoTable;
import com.jodexindustries.donatecase.common.database.entities.PlayerKeysTable;
import org.spongepowered.configurate.ConfigurationNode;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class CaseDatabaseImpl implements CaseDatabase {

    private Dao<CaseData.History, String> historyDataTables;
    private Dao<PlayerKeysTable, String> playerKeysTables;
    private Dao<OpenInfoTable, String> openInfoTables;
    private JdbcConnectionSource connectionSource;

    private final DonateCase api;
    private final Logger logger;
    private DatabaseType databaseType;

    /**
     * Cache map for storing cases histories
     */
    public final static SimpleCache<String, List<CaseData.History>> historyCache = new SimpleCache<>(20);

    public CaseDatabaseImpl(DonateCase api) {
        this.api = api;
        this.logger = api.getPlatform().getLogger();
    }

    @Override
    public void connect(String path) {
        try {
            close();
            connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + path + "/database.db");
            databaseType = DatabaseType.SQLITE;
            init();
            logger.info("Using SQLITE database type!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void connect(String database, String port, String host, String user, String password) {
        try {
            close();
            connectionSource = new JdbcConnectionSource("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
            databaseType = DatabaseType.MYSQL;
            init();
            logger.info("Using MYSQL database type!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void connect() {
        ConfigurationNode node = api.getConfig().getConfig().node("DonateCase", "MySql");
        if(node == null || !node.node("Enabled").getBoolean()) {
            connect(api.getPlatform().getDataFolder().getAbsolutePath());
            return;
        }

        String databaseName = node.node("DataBase").getString();
        String port = node.node("Port").getString();
        String host = node.node("Host").getString();
        String user = node.node("User").getString();
        String password = node.node("Password").getString();

        connect(
                databaseName,
                port,
                host,
                user,
                password
        );
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
    public CompletableFuture<Map<String, Integer>> getKeys(String player) {
        return CompletableFuture.supplyAsync(() -> {
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
                logger.warning(e.getMessage());
            }
            return keys;
        });
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public CompletableFuture<Map<String, Integer>> getOpenCount(String player) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Integer> opens = new HashMap<>();
            try {
                List<OpenInfoTable> results = openInfoTables.queryBuilder()
                        .where()
                        .eq("player", player)
                        .query();
                for (OpenInfoTable result : results) {
                    opens.put(result.getCaseType(), result.getCount());
                }
            } catch (SQLException e) {
                logger.warning(e.getMessage());
            }
            return opens;
        });
    }

    @Override
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

    @Override
    public void setHistoryData(CaseData.History[] historyData) {
        for (int index = 0; index < historyData.length; index++) {
            CaseData.History data = historyData[index];
            if (data == null) continue;

            setHistoryData(data.getCaseType(), index, data);
        }
    }

    @Override
    public CompletableFuture<DatabaseStatus> setHistoryData(String caseType, CaseData.History data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                QueryBuilder<CaseData.History, String> queryBuilder = historyDataTables.queryBuilder();
                queryBuilder.where().eq("case_type", caseType);

                List<CaseData.History> results = queryBuilder.query();

                for (CaseData.History historyDataTable : results) {
                    setHistoryDataTable(historyDataTable, historyDataTable.getId(), data);
                }

            } catch (SQLException e) {
                logger.warning(e.getMessage());
                return DatabaseStatus.FAIL;
            }
            return DatabaseStatus.COMPLETE;
        });

    }

    private void setHistoryDataTable(CaseData.History historyDataTable, int index, CaseData.History data) throws SQLException {
        if (historyDataTable == null) {
            data.setId(index);
            historyDataTables.create(data);
        } else {
            UpdateBuilder<CaseData.History, String> updateBuilder = historyDataTables.updateBuilder();
            updateBuilder.updateColumnValue("item", data.getItem());
            updateBuilder.updateColumnValue("player_name", data.getPlayerName());
            updateBuilder.updateColumnValue("time", data.getTime());
            updateBuilder.updateColumnValue("group", data.getGroup());
            updateBuilder.updateColumnValue("action", data.getAction());
            updateBuilder.where().eq("id", index).and().eq("case_type", data.getCaseType());
            updateBuilder.update();
        }
    }

    @Override
    public CompletableFuture<DatabaseStatus> setHistoryData(String caseType, int index, CaseData.History data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                QueryBuilder<CaseData.History, String> queryBuilder = historyDataTables.queryBuilder();
                queryBuilder.where().eq("case_type", caseType).and().eq("id", index);

                List<CaseData.History> results = queryBuilder.query();
                CaseData.History historyDataTable = results.isEmpty() ? null : results.get(0);
                setHistoryDataTable(historyDataTable, index, data);
            } catch (SQLException e) {
                logger.warning(e.getMessage());
                return DatabaseStatus.FAIL;
            }
            return DatabaseStatus.COMPLETE;
        });
    }

    @Override
    public CompletableFuture<DatabaseStatus> removeHistoryData(String caseType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DeleteBuilder<CaseData.History, String> deleteBuilder = historyDataTables.deleteBuilder();
                deleteBuilder.where().eq("case_type", caseType);
                deleteBuilder.delete();
                return DatabaseStatus.COMPLETE;
            } catch (SQLException e) {
                logger.warning(e.getMessage());
                return DatabaseStatus.FAIL;
            }
        });
    }

    @Override
    public CompletableFuture<DatabaseStatus> removeHistoryData(String caseType, int index) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DeleteBuilder<CaseData.History, String> deleteBuilder = historyDataTables.deleteBuilder();
                deleteBuilder.where().eq("case_type", caseType).and().eq("id", index);
                deleteBuilder.delete();
                return DatabaseStatus.COMPLETE;
            } catch (SQLException e) {
                logger.warning(e.getMessage());
                return DatabaseStatus.FAIL;
            }
        });
    }

    @Override
    public CompletableFuture<List<CaseData.History>> getHistoryData() {
        List<CaseData.History> result = new ArrayList<>();
        return CompletableFuture.supplyAsync(() -> {
            try {
                result.addAll(historyDataTables.queryForAll());
            } catch (SQLException e) {
                logger.warning(e.getMessage());
            }
            return result;
        });
    }

    @Override
    public CompletableFuture<List<CaseData.History>> getHistoryData(String caseType) {
        List<CaseData.History> result = new ArrayList<>();
        return CompletableFuture.supplyAsync(() -> {
            try {
                result.addAll(historyDataTables.queryBuilder()
                        .where()
                        .eq("case_type", caseType)
                        .query());
            } catch (SQLException e) {
                logger.warning(e.getMessage());
            }
            return result;
        });
    }


    @Override
    public List<CaseData.History> getCache() {
        if (databaseType == DatabaseType.SQLITE) return getHistoryData().join();

        List<CaseData.History> cachedList = historyCache.get("all!");

        if (cachedList != null) {
            return cachedList;
        }

        List<CaseData.History> previousList = historyCache.getPrevious("all!");

        getHistoryData().thenAcceptAsync(historyData -> historyCache.put("all!", historyData));

        return (previousList != null) ? previousList : getHistoryData().join();
    }

    @Override
    public List<CaseData.History> getCache(String caseType) {
        if (databaseType == DatabaseType.SQLITE) return getHistoryData(caseType).join();

        List<CaseData.History> cachedList = historyCache.get(caseType);

        if (cachedList != null) {
            return cachedList;
        }

        List<CaseData.History> previousList = historyCache.getPrevious(caseType);

        getHistoryData(caseType).thenAcceptAsync(historyData -> historyCache.put(caseType, historyData));

        return (previousList != null) ? previousList : getHistoryData(caseType).join();
    }

    @Override
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
}