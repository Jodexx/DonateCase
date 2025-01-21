package com.jodexindustries.donatecase.config;

import com.jodexindustries.donatecase.api.config.Messages;
import com.jodexindustries.donatecase.platform.BackendPlatform;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MessagesImpl implements Messages {
    private final BackendPlatform platform;
    private final File folder;

    private ConfigurationNode node;

    public MessagesImpl(BackendPlatform platform) {
        this.platform = platform;
        this.folder = new File(platform.getDataFolder(), "lang");
    }

    @Override
    public @NotNull String getString(@NotNull String path) {
        String value = node.node(path).getString();
        return value == null ? path : value;
    }

    @Override
    public List<String> getStringList(@NotNull String path) {
        try {
            return node.node(path).getList(String.class, new ArrayList<>());
        } catch (SerializationException e) {
            return new ArrayList<>();
        }
    }

    public void load(@NotNull String language) throws ConfigurateException {
        String langFilePath = "lang/" + language + ".yml";
        if (platform.getResource(langFilePath) != null) platform.saveResource(langFilePath, false);

        File file = selectFile(language);

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder().file(file).build();
        this.node = loader.load();
    }

    private File selectFile(String language) {
        File[] listFiles = folder.listFiles();

        File languageFile = new File(folder, "en_US.yml");

        if (listFiles == null) return languageFile;

        for (File file : listFiles) {
            String fileName = file.getName().toLowerCase();
            if (fileName.equals(language + ".yml") || fileName.split("_")[0].equals(language)) {
                languageFile = file;
                break;
            }
        }

        return languageFile;
    }
}