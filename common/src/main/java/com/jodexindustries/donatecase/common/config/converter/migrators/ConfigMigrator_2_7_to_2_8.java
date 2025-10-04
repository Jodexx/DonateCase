package com.jodexindustries.donatecase.common.config.converter.migrators;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.config.converter.ConfigMigrator;
import com.jodexindustries.donatecase.api.data.config.ConfigData;
import com.jodexindustries.donatecase.api.data.database.DatabaseType;
import com.jodexindustries.donatecase.common.DonateCase;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

public class ConfigMigrator_2_7_to_2_8 implements ConfigMigrator {

    @Deprecated
    @Override
    public void migrate(Config config) throws ConfigurateException {
        ConfigurationNode root = config.node();

        DCAPI api = DonateCase.getInstance();

        ConfigData configData = api.getConfigManager().getConfig();

        ConfigData.MySQL mysql = configData.mysql();

        root.removeChild("mysql");

        ConfigData.Database database = new ConfigData.Database()
                .type(mysql.enabled() ? DatabaseType.MYSQL : DatabaseType.SQLITE)
                .settings(mysql.toSettings());

        root.node("database").set(database);

        root.node("config", "version").set(28);
    }
}
