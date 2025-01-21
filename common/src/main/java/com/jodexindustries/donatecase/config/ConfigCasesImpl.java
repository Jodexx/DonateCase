package com.jodexindustries.donatecase.config;

import com.jodexindustries.donatecase.api.config.ConfigCases;
import com.jodexindustries.donatecase.platform.BackendPlatform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for loading cases configurations
 */
public class ConfigCasesImpl implements ConfigCases {
    private final Map<String, ConfigurationNode> cases = new HashMap<>();
    private final BackendPlatform platform;

    public ConfigCasesImpl(BackendPlatform platform) {
        this.platform = platform;
    }

    /**
     * Check is file with .yml format
     *
     * @param file File for checking
     * @return result
     */
    private static boolean isYamlFile(File file) {
        return file.getName().endsWith(".yml") || file.getName().endsWith(".yaml");
    }

    /**
     * Get file name without file format
     *
     * @param file File for checking
     * @return File name without format
     */
    private static String getFileNameWithoutExtension(File file) {
        String fileName = file.getName();
        return fileName.lastIndexOf(".") == -1 ? fileName : fileName.substring(0, fileName.lastIndexOf("."));
    }

    /**
     * Get list of files in cases folder
     *
     * @return list of files
     */
    private List<File> getCasesInFolder() {
        List<File> files = new ArrayList<>();
        File directory = new File(platform.getDataFolder(), "cases");
        File[] array = directory.listFiles();
        if (array != null) Collections.addAll(files, array);
        return files;
    }

    @Override
    public void load() throws ConfigurateException {
        if (getCasesInFolder().isEmpty()) platform.saveResource("cases/case.yml", false);

        for (File file : getCasesInFolder()) {
            if (isYamlFile(file)) {
                String name = getFileNameWithoutExtension(file);
                YamlConfigurationLoader loader = YamlConfigurationLoader.builder().file(file).build();

                ConfigurationNode node = loader.load();

                if (node.hasChild("case")) {
                    cases.put(name, node);
                } else {
                    platform.getLogger().warning("Case " + name + " has a broken case section, skipped.");
                }
            }
        }
    }

    @Override
    public @Nullable ConfigurationNode getCase(@NotNull String name) {
        return cases.get(name);
    }

    @Override
    public @NotNull Map<String, ConfigurationNode> getMap() {
        return cases;
    }
}
