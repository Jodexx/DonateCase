package com.jodexindustries.donatecase.api.data.database;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.config.ConfigData.Database.Settings;

import java.sql.SQLException;

/**
 * Enum representing the types of databases supported by the DonateCase
 */
public enum DatabaseType {

    MYSQL {
        @Override
        public JdbcConnectionSource build(DCAPI api, Settings settings) throws Exception {
            String url = String.format("jdbc:mysql://%s:%d/%s?characterEncoding=utf8", settings.host(), settings.port(), settings.database());
            return pool(url, settings.username(), settings.password());
        }
    },

    SQLITE {
        @Override
        public JdbcConnectionSource build(DCAPI api, Settings settings) throws Exception {
            // a single connection is sufficient and pooling can cause locking issues
            String url = "jdbc:sqlite:" + api.getPlatform().getDataFolder().getAbsolutePath() + "/database.db";
            return new JdbcConnectionSource(url);
        }
    },

    POSTGRESQL {
        @Override
        public JdbcConnectionSource build(DCAPI api, Settings settings) throws Exception {
            String url = "jdbc:postgresql://" + settings.host() + ":" + settings.port() + "/" + settings.database();
            return pool(url, settings.username(), settings.password());
        }
    };

    public abstract JdbcConnectionSource build(DCAPI api, Settings settings) throws Exception;

    private static JdbcPooledConnectionSource pool(String url, String user, String password) throws SQLException {
        JdbcPooledConnectionSource pool = new JdbcPooledConnectionSource(url, user, password);
        pool.setTestBeforeGet(true);
        return pool;
    }
}
