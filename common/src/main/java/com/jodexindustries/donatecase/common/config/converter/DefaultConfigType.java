package com.jodexindustries.donatecase.common.config.converter;

import com.jodexindustries.donatecase.api.config.converter.ConfigMigrator;
import com.jodexindustries.donatecase.api.config.converter.ConfigType;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseItems;
import com.jodexindustries.donatecase.api.data.config.ConfigData;
import com.jodexindustries.donatecase.api.data.config.ConfigSerializer;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMenu;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseSettings;
import com.jodexindustries.donatecase.common.config.converter.migrators.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum DefaultConfigType implements ConfigType {

    /**
     * Legacy case configuration format used prior to the introduction of modular formats:
     * {@link #CASE_MENU}, {@link #CASE_ITEMS}, and {@link #CASE_SETTINGS}.
     *
     * <p>Version {@code 14} is a virtual/fake version used solely for migration purposes.</p>
     * <p>Automatically triggers conversion to the new case format structure.</p>
     */
    OLD_CASE(14, new HashMap<Integer, ConfigMigrator>() {{
        put(12, new CaseMigrator_1_2_to_1_3());
        put(13, new CaseMigrator_1_3_to_1_4());
    }}),

    CASE_MENU(1, new ConfigSerializer(CaseMenu.class)),

    CASE_SETTINGS(1, new ConfigSerializer(CaseSettings.class)),

    CASE_ITEMS(1, new ConfigSerializer(CaseItems.class, "items")),

    ANIMATIONS(15, new HashMap<Integer, ConfigMigrator>() {{
        put(14, new AnimationsMigrator_1_4_to_1_5());
    }}),

    CASES(11, new HashMap<Integer, ConfigMigrator>() {{
        put(10, new CasesMigrator_1_0_to_1_1());
    }}),

    CONFIG(26,
            new HashMap<Integer, ConfigMigrator>() {{
                put(25, new ConfigMigrator_2_5_to_2_6());
            }},
            new ConfigSerializer(ConfigData.class)),

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
    private ConfigSerializer configSerializer;

    private Map<Integer, ConfigMigrator> migrations;

    DefaultConfigType(int latestVersion) {
        this.latestVersion = latestVersion;
    }

    DefaultConfigType(int latestVersion, ConfigSerializer configSerializer) {
        this(latestVersion);
        this.configSerializer = configSerializer;
    }

    DefaultConfigType(int latestVersion, Map<Integer, ConfigMigrator> migrations) {
        this(latestVersion);
        this.migrations = migrations;
    }

    DefaultConfigType(int latestVersion, Map<Integer, ConfigMigrator> migrations, ConfigSerializer configSerializer) {
        this(latestVersion, migrations);
        this.configSerializer = configSerializer;
    }

    DefaultConfigType(boolean permanent, ConfigMigrator permanentMigrator) {
        this.permanent = permanent;
        this.permanentMigrator = permanentMigrator;
    }

    public boolean isUnknown() {
        return this == UNKNOWN || this == UNKNOWN_CUSTOM;
    }

    @Override
    public ConfigMigrator getMigrator(int version) {
        if (migrations == null) return permanentMigrator;
        return migrations.get(version);
    }

    @Override
    public @Nullable ConfigSerializer getConfigSerializer() {
        return configSerializer;
    }

    @Override
    public boolean isPermanent() {
        return permanent;
    }

    @Override
    public String getName() {
        return toString();
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
