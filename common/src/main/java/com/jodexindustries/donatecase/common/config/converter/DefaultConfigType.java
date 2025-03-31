package com.jodexindustries.donatecase.common.config.converter;

import com.jodexindustries.donatecase.api.config.converter.ConfigMigrator;
import com.jodexindustries.donatecase.api.config.converter.ConfigType;
import com.jodexindustries.donatecase.common.config.converter.migrators.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public enum DefaultConfigType implements ConfigType {

    OLD_CASE(13, new HashMap<Integer, ConfigMigrator>() {{
        put(12, new CaseMigrator_1_2_to_1_3());
    }}),

    CASE_GUI(10),
    CASE_SETTINGS(10),
    CASE_ITEMS(10),
    ANIMATIONS(15, new HashMap<Integer, ConfigMigrator>() {{
        put(14, new AnimationsMigrator_1_4_to_1_5());
    }}),
    CASES(11, new HashMap<Integer, ConfigMigrator>() {{
        put(10, new CasesMigrator_1_0_to_1_1());
    }}),

    CONFIG(25),
    LANG(27, new HashMap<Integer, ConfigMigrator>() {{
        put(26, new LanguageMigrator_2_6_to_2_7());
    }}),
    /**
     * Custom configuration unknown to DonateCase
     */
    UNKNOWN_CUSTOM(0),
    /**
     * Configuration is not of the specified type
     */
    UNKNOWN(true, new UnknownMigrator());

    private int latestVersion;
    private boolean permanent;
    private ConfigMigrator permanentMigrator;

    private Map<Integer, ConfigMigrator> migrations;

    DefaultConfigType(int latestVersion) {
        this.latestVersion = latestVersion;
    }

    DefaultConfigType(int latestVersion, Map<Integer, ConfigMigrator> migrations) {
        this(latestVersion);
        this.migrations = migrations;
    }

    DefaultConfigType(boolean permanent, ConfigMigrator permanentMigrator) {
        this.permanent = permanent;
        this.permanentMigrator = permanentMigrator;
    }

    @Override
    public ConfigMigrator getMigrator(int version) {
        if (migrations == null) return permanentMigrator;
        return migrations.get(version);
    }

    @Override
    public boolean isPermanent() {
        return permanent;
    }

    @Override
    public int getLatestVersion() {
        return latestVersion;
    }

    @NotNull
    public static DefaultConfigType getType(String name) {
        if (name != null) {
            try {
                return valueOf(name.toUpperCase());
            } catch (IllegalArgumentException ex) {
                return UNKNOWN_CUSTOM;
            }
        }
        return UNKNOWN;
    }
}