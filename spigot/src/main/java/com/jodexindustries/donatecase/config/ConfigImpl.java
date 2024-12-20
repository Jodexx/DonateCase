package com.jodexindustries.donatecase.config;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.config.ConfigBukkit;
import com.jodexindustries.donatecase.api.config.ConfigCasesBukkit;
import com.jodexindustries.donatecase.api.events.DonateCaseReloadEvent;
import com.jodexindustries.donatecase.database.CaseDatabaseImpl;
import com.jodexindustries.donatecase.impl.managers.CaseKeyManagerImpl;
import com.jodexindustries.donatecase.impl.managers.CaseOpenManagerImpl;
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
public class ConfigImpl implements ConfigBukkit {

    private final DonateCase plugin;

    private File fileLang;
    private YamlConfiguration lang;

    private final ConfigCasesImpl casesConfig;

    private final Map<File, YamlConfiguration> configs = new HashMap<>();

    private final Converter converter;

    private static final String[] defaultFiles = {
            "Config.yml",
            "Cases.yml",
            "Animations.yml",
            "lang/en_US.yml",
    };

    /**
     * Default initialization constructor
     *
     * @param plugin Plugin object
     */
    public ConfigImpl(DonateCase plugin) {
        this.plugin = plugin;
        this.converter = new Converter(this);
        this.casesConfig = new ConfigCasesImpl(plugin);
    }

    @Override
    @Nullable
    public YamlConfiguration get(@NotNull File file) {
        return configs.get(file);
    }

    @Override
    @Nullable
    public YamlConfiguration get(@NotNull String name) {
        return configs.get(new File(plugin.getDataFolder(), name));
    }

    @Override
    public void load() {
        createFiles();
        loadConfigurations();

        casesConfig.load();

        checkAndUpdateConfig(getConfig(), "Config.yml", "2.5");
        checkAndUpdateConfig(getAnimations(), "Animations.yml", "1.4");

        converter.convertConfig();
        converter.convertAnimations();

        checkConvertCases();
        checkConvertLocations();

        File langFolder = new File(plugin.getDataFolder(), "lang");
        File[] listFiles = langFolder.listFiles();
        File defaultLangFile = new File(langFolder, "en_US.yml");

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
            CaseOpenManagerImpl.openCache.setMaxAge(caching);
            CaseKeyManagerImpl.keysCache.setMaxAge(caching);
            CaseDatabaseImpl.historyCache.setMaxAge(caching);
        }

        DonateCaseReloadEvent reloadEvent = new DonateCaseReloadEvent(plugin, DonateCaseReloadEvent.Type.CONFIG);
        plugin.getServer().getPluginManager().callEvent(reloadEvent);
    }

    @Override
    public void delete(@NotNull File file) {
        if(file.delete()) configs.remove(file);
    }

    @Override
    public void delete(@NotNull String name) {
        File file = new File(plugin.getDataFolder(), name);
        delete(file);
    }

    @Override
    public boolean save(String name) {
        return save(new File(plugin.getDataFolder(), name));
    }

    @Override
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
     * Save Animations.yml configuration
     */
    public void saveAnimations() {
        save("Animations.yml");
    }

    /**
     * Save lang configuration
     */
    @Override
    public void saveLang() {
        try {
            lang.save(fileLang);
        } catch (IOException ignored) {
            plugin.getLogger().log(Level.WARNING, "Couldn't save " + fileLang.getName());
        }
    }

    private void loadConfigurations() {
        File[] directoryFiles = plugin.getDataFolder().listFiles();
        if(directoryFiles != null) {
            for (File file : directoryFiles) {
                if(!file.isFile()) continue;
                if(file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")) {
                    try {
                        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
                        configs.put(file, yamlConfiguration);
                    } catch (Throwable e) {
                        plugin.getLogger().log(Level.WARNING, "Error with loading configuration : ", e);
                    }
                }
            }
        }
    }

    private void createFiles() {
        for (String fileName : defaultFiles) {
            if (!(new File(plugin.getDataFolder(), fileName)).exists()) plugin.saveResource(fileName, false);
        }
    }

    private void checkAndUpdateConfig(YamlConfiguration config, String fileName, String expectedValue) {
        String version = config.getString("config");
        if (version == null || !version.equals(expectedValue)) {
            Logger.log("&cOutdated " + fileName + "! Creating a new!");
            File configFile = new File(plugin.getDataFolder(), fileName);
            File newFile = new File(plugin.getDataFolder(), fileName + ".old");
            if(configFile.renameTo(newFile)) plugin.saveResource(fileName, false);
        }
    }

    private void checkConvertCases() {
        YamlConfiguration configuration = getConfig();
        File casesDir = new File(plugin.getDataFolder(), "cases");
        if (!casesDir.exists()) casesDir.mkdir();
        if (configuration.getConfigurationSection("DonateCase.Cases") != null) {
            Logger.log("&cOutdated cases format!");
            converter.convertOldCasesFormat();
        }
    }

    private void checkConvertLocations() {
        String version = getCases().getString("config");

        if (version == null || !version.equals("1.0")) {
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
                if (langRu.renameTo(new File(plugin.getDataFolder(), "lang/ru_RU.yml.old")))
                    plugin.saveResource("lang/ru_RU.yml", false);
                File langEn = new File(plugin.getDataFolder(), "lang/en_US.yml");
                if (langEn.renameTo(new File(plugin.getDataFolder(), "lang/en_US.yml.old")))
                    plugin.saveResource("lang/en_US.yml", false);
                File langUa = new File(plugin.getDataFolder(), "lang/ua_UA.yml");
                if (langUa.renameTo(new File(plugin.getDataFolder(), "lang/ua_UA.yml.old")))
                    plugin.saveResource("lang/ua_UA.yml", false);
            }
        }
    }

    /**
     * Get language configuration
     *
     * @return Lang configuration
     */
    @Override
    public YamlConfiguration getLang() {
        return lang;
    }

    /**
     * Get cases config instance
     * Used for loading cases folder
     *
     * @return CasesConfig class
     */
    @Override
    public ConfigCasesBukkit getConfigCases() {
        return casesConfig;
    }

    public Converter getConverter() {
        return converter;
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
