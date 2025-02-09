package com.jodexindustries.donatecase.common.config;

import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.config.ConfigCases;
import com.jodexindustries.donatecase.common.platform.BackendPlatform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for loading cases configurations
 */
public class ConfigCasesImpl implements ConfigCases {

    private final Map<String, Config> cases = new HashMap<>();

    private final BackendPlatform platform;

    public ConfigCasesImpl(ConfigManagerImpl config) {
        this.platform = config.getPlatform();

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

    @NotNull
    private File[] getCasesInFolder() {
        File directory = new File(platform.getDataFolder(), "cases");
        File[] files = directory.listFiles();
        return files != null ? files : new File[0];
    }

    @Override
    public void load() throws ConfigurateException {
        cases.clear();

        if (getCasesInFolder().length == 0) {
            platform.saveResource("cases/case.yml", false);
        }

        for (File file : getCasesInFolder()) {
            String name = getFileNameWithoutExtension(file);
            Config config = new ConfigImpl(file);
            config.load();

            cases.put(name, config);
        }
    }

    @Override
    public @Nullable Config get(@NotNull String name) {
        return cases.get(name);
    }

    @Override
    public @NotNull Map<String, Config> getMap() {
        return cases;
    }
}
