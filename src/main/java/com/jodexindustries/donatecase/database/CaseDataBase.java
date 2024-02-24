package com.jodexindustries.donatecase.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.jodexindustries.donatecase.database.entities.HistoryDataTable;
import com.jodexindustries.donatecase.database.entities.PlayerKeysTable;
import com.jodexindustries.donatecase.DonateCase;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.List;

public class CaseDataBase {
    private Dao<HistoryDataTable, Integer> historyDataTables;
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
            historyDataTables = DaoManager.createDao(connectionSource, HistoryDataTable.class);
            playerKeysTables = DaoManager.createDao(connectionSource, PlayerKeysTable.class);
        } catch (SQLException e) {
            instance.getLogger().warning(e.getMessage());
            Bukkit.getPluginManager().disablePlugin(instance);
        }
    }

    public int getKey(String name, String player) {
        try {
            player = player.toLowerCase();
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
        try {
            PlayerKeysTable playerKeysTable = playerKeysTables.queryForId(player);
            if (playerKeysTable == null) {
                playerKeysTable = new PlayerKeysTable();
                playerKeysTable.setPlayer(player);
                playerKeysTable.setCaseName(name);
                playerKeysTable.setKeys(keys);
                playerKeysTables.create(playerKeysTable);
            } else {
                playerKeysTable.setKeys(keys);
                playerKeysTables.update(playerKeysTable);
            }
        } catch (SQLException e) {
            instance.getLogger().warning(e.getMessage());
        }
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
