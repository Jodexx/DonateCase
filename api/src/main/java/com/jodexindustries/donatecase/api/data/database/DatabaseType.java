package com.jodexindustries.donatecase.api.data.database;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.config.ConfigData.Database.Settings;

/**
 * Enum representing the types of databases supported by the DonateCase
 */
public enum DatabaseType {

    MYSQL {
        @Override
        public JdbcConnectionSource build(DCAPI api, Settings settings) throws Exception {
            String url = "jdbc:mysql://" + settings.host() + ":" + settings.port() + "/" + settings.database()
                    + "?autoReconnect=true&useSSL=false&characterEncoding=utf8";
            return new JdbcConnectionSource(url, settings.username(), settings.password());
        }
    },

    SQLITE {
        @Override
        public JdbcConnectionSource build(DCAPI api, Settings settings) throws Exception {
            String url = "jdbc:sqlite:" + api.getPlatform().getDataFolder().getAbsolutePath() + "/database.db";
            return new JdbcConnectionSource(url);
        }
    },

    POSTGRESQL {
        @Override
        public JdbcConnectionSource build(DCAPI api, Settings settings) throws Exception {
            String url = "jdbc:postgresql://" + settings.host() + ":" + settings.port() + "/" + settings.database();
            return new JdbcConnectionSource(url, settings.username(), settings.password());
        }
    };

    public abstract JdbcConnectionSource build(DCAPI api, Settings settings) throws Exception;
}

