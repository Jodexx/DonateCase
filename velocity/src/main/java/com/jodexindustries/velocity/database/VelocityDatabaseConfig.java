package com.jodexindustries.velocity.database;

import lombok.Getter;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

public final class VelocityDatabaseConfig {

    private static final String FILE_NAME = "database.properties";

    @Getter
    private VelocityDatabaseType type = VelocityDatabaseType.SQLITE;
    private String host = "localhost";
    private int port = 3306;
    private String database = "donatecase";
    private String username = "admin";
    private String password = "123456";
    @Getter
    private long cacheMaxAgeTicks = 20L;

    public static VelocityDatabaseConfig load(Path dataFolder, Logger logger) {
        VelocityDatabaseConfig config = new VelocityDatabaseConfig();
        try {
            Files.createDirectories(dataFolder);
            Path file = dataFolder.resolve(FILE_NAME);
            if (Files.exists(file)) {
                Properties props = new Properties();
                try (InputStream in = Files.newInputStream(file)) {
                    props.load(in);
                }
                config.apply(props, logger);
            } else {
                config.save(file, logger);
            }
        } catch (Exception e) {
            logger.warn("Failed to load database config, using defaults", e);
        }
        return config;
    }

    private void apply(Properties props, Logger logger) {
        String typeValue = props.getProperty("type", type.name());
        try {
            this.type = VelocityDatabaseType.valueOf(typeValue.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            logger.warn("Unknown database type: {}, falling back to SQLITE", typeValue);
            this.type = VelocityDatabaseType.SQLITE;
        }

        this.host = props.getProperty("host", host);
        this.database = props.getProperty("database", database);
        this.username = props.getProperty("username", username);
        this.password = props.getProperty("password", password);

        this.port = parseInt(props.getProperty("port"), port);
        this.cacheMaxAgeTicks = parseLong(props.getProperty("cacheMaxAgeTicks"), cacheMaxAgeTicks);
    }

    private int parseInt(String value, int fallback) {
        if (value == null || value.isBlank()) return fallback;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private long parseLong(String value, long fallback) {
        if (value == null || value.isBlank()) return fallback;
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private void save(Path file, Logger logger) {
        Properties props = new Properties();
        props.setProperty("type", type.name());
        props.setProperty("host", host);
        props.setProperty("port", String.valueOf(port));
        props.setProperty("database", database);
        props.setProperty("username", username);
        props.setProperty("password", password);
        props.setProperty("cacheMaxAgeTicks", String.valueOf(cacheMaxAgeTicks));

        try (OutputStream out = Files.newOutputStream(file)) {
            props.store(out, "DonateCase Velocity database settings");
        } catch (Exception e) {
            logger.warn("Failed to save database config", e);
        }
    }

    public VelocityDatabaseSettings toSettings() {
        if (type == VelocityDatabaseType.SQLITE) return null;
        return new VelocityDatabaseSettings(host, port, database, username, password);
    }
}
