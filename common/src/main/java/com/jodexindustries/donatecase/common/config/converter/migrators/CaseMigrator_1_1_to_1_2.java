package com.jodexindustries.donatecase.common.config.converter.migrators;

import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.config.converter.ConfigMigrator;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class CaseMigrator_1_1_to_1_2 implements ConfigMigrator {

    @Override
    public void migrate(Config config) throws SerializationException {
        ConfigurationNode root = config.node();
        root.node("OpenType").set("GUI");
        root.node("config").set(12);
    }

}
