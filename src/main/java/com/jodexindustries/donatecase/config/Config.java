package com.jodexindustries.donatecase.config;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.database.yaml.YamlData;
import com.jodexindustries.donatecase.tools.Logger;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Class for load all configuration files
 */
public class Config {

    private final DonateCase plugin;

    private final File fileCases;
    private final File fileKeys;
    private final File fileConfig;
    private final File fileAnimations;
    private File fileLang;
    private final YamlConfiguration lang;
    private final YamlConfiguration cases;
    private final YamlConfiguration keys;
    private final YamlConfiguration config;
    private final YamlConfiguration animations;

    private final CasesConfig casesConfig;
    private final YamlData data;

    private final Converter converter;

    /**
     * Default initialization constructor
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
                "lang/ru_RU.yml",
                "lang/en_US.yml",
                "lang/ua_UA.yml"
        };

        for (String file : files) {
            checkAndCreateFile(file);
        }

        fileAnimations = new File(Case.getInstance().getDataFolder(), "Animations.yml");
        animations = YamlConfiguration.loadConfiguration(fileAnimations);
        fileCases = new File(Case.getInstance().getDataFolder(), "Cases.yml");
        cases = YamlConfiguration.loadConfiguration(fileCases);
        fileKeys = new File(Case.getInstance().getDataFolder(), "Keys.yml");
        keys = YamlConfiguration.loadConfiguration(fileKeys);
        fileConfig = new File(Case.getInstance().getDataFolder(), "Config.yml");
        config = YamlConfiguration.loadConfiguration(fileConfig);

        config.addDefault("DonatCase.NoKeyWarningSound", "ENTITY_ENDERMAN_TELEPORT");

        checkAndUpdateConfig(config, "Config.yml", "2.5");
        checkAndUpdateConfig(animations, "Animations.yml", "1.3");

        checkConvertCases();
        casesConfig = new CasesConfig(plugin);
        converter.convertBASE64();

        checkConvertLocations();

        data = new YamlData();

        File path = new File(Case.getInstance().getDataFolder(), "lang");
        File[] listFiles = path.listFiles();

        File defaultLang = new File(path, "en_US.yml");

        if(listFiles == null) {
            fileLang = defaultLang;
            lang = YamlConfiguration.loadConfiguration(defaultLang);
            return;
        }

        String lang = getConfig().getString("DonatCase.Languages");

        for (File file : listFiles) {
            if (file.getName().toLowerCase().split("_")[0].equalsIgnoreCase(lang)) {
                this.fileLang = file;
                break;
            }
        }

        this.lang = YamlConfiguration.loadConfiguration(fileLang);
        checkLanguageVersion();

        long caching = getConfig().getLong("DonatCase.Caching");
        if(caching > 0) {
            Case.keysCache.setMaxAge(caching);
            Case.openCache.setMaxAge(caching);
        }
    }

    /**
     * Save Cases.yml configuration
     */
    public void saveCases() {
        try {
            cases.save(fileCases);
        } catch (IOException var1) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save Cases.yml");
        }

    }

    /**
     * Save Config.yml configuration
     */
    public void saveConfig() {
        try {
            config.save(fileConfig);
        } catch (IOException var1) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save Config.yml");
        }
    }

    /**
     * Save Keys.yml configuration
     */
    public void saveKeys() {
        try {
            keys.save(fileKeys);
        } catch (IOException var1) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save Keys.yml");
        }
    }

    /**
     * Save Animations.yml configuration
     */
    public void saveAnimations() {
        try {
            animations.save(fileAnimations);
        } catch (IOException var1) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save Animations.yml");
        }

    }

    /**
     * Save lang configuration
     */
    public void saveLang() {
        try {
            lang.save(fileLang);
        } catch (IOException ignored) {
            Case.getInstance().getLogger().log(Level.WARNING, "Couldn't save " + fileLang.getName());
        }
    }

    private void checkAndCreateFile(String fileName) {
        if (!(new File(plugin.getDataFolder(), fileName)).exists()) {
            plugin.saveResource(fileName, false);
        }
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
        if(config.getConfigurationSection("DonatCase.Cases") != null) {
            new File(plugin.getDataFolder(), "cases").mkdir();
            Logger.log("&cOutdated cases format!");
            converter.convertCases();
        } else {
            if(!new File(plugin.getDataFolder(), "cases").exists()) {
                new File(plugin.getDataFolder(), "cases").mkdir();
            }
        }
    }

    private void checkConvertLocations() {
        if(cases.getString("config") == null || !cases.getString("config", "").equalsIgnoreCase("1.0")) {
            Logger.log("Conversion of case locations to a new method of storage...");
            converter.convertCasesLocation();
        }
    }

    private void checkLanguageVersion() {
        String version = lang.getString("config");
        if (version == null || !version.equalsIgnoreCase("2.6")) {
            Logger.log("&cOutdated language config! Creating a new!");
            if(version != null && version.equalsIgnoreCase("2.5")) {
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
     * @return Configuration
     */
    public YamlConfiguration getCases() {
        if(cases == null) {
            Case.getInstance().loadConfig();
        }
        return cases;
    }

    /**
     * Get Keys.yml configuration
     * @return Configuration
     */
    public YamlConfiguration getKeys() {
        if(keys == null) {
            Case.getInstance().loadConfig();
        }
        return keys;
    }

    /**
     * Get Config.yml configuration
     * @return Configuration
     */
    public YamlConfiguration getConfig() {
        if(config == null) {
            Case.getInstance().loadConfig();
        }
        return config;
    }

    /**
     * Get Animations.yml configuration
     * @return Configuration
     */
    public YamlConfiguration getAnimations() {
        if(animations == null) {
            Case.getInstance().loadConfig();
        }
        return animations;
    }

    /**
     * Get language configuration
     * @return Lang configuration
     */
    public YamlConfiguration getLang() {
        return lang;
    }

    /**
     * Get cases config instance
     * Used for loading cases folder
     * @return CasesConfig class
     */
    public CasesConfig getCasesConfig() {
        return casesConfig;
    }

    /**
     * Used for data storing
     * @return Data.yml config instance
     */
    public YamlData getData() {
        return data;
    }

    public DonateCase getPlugin() {
        return plugin;
    }
}
