package com.jodexindustries.velocity.database;

import lombok.Getter;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public final class VelocityDatabase {

    private final Logger logger;
    @Getter
    private Connection connection;
    private VelocityDatabaseType databaseType;

    public VelocityDatabase(Logger logger) {
        this.logger = logger;
    }

    @Deprecated
    public void connect(String path) {
        if (path == null || path.isBlank()) {
            logger.error("SQLite path is empty");
            return;
        }
        connect(Path.of(path), VelocityDatabaseType.SQLITE, null);
    }

    @Deprecated
    public void connect(String database, int port, String host, String user, String password) {
        VelocityDatabaseSettings settings = new VelocityDatabaseSettings(host, port, database, user, password);
        connect(null, VelocityDatabaseType.MYSQL, settings);
    }

    public void connect(Path dataFolder, VelocityDatabaseType type, VelocityDatabaseSettings settings) {
        this.databaseType = type;

        String url;
        try {
            url = buildUrl(dataFolder, type, settings);
        } catch (Exception e) {
            logger.error("Error while building url connection ({})", type, e);
            return;
        }
        connect(type, url, settings);
    }

    private void connect(VelocityDatabaseType type, String url, VelocityDatabaseSettings settings) {
        try {
            close();

            this.databaseType = type;
            this.connection = openConnection(type, url, settings);

            initSchema();

            logger.info("Using {} database type!", type);
        } catch (Exception e) {
            logger.error("Error while loading to database ({})", type, e);
            close();
        }
    }

    private String buildUrl(Path dataFolder, VelocityDatabaseType type, VelocityDatabaseSettings settings) throws Exception {
        return switch (type) {
            case MYSQL -> {
                if (settings == null) throw new IllegalArgumentException("MySQL settings are required");
                yield "jdbc:mysql://" + settings.host() + ":" + settings.port() + "/" + settings.database()
                        + "?autoReconnect=true&useSSL=false&characterEncoding=utf8";
            }
            case SQLITE -> {
                if (dataFolder == null) throw new IllegalArgumentException("dataFolder is required for SQLite");
                Files.createDirectories(dataFolder);
                Path databaseFile = dataFolder.resolve("database.db");
                yield "jdbc:sqlite:" + databaseFile.toAbsolutePath();
            }
            case POSTGRESQL -> {
                if (settings == null) throw new IllegalArgumentException("PostgreSQL settings are required");
                yield "jdbc:postgresql://" + settings.host() + ":" + settings.port() + "/" + settings.database();
            }
        };
    }

    private Connection openConnection(VelocityDatabaseType type, String url, VelocityDatabaseSettings settings) throws SQLException {
        tryLoadDriver(type);

        if (settings == null || settings.username() == null || settings.username().isBlank()) {
            return DriverManager.getConnection(url);
        }

        Properties props = new Properties();
        props.setProperty("user", settings.username());
        if (settings.password() != null) {
            props.setProperty("password", settings.password());
        }

        return DriverManager.getConnection(url, props);
    }

    private void tryLoadDriver(VelocityDatabaseType type) {
        String[] drivers = switch (type) {
            case MYSQL -> new String[]{"com.mysql.cj.jdbc.Driver", "com.mysql.jdbc.Driver"};
            case SQLITE -> new String[]{"org.sqlite.JDBC"};
            case POSTGRESQL -> new String[]{"org.postgresql.Driver"};
        };

        for (String driver : drivers) {
            try {
                Class.forName(driver);
                return;
            } catch (ClassNotFoundException ignored) {
                // Driver might be provided by the runtime.
            }
        }
    }

    private void initSchema() throws SQLException {
        if (connection == null) {
            throw new SQLException("Connection is not initialized");
        }

        String groupColumn = databaseType == VelocityDatabaseType.POSTGRESQL ? "\"group\"" : "`group`";
        String countColumn = databaseType == VelocityDatabaseType.POSTGRESQL ? "\"count\"" : "`count`";

        String historySql = "CREATE TABLE IF NOT EXISTS history_data ("
                + "id INTEGER,"
                + "item VARCHAR(255),"
                + "player_name VARCHAR(64),"
                + "time BIGINT,"
                + groupColumn + " VARCHAR(64),"
                + "case_type VARCHAR(64),"
                + "action VARCHAR(255)"
                + ")";

        String playerKeysSql = "CREATE TABLE IF NOT EXISTS player_keys ("
                + "player VARCHAR(64) NOT NULL,"
                + "case_name VARCHAR(64) NOT NULL,"
                + "keys INTEGER NOT NULL DEFAULT 0"
                + ")";

        String openInfoSql = "CREATE TABLE IF NOT EXISTS open_info ("
                + "player VARCHAR(64) NOT NULL,"
                + "case_type VARCHAR(64) NOT NULL,"
                + countColumn + " INTEGER NOT NULL DEFAULT 0"
                + ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(historySql);
            statement.executeUpdate(playerKeysSql);
            statement.executeUpdate(openInfoSql);
        }
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                logger.warn(e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    public VelocityDatabaseType getType() {
        return databaseType;
    }
}
