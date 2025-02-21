package com.jodexindustries.donatecase.common.config.converter;

import com.jodexindustries.donatecase.common.config.ConfigImpl;
import org.spongepowered.configurate.serialize.SerializationException;

public interface ConfigMigrator {

    void migrate(ConfigImpl config) throws SerializationException;

}
