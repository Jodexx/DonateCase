package com.jodexindustries.donatecase.common.config;

import com.jodexindustries.donatecase.api.config.Messages;
import com.jodexindustries.donatecase.common.platform.BackendPlatform;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MessagesImpl implements Messages {

    private final static String DEFAULT_LANG = "lang/en_US.yml";

    private final ConfigManagerImpl configManager;
    private final BackendPlatform platform;

    private ConfigImpl config;

    public MessagesImpl(ConfigManagerImpl configManager) {
        this.configManager = configManager;
        this.platform = configManager.getPlatform();
    }

    @Override
    public @NotNull ConfigImpl get() {
        return config;
    }

    @Override
    public @NotNull String getString(@NotNull Object... path) {
        String value = config.node(path).getString();
        return value == null ? "" : value;
    }

    @Override
    public @NotNull String getString(@NotNull String path, @NotNull String def) {
        return config.node((Object[]) path.split("\\.")).getString(def);
    }

    @Override
    public @NotNull List<String> getStringList(@NotNull String path) {
        try {
            return config.node(path).getList(String.class, new ArrayList<>());
        } catch (SerializationException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void load(@NotNull String language) throws ConfigurateException {
        String path = "lang/" + language + ".yml";
        ConfigImpl config = this.configManager.getConfig(path);

        if (config == null) {
            if(platform.getResource(path) != null) {
                platform.saveResource(path, false);
                config = this.configManager.load(new File(platform.getDataFolder(), path));
            } else {
                config = loadDefault();
                platform.getLogger().warning("Language file \"" + language + "\" was not found! Using the default \"en_US\"");
            }
        }

        if (config == null) {
            throw new ConfigurateException("Failed to load messages configuration: " + path);
        }

        this.config = config;
    }

    private ConfigImpl loadDefault() {
        File defaultLang = new File(platform.getDataFolder(), DEFAULT_LANG);
        if(!defaultLang.exists()) platform.saveResource(DEFAULT_LANG, false);

        return this.configManager.load(defaultLang);
    }

}