package com.jodexindustries.donatecase.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

public class LangConfig extends YamlConfiguration {

    @NotNull
    public String getString(@NotNull String var) {
        String value = super.getString(var);
        if (value == null) value = var;
        return value;
    }

    public static @NotNull LangConfig loadConfiguration(@NotNull File file) {
        LangConfig config = new LangConfig();

        try {
            config.load(file);
        } catch (FileNotFoundException ignored) {
        } catch (IOException | InvalidConfigurationException var4) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, var4);
        }

        return config;
    }
}
