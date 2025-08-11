package com.jodexindustries.donatecase.common.config.converter.migrators;

import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.config.converter.ConfigMigrator;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

public class ConfigMigrator_2_6_to_2_7 implements ConfigMigrator {

    @Override
    public void migrate(Config config) throws ConfigurateException {
        ConfigurationNode root = config.node();

        ConfigurationNode converter = root.node("converter");
        converter.node("keys").set(false);
        converter.node("data").set(false);

        root.node("config", "version").set(27);
    }
}
