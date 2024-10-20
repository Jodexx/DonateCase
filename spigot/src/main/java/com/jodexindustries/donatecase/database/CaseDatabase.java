package com.jodexindustries.donatecase.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.Level;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.table.TableUtils;
import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.DatabaseType;
import com.jodexindustries.donatecase.database.entities.HistoryDataTable;
import com.jodexindustries.donatecase.database.entities.OpenInfoTable;
import com.jodexindustries.donatecase.database.entities.PlayerKeysTable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CaseDatabase {
    private Dao<CaseData.HistoryData, String> historyDataTables;
    private Dao<PlayerKeysTable, String> playerKeysTables;
    private Dao<OpenInfoTable, String> openInfoTables;
    private JdbcConnectionSource connectionSource;

    private final DonateCase instance;

    public CaseDatabase(DonateCase instance) {
        this.instance = instance;
    }

    public void connect() {
        ConfigurationSection mysqlSection = instance.config.getConfig().getConfigurationSection("DonateCase.MySql");
        if(mysqlSection == null) instance.databaseType = DatabaseType.SQLITE;

        String url;

        try {
            if (instance.databaseType == DatabaseType.MYSQL) {
                String database = mysqlSection.getString("DataBase");
                String port = mysqlSection.getString("Port");
                String host = mysqlSection.getString("Host");
                String user = mysqlSection.getString("User");
                String password = mysqlSection.getString("Password");

                url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true";
                connectionSource = new JdbcConnectionSource(url, user, password);
            } else {
                connectionSource = new JdbcConnectionSource("jdbc:sqlite:" +
                        instance.getDataFolder().getAbsolutePath() + "/database.db");
            }

            com.j256.ormlite.logger.Logger.setGlobalLogLevel(Level.WARNING);

            TableUtils.createTableIfNotExists(connectionSource, HistoryDataTable.class);
            TableUtils.createTableIfNotExists(connectionSource, PlayerKeysTable.class);
            TableUtils.createTableIfNotExists(connectionSource, OpenInfoTable.class);
            historyDataTables = DaoManager.createDao(connectionSource, CaseData.HistoryData.class);
            playerKeysTables = DaoManager.createDao(connectionSource, PlayerKeysTable.class);
            openInfoTables = DaoManager.createDao(connectionSource, OpenInfoTable.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
                instance.getLogger().warning(e.getMessage());
            }
            return keys;
        });
    }

    public void setKeys(String name, String player, int keys) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {

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
                instance.getLogger().warning(e.getMessage());
            }
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
                instance.getLogger().warning(e.getMessage());
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
    public void setCount(String caseType, String player, int count) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
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
                instance.getLogger().warning(e.getMessage());
            }
        });
    }

    public void setHistoryData(CaseData.HistoryData[] historyData) {
        for (int index = 0; index < historyData.length; index++) {
            CaseData.HistoryData data = historyData[index];
            if(data == null) continue;

            setHistoryData(data.getCaseType(), index, data);
        }
    }

    public void setHistoryData(String caseType, int index, CaseData.HistoryData data) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            try {
                List<CaseData.HistoryData> results = historyDataTables.queryBuilder()
                        .where()
                        .eq("id", index)
                        .and()
                        .eq("case_type", caseType)
                        .query();

                CaseData.HistoryData historyDataTable = results.isEmpty() ? null : results.get(0);

                if (historyDataTable == null) {
                    data.setId(index);
                    historyDataTables.create(data);
                } else {
                    UpdateBuilder<CaseData.HistoryData, String> updateBuilder = historyDataTables.updateBuilder();
                    updateBuilder.updateColumnValue("item", data.getItem());
                    updateBuilder.updateColumnValue("player_name", data.getPlayerName());
                    updateBuilder.updateColumnValue("time", data.getTime());
                    updateBuilder.updateColumnValue("group", data.getGroup());
                    updateBuilder.updateColumnValue("action", data.getAction());
                    updateBuilder.where().eq("id", index).and().eq("case_type", caseType);
                    updateBuilder.update();
                }

            } catch (SQLException e) {
                instance.getLogger().warning(e.getMessage());
            }
        });

    }



    public CompletableFuture<List<CaseData.HistoryData>> getHistoryData() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return historyDataTables.queryForAll();
            } catch (SQLException e) {
                instance.getLogger().warning(e.getMessage());
            }
            return new ArrayList<>();
        });
    }

    public CompletableFuture<List<CaseData.HistoryData>> getHistoryDataByCaseType(String caseType) {
        return CompletableFuture.supplyAsync(() -> {
            List<CaseData.HistoryData> list = new ArrayList<>();
            try {
                list = historyDataTables.queryBuilder()
                        .where()
                        .eq("case_type", caseType)
                        .query();
            } catch (SQLException e) {
                instance.getLogger().warning(e.getMessage());
            }
            return list;
        });
    }


    public void delAllKeys() {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            try {
                playerKeysTables.deleteBuilder().delete();
            } catch (SQLException e) {
                instance.getLogger().warning(e.getMessage());
            }
        });
    }

    public void close() {
        if (connectionSource != null) {
            try {
                connectionSource.close();
            } catch (Exception e) {
                instance.getLogger().warning(e.getMessage());
            }
        }
    }

}