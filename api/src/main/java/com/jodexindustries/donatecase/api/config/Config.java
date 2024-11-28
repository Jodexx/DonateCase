package com.jodexindustries.donatecase.api.config;

import com.jodexindustries.donatecase.api.data.database.DatabaseType;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface Config {

    void load();

    void delete(@NotNull File file);

    void delete(@NotNull String name);

    boolean save(String name);

    boolean save(File file);

    void saveLang();

    DatabaseType getDatabaseType();
}
