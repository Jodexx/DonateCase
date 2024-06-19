package com.jodexindustries.donatecase.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.table.TableUtils;
import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.database.entities.HistoryDataTable;
import com.jodexindustries.donatecase.database.entities.PlayerKeysTable;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CaseDataBase {
    private Dao<CaseData.HistoryData , String> historyDataTables;
    private Dao<PlayerKeysTable, String> playerKeysTables;
    private JdbcConnectionSource connectionSource;
    private final DonateCase instance;

    public CaseDataBase(DonateCase instance, String database, String port, String host, String user, String password) {
        this.instance = instance;
        try {
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true";
            connectionSource = new JdbcConnectionSource(url, user, password);
            TableUtils.createTableIfNotExists(connectionSource, HistoryDataTable.class);
            TableUtils.createTableIfNotExists(connectionSource, PlayerKeysTable.class);
            historyDataTables = DaoManager.createDao(connectionSource, CaseData.HistoryData.class);
            playerKeysTables = DaoManager.createDao(connectionSource, PlayerKeysTable.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getKey(String name, String player) {
        try {
            List<PlayerKeysTable> results = playerKeysTables.queryBuilder()
                    .where()
                    .eq("player", player)
                    .and()
                    .eq("case_name", name)
                    .query();

            if (!results.isEmpty()) {
                return results.get(0).getKeys();
            }
        } catch (SQLException e) {
            instance.getLogger().warning(e.getMessage());
        }
        return 0;
    }

    public void setKey(String name, String player, int keys) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () ->{

        try {
            List<PlayerKeysTable> results = playerKeysTables.queryBuilder()
                    .where()
                    .eq("player", player)
                    .and()
                    .eq("case_name", name)
                    .query();
            PlayerKeysTable playerKeysTable = null;
            if(!results.isEmpty()) playerKeysTable = results.get(0);
            if (playerKeysTable == null) {
                playerKeysTable = new PlayerKeysTable();
                playerKeysTable.setPlayer(player);
                playerKeysTable.setCaseName(name);
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
    public void setHistoryData(String caseName, int index, CaseData.HistoryData data) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            try {
                CaseData.HistoryData historyDataTable = null;
                List<CaseData.HistoryData> results = historyDataTables.queryBuilder()
                        .where()
                        .eq("id", index)
                        .and()
                        .eq("case_type", caseName)
                        .query();
                if (!results.isEmpty()) historyDataTable = results.get(0);
                if (historyDataTable == null) {
                    historyDataTable = data;
                    historyDataTable.setId(index);
                    historyDataTables.create(historyDataTable);
                } else {

                    UpdateBuilder<CaseData.HistoryData, String> updateBuilder = historyDataTables.updateBuilder();
                    updateBuilder.updateColumnValue("item", data.getItem());
                    updateBuilder.updateColumnValue("player_name", data.getPlayerName());
                    updateBuilder.updateColumnValue("time", data.getTime());
                    updateBuilder.updateColumnValue("group", data.getGroup());
                    updateBuilder.updateColumnValue("action", data.getAction());
                    updateBuilder.where().eq("id", index).and().eq("case_type", caseName);
                    updateBuilder.update();
                }

            } catch (SQLException e) {
                instance.getLogger().warning(e.getMessage());
            }
        });
    }
    public List<CaseData.HistoryData> getHistoryData() {
        List<CaseData.HistoryData> list = new ArrayList<>();
        try {
            list = historyDataTables.queryForAll();
        } catch (SQLException e) {
            instance.getLogger().warning(e.getMessage());
        }
        return list;
    }
    public List<CaseData.HistoryData> getHistoryDataByCaseType(String caseType) {
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
    }


    public void delAllKey() {
        try {
            playerKeysTables.deleteBuilder().delete();
        } catch (SQLException e) {
            instance.getLogger().warning(e.getMessage());
        }
    }

    public void close() {
        try {
            connectionSource.close();
        } catch (Exception e) {
            instance.getLogger().warning(e.getMessage());
        }
    }

}
