package com.jodexindustries.donatecase.common.config;

import com.jodexindustries.donatecase.api.config.*;
import com.jodexindustries.donatecase.api.event.plugin.DonateCaseReloadEvent;
import com.jodexindustries.donatecase.common.config.converter.ConfigConverter;
import com.jodexindustries.donatecase.common.database.CaseDatabaseImpl;
import com.jodexindustries.donatecase.common.managers.CaseKeyManagerImpl;
import com.jodexindustries.donatecase.common.managers.CaseOpenManagerImpl;
import com.jodexindustries.donatecase.common.platform.BackendPlatform;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ConfigManagerImpl implements ConfigManager {

    private final Messages messages;
    private final CaseStorage caseStorage;
    private final ConfigConverter converter;

    private final Map<String, ConfigImpl> configurations = new HashMap<>();

    private static final String[] defaultFiles = {
            "Config.yml",
            "Cases.yml",
            "Animations.yml",
    };

    @Getter
    private final BackendPlatform platform;

    public ConfigManagerImpl(BackendPlatform platform) {
        this.platform = platform;
        this.caseStorage = new CaseStorageImpl(this);
        this.messages = new MessagesImpl(this);
        this.converter = new ConfigConverter(this);
    }

    @Override
    public @Nullable Config getConfig(@NotNull String name) {
        return configurations.get("plugins/DonateCase/" + name);
    }

    @Override
    @Nullable
    public ConfigurationNode get(@NotNull String name) {
        Config config = getConfig(name);
        return config != null ? config.node() : null;
    }

    @Override
    public Map<String, ? extends ConfigImpl> get() {
        return configurations;
    }

    @Override
    public void load() {
        configurations.clear();
        createFiles();
        loadConfigurations(platform.getDataFolder().listFiles(), false);

        try {
            messages.load(getConfig().node("DonateCase", "Languages").getString("en_US"));
            caseStorage.load();
        } catch (ConfigurateException e) {
            platform.getLogger().log(Level.WARNING, "Error with loading configuration: ", e);
        }

        converter.convert();

        long caching = getConfig().node("DonateCase", "Caching").getLong();
        if (caching >= 0) {
            CaseOpenManagerImpl.cache.setMaxAge(caching);
            CaseKeyManagerImpl.cache.setMaxAge(caching);
            CaseDatabaseImpl.cache.setMaxAge(caching);
        }

        platform.getAPI().getEventBus().post(new DonateCaseReloadEvent(DonateCaseReloadEvent.Type.CONFIG));
    }

    private void loadConfigurations(File[] files, boolean deep) {
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                String dirName = file.getName().toLowerCase();
                File[] subFiles = file.listFiles();

                if (deep || "cases".equals(dirName)) {
                    loadConfigurations(subFiles, true);
                } else if ("lang".equals(dirName)) {
                    loadConfigurations(subFiles, false);
                }
                continue;
            }

            String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(".yml") || fileName.endsWith(".yaml")) {
                load(file);
            }
        }
    }

    @Override
    public Config load(@NotNull File file) {
        String path = file.getPath().replace("\\", "/");
        Config exist = configurations.get(path);
        if (exist != null) return exist;

        ConfigImpl config = new ConfigImpl(path, file);

        try {
            config.load();
            configurations.put(path, config);
        } catch (ConfigurateException e) {
            platform.getLogger().log(Level.WARNING, "Error with loading configuration: ", e);
        }

        return config;
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
    public @NotNull CaseStorage getCaseStorage() {
        return caseStorage;
    }

}
