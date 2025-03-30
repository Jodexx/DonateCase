package com.jodexindustries.donatecase.api.config.converter;

import com.jodexindustries.donatecase.api.config.Config;
import org.spongepowered.configurate.serialize.SerializationException;

public interface ConfigMigrator {

    void migrate(Config config) throws SerializationException;

}
