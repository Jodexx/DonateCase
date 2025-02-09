package com.jodexindustries.donatecase.common.config;

import com.jodexindustries.donatecase.api.config.*;
import com.jodexindustries.donatecase.api.event.plugin.DonateCaseReloadEvent;
import com.jodexindustries.donatecase.common.database.CaseDatabaseImpl;
import com.jodexindustries.donatecase.common.managers.CaseKeyManagerImpl;
import com.jodexindustries.donatecase.common.managers.CaseOpenManagerImpl;
import com.jodexindustries.donatecase.common.platform.BackendPlatform;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ConfigManagerImpl implements ConfigManager {

    private final Messages messages;
    private final ConfigCases configCases;
    private final CaseStorage caseStorage;

    private final Map<String, Config> configurations = new HashMap<>();

    private static final String[] defaultFiles = {
            "Config.yml",
            "Cases.yml",
            "Animations.yml",
            "lang/en_US.yml",
    };

    @Getter
    private final BackendPlatform platform;

    public ConfigManagerImpl(BackendPlatform platform) {
        this.platform = platform;
        this.caseStorage = new CaseStorageImpl(this);
        this.messages = new MessagesImpl(platform);
        this.configCases = new ConfigCasesImpl(this);
    }

    @Override
    public @Nullable Config getConfig(@NotNull String name) {
        return configurations.get(name);
    }

    @Override
    @Nullable
    public ConfigurationNode get(@NotNull String name) {
        Config config = configurations.get(name);
        return config != null ? config.node() : null;
    }

    @Override
    public void load() {
        createFiles();
        loadConfigurations(platform.getDataFolder().listFiles());

        try {
            messages.load(getConfig().node("DonateCase", "Languages").getString("en_US"));
            caseStorage.load();
            configCases.load();
        } catch (ConfigurateException e) {
            platform.getLogger().log(Level.WARNING, "Error with loading configuration: ", e);
        }

        long caching = getConfig().node("DonateCase", "Caching").getLong();
        if (caching >= 0) {
            CaseOpenManagerImpl.cache.setMaxAge(caching);
            CaseKeyManagerImpl.cache.setMaxAge(caching);
            CaseDatabaseImpl.cache.setMaxAge(caching);
        }

        platform.getAPI().getEventBus().post(new DonateCaseReloadEvent(DonateCaseReloadEvent.Type.CONFIG));
    }

    private void loadConfigurations(File[] files) {
        if (files == null) return;
        for (File file : files) {
            if (!file.isFile()) continue;

            loadConfiguration(file);
        }
    }

    private void loadConfiguration(File file) {
        if (file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")) {
            try {
                ConfigImpl config = new ConfigImpl(file);
                config.load();

                configurations.put(file.getName(), config);
            } catch (ConfigurateException e) {
                platform.getLogger().log(Level.WARNING, "Error with loading configuration: ", e);
            }
        }
    }

    private void createFiles() {
        for (String fileName : defaultFiles) {
            File file = new File(platform.getDataFolder(), fileName);
            if (!file.exists()) platform.saveResource(fileName, false);
        }
    }

    @Override
    public @NotNull Messages getMessages() {
        return messages;
    }

    @Override
    public @NotNull ConfigCases getConfigCases() {
        return configCases;
    }

    @Override
    public @NotNull CaseStorage getCaseStorage() {
        return caseStorage;
    }

}
