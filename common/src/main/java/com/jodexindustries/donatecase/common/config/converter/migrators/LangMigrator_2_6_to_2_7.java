package com.jodexindustries.donatecase.common.config.converter.migrators;

import com.jodexindustries.donatecase.common.config.converter.ConfigMigrator;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class LangMigrator_2_6_to_2_7 implements ConfigMigrator {

    @Override
    public void migrate(ConfigurationNode root) throws SerializationException {
        root.removeChild("config");

        root.node("config", "version").set("27");
        root.node("config", "type").set("lang");
    }
}
