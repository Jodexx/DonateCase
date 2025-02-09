package com.jodexindustries.donatecase.common.config.converter;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public interface ConfigMigrator {

    void migrate(ConfigurationNode root) throws SerializationException;

}
