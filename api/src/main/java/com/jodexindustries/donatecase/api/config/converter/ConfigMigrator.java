package com.jodexindustries.donatecase.api.config.converter;

import com.jodexindustries.donatecase.api.config.Config;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;

public interface ConfigMigrator {

    void migrate(Config config) throws ConfigurateException;

    @NotNull
    default ConvertOrder order() {
        return ConvertOrder.ON_CONFIG;
    }
}
