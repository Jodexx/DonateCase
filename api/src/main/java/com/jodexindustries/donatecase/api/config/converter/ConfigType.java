package com.jodexindustries.donatecase.api.config.converter;

public interface ConfigType {

    int getLatestVersion();

    boolean isPermanent();

    ConfigMigrator getMigrator(int version);

}
