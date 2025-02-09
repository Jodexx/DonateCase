package com.jodexindustries.donatecase.common.config.converter;

import com.jodexindustries.donatecase.common.config.converter.migrators.CaseMigrator_1_2_to_1_3;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum ConfigType {

    CASE(13, new HashMap<Integer, ConfigMigrator>() {{
        put(12, new CaseMigrator_1_2_to_1_3());
    }});

    @Getter
    private final int latestVersion;
    private final Map<Integer, ConfigMigrator> migrations;

    ConfigType(int latestVersion, Map<Integer, ConfigMigrator> migrations) {
        this.latestVersion = latestVersion;
        this.migrations = migrations;
    }

    public ConfigMigrator getMigrator(int version) {
        return migrations.get(version);
    }

}