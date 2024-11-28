package com.jodexindustries.donatecase.config;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.config.ConfigCasesBukkit;
import com.jodexindustries.donatecase.api.tools.Pair;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for loading cases configurations
 */
public class ConfigCasesImpl implements ConfigCasesBukkit {
    private final Map<String, Pair<File, YamlConfiguration>> cases = new HashMap<>();
    private final Plugin plugin;

    /**
     * Default initialization constructor
     *
     * @param plugin Plugin object
     */
    public ConfigCasesImpl(Plugin plugin) {
        this.plugin = plugin;
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
    private static List<File> getCasesInFolder() {
        List<File> files = new ArrayList<>();
        File directory = new File(Case.getInstance().getDataFolder(), "cases");
        File[] array = directory.listFiles();
        if (array != null) Collections.addAll(files, array);
        return files;
    }

    /**
     * Get all cases configurations
     *
     * @return map of configuration
     */
    @Override
    public Map<String, Pair<File, YamlConfiguration>> getCases() {
        return cases;
    }

    /**
     * Get case configuration
     *
     * @param name Case type (file name without .yml)
     * @return case configuration
     */
    @Override
    public Pair<File, YamlConfiguration> getCase(String name) {
        return cases.get(name);
    }

    @Override
    public void load() {
        if (getCasesInFolder().isEmpty())
            plugin.saveResource("cases/case.yml", false);

        for (File file : getCasesInFolder()) {
            if (isYamlFile(file)) {
                String name = getFileNameWithoutExtension(file);
                YamlConfiguration caseConfig = YamlConfiguration.loadConfiguration(file);
                if (caseConfig.getConfigurationSection("case") != null) {
                    cases.put(name, new Pair<>(file, caseConfig));
                } else {
                    Case.getInstance().getLogger().warning("Case " + name + " has a broken case section, skipped.");
                }
            }
        }
    }
}
