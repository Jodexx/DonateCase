package com.jodexindustries.donatecase.common.config.converter.migrators;

import com.jodexindustries.donatecase.common.config.ConfigImpl;
import com.jodexindustries.donatecase.common.config.converter.ConfigMigrator;
import com.jodexindustries.donatecase.common.config.converter.ConfigType;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class UnknownMigrator implements ConfigMigrator {

    @Override
    public void migrate(ConfigImpl config) throws SerializationException {
        String name = config.file().getName().toLowerCase();

        ConfigurationNode node = config.node();
        ConfigType type = config.type();

        switch (name) {
            case "animations.yml": {
                type = ConfigType.ANIMATIONS;
                break;
            }
            case "config.yml": {
                type = ConfigType.CONFIG;
                break;
            }
            case "cases.yml": {
                type = ConfigType.CASES;
                break;
            }
        }

        if(config.path().contains("/lang")) type = ConfigType.LANG;

        if(type == ConfigType.UNKNOWN) type = ConfigType.UNKNOWN_CUSTOM;

        node.removeChild("config");
        node.node("config", "version").set(config.version());
        node.node("config", "type").set(type);
        config.type(type);
    }
}
