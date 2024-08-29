package com.jodexindustries.donatecase.config;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.database.yaml.YamlData;
import com.jodexindustries.donatecase.tools.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Class for load all configuration files
 */
public class Config {

    private final DonateCase plugin;

    private File fileLang;
    private final YamlConfiguration lang;

    private final CasesConfig casesConfig;
    private final YamlData data;

    private final Map<File, YamlConfiguration> configs = new HashMap<>();

    private final Converter converter;

    /**
     * Default initialization constructor
     *
     * @param plugin Plugin object
     */
    public Config(DonateCase plugin) {
        this.plugin = plugin;
        this.converter = new Converter(this);

        String[] files = {
                "Config.yml",
                "Cases.yml",
                "Keys.yml",
                "Animations.yml",
                "Data.yml",
        };


        for (String file : files) {
            checkAndCreateFile(file);
        }

        File fileAnimations = new File(plugin.getDataFolder(), "Animations.yml");
        YamlConfiguration animations = YamlConfiguration.loadConfiguration(fileAnimations);
        configs.put(fileAnimations, animations);

        File fileCases = new File(plugin.getDataFolder(), "Cases.yml");
        YamlConfiguration cases = YamlConfiguration.loadConfiguration(fileCases);
        configs.put(fileCases, cases);

        File fileKeys = new File(plugin.getDataFolder(), "Keys.yml");
        YamlConfiguration keys = YamlConfiguration.loadConfiguration(fileKeys);
        configs.put(fileKeys, keys);

        File fileConfig = new File(plugin.getDataFolder(), "Config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(fileConfig);
        configs.put(fileConfig, config);

        checkAndUpdateConfig(config, "Config.yml", "2.5");
        checkAndUpdateConfig(animations, "Animations.yml", "1.3");

        converter.convertConfig();

        checkConvertCases();

        casesConfig = new CasesConfig(plugin);

        checkConvertLocations();

        data = new YamlData();

        File langFolder = new File(plugin.getDataFolder(), "lang");
        File[] listFiles = langFolder.listFiles();
        File defaultLangFile = new File(langFolder, "en_US.yml");
        checkAndCreateFile("lang/en_US.yml");

        String configLang = getConfig().getString("DonateCase.Languages", "en_US");

        if (listFiles == null || listFiles.length == 0) {
            fileLang = defaultLangFile;
            plugin.getLogger().info("Lang folder is empty! Using default en_US language");
        } else {
            for (File file : listFiles) {
                String fileName = file.getName().toLowerCase();

                if (fileName.equalsIgnoreCase(configLang + ".yml") ||
                        fileName.split("_")[0].equalsIgnoreCase(configLang)) {
                    fileLang = file;
                    break;
                }
            }

            if (fileLang == null) {
                String langFilePath = "lang/" + configLang + ".yml";
                if (plugin.getResource(langFilePath) != null) plugin.saveResource(langFilePath, false);
                fileLang = new File(plugin.getDataFolder(), langFilePath);

                if (!fileLang.exists()) {
                    fileLang = defaultLangFile;
                    plugin.getLogger().warning("Lang file: " + configLang + " not found! Using default en_US language");
                }
            }
        }

        this.lang = YamlConfiguration.loadConfiguration(fileLang);

        checkLanguageVersion();

        // Convert after language file loaded
        converter.convertOverall();

        long caching = getConfig().getLong("DonateCase.Caching");
        if (caching >= 0) {
            Case.keysCache.setMaxAge(caching);
            Case.openCache.setMaxAge(caching);
            Case.historyCache.setMaxAge(caching);
        }
    }

    @Nullable
    public YamlConfiguration get(@NotNull File file) {
        return configs.get(file);
    }

    @Nullable
    public YamlConfiguration get(@NotNull String name) {
        return configs.get(new File(plugin.getDataFolder(), name));
    }

    public boolean save(String name) {
        return save(new File(plugin.getDataFolder(), name));
    }

    public boolean save(File file) {
        String name = file.getName();
        YamlConfiguration configuration = configs.get(file);
        if (configuration == null) return false;

        try {
            configuration.save(file);
            return true;
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Couldn't save " + name);
        }
        return false;
    }

    /**
     * Save Cases.yml configuration
     */
    public void saveCases() {
        save("Cases.yml");
    }

    /**
     * Save Config.yml configuration
     */
    public void saveConfig() {
        save("Config.yml");
    }

    /**
     * Save Keys.yml configuration
     */
    public void saveKeys() {
        save("Keys.yml");
    }

    /**
     * Save Animations.yml configuration
     */
    public void saveAnimations() {
        save("Animations.yml");
    }

    /**
     * Save lang configuration
     */
    public void saveLang() {
        try {
            lang.save(fileLang);
        } catch (IOException ignored) {
            plugin.getLogger().log(Level.WARNING, "Couldn't save " + fileLang.getName());
        }
    }

    private void checkAndCreateFile(String fileName) {
        if (!(new File(plugin.getDataFolder(), fileName)).exists()) plugin.saveResource(fileName, false);
    }

    private void checkAndUpdateConfig(YamlConfiguration config, String fileName, String expectedValue) {
        if (config.getString("config") == null || !config.getString("config", "").equals(expectedValue)) {
            Logger.log("&cOutdated " + fileName + "! Creating a new!");
            File configFile = new File(plugin.getDataFolder(), fileName);
            configFile.renameTo(new File(plugin.getDataFolder(), fileName + ".old"));
            plugin.saveResource(fileName, false);
        }
    }

    private void checkConvertCases() {
        YamlConfiguration configuration = getConfig();
        if (configuration.getConfigurationSection("DonateCase.Cases") != null) {
            new File(plugin.getDataFolder(), "cases").mkdir();
            Logger.log("&cOutdated cases format!");
            converter.convertOldCasesFormat();
        } else {
            if (!new File(plugin.getDataFolder(), "cases").exists()) {
                new File(plugin.getDataFolder(), "cases").mkdir();
            }
        }
    }

    private void checkConvertLocations() {
        YamlConfiguration configuration = getCases();
        if (configuration.getString("config") == null ||
                !configuration.getString("config", "").equalsIgnoreCase("1.0")) {
            Logger.log("Conversion of case locations to a new method of storage...");
            converter.convertCasesLocation();
        }
    }

    private void checkLanguageVersion() {
        String version = lang.getString("config");
        if (version == null || !version.equalsIgnoreCase("2.6")) {
            Logger.log("&cOutdated language config! Creating a new!");
            if (version != null && version.equalsIgnoreCase("2.5")) {
                converter.convertLanguage();
            } else {
                File langRu = new File(plugin.getDataFolder(), "lang/ru_RU.yml");
                langRu.renameTo(new File(plugin.getDataFolder(), "lang/ru_RU.yml.old"));
                plugin.saveResource("lang/ru_RU.yml", false);
                File langEn = new File(plugin.getDataFolder(), "lang/en_US.yml");
                langEn.renameTo(new File(plugin.getDataFolder(), "lang/en_US.yml.old"));
                plugin.saveResource("lang/en_US.yml", false);
                File langUa = new File(plugin.getDataFolder(), "lang/ua_UA.yml");
                langUa.renameTo(new File(plugin.getDataFolder(), "lang/ua_UA.yml.old"));
                plugin.saveResource("lang/ua_UA.yml", false);
            }
        }
    }


    /**
     * Get Cases.yml configuration
     *
     * @return Configuration
     */
    public YamlConfiguration getCases() {
        return get("Cases.yml");
    }

    /**
     * Get Keys.yml configuration
     *
     * @return Configuration
     */
    public YamlConfiguration getKeys() {
        return get("Keys.yml");
    }

    /**
     * Get Config.yml configuration
     *
     * @return Configuration
     */
    public YamlConfiguration getConfig() {
        return get("Config.yml");
    }

    /**
     * Get Animations.yml configuration
     *
     * @return Configuration
     */
    public YamlConfiguration getAnimations() {
        return get("Animations.yml");
    }

    /**
     * Get language configuration
     *
     * @return Lang configuration
     */
    public YamlConfiguration getLang() {
        return lang;
    }

    /**
     * Get cases config instance
     * Used for loading cases folder
     *
     * @return CasesConfig class
     */
    public CasesConfig getCasesConfig() {
        return casesConfig;
    }

    /**
     * Used for data storing
     *
     * @return Data.yml config instance
     */
    public YamlData getData() {
        return data;
    }

    /**
     * Gets DonateCase instance
     *
     * @return DonateCase instance
     */
    public DonateCase getPlugin() {
        return plugin;
    }
}
