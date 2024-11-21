package com.jodexindustries.donatecase.api.config;

import com.jodexindustries.donatecase.api.data.database.DatabaseType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public interface ConfigBukkit extends Config {

    @Nullable
    YamlConfiguration get(@NotNull File file);

    @Nullable
    YamlConfiguration get(@NotNull String name);

    default YamlConfiguration getCases() {
        return get("Cases.yml");
    }

    default YamlConfiguration getConfig() {
        return get("Config.yml");
    }

    default YamlConfiguration getAnimations() {
        return get("Animations.yml");
    }

    default DatabaseType getDatabaseType() {
        return getConfig().getBoolean("MySql.Enabled") ? DatabaseType.MYSQL : DatabaseType.SQLITE;
    }


    YamlConfiguration getLang();

    ConfigCasesBukkit getConfigCases();
}
