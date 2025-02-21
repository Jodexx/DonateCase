package com.jodexindustries.donatecase.common.config.converter;

import com.jodexindustries.donatecase.common.config.converter.migrators.CaseMigrator_1_2_to_1_3;
import com.jodexindustries.donatecase.common.config.converter.migrators.LangMigrator_2_6_to_2_7;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public enum ConfigType {

    OLD_CASE(13, new HashMap<Integer, ConfigMigrator>() {{
        put(12, new CaseMigrator_1_2_to_1_3());
    }}),

    CASE_GUI(10),
    CASE_SETTINGS(10),
    CASE_ITEMS(10),
    ANIMATIONS(14),
    CASES(10),
    CONFIG(25),
    LANG(27, new HashMap<Integer, ConfigMigrator>() {{
        put(26, new LangMigrator_2_6_to_2_7());
    }}),
    /**
     * Custom configuration unknown to DonateCase
     */
    UNKNOWN_CUSTOM(0),
    /**
     * Configuration is not of the specified type
     */
    UNKNOWN(0);

    @Getter
    private final int latestVersion;
    private Map<Integer, ConfigMigrator> migrations;

    ConfigType(int latestVersion) {
        this.latestVersion = latestVersion;
    }

    ConfigType(int latestVersion, Map<Integer, ConfigMigrator> migrations) {
        this(latestVersion);
        this.migrations = migrations;
    }

    public ConfigMigrator getMigrator(int version) {
        if (migrations == null) return null;
        return migrations.get(version);
    }

    @NotNull
    public static ConfigType getType(String name) {
        if (name != null) {
            try {
                return valueOf(name);
            } catch (IllegalArgumentException ex) {
                return UNKNOWN_CUSTOM;
            }
        }
        return UNKNOWN;
    }
}