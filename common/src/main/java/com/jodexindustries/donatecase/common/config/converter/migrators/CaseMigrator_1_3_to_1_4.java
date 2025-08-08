package com.jodexindustries.donatecase.common.config.converter.migrators;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.config.converter.ConfigMigrator;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.manager.ConfigManager;
import com.jodexindustries.donatecase.common.config.ConfigImpl;
import com.jodexindustries.donatecase.common.config.converter.DefaultConfigType;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;

public class CaseMigrator_1_3_to_1_4 implements ConfigMigrator {

    @Override
    @Deprecated
    public void migrate(Config config) throws ConfigurateException {
        ConfigManager configManager = DCAPI.getInstance().getConfigManager();

        File file = config.file();

        CaseData caseData = config.node("case").get(CaseData.class);
        if (caseData == null) {
            DCAPI.getInstance().getPlatform().getLogger().warning(
                    "Migration warning: CaseData is null in config file " + file.getName() +
                            ". Migration aborted."
            );
            return;
        }

        CaseDefinition definition = CaseData.toDefinition(caseData);

        File parent = file.getParentFile();

        String type = config.getNameWithoutExtension();

        definition.settings().type(type);

        File caseFolder = new File(parent, type);

        // settings.yml
        File settingsFile = new File(caseFolder, "settings.yml");
        ConfigImpl settings = new ConfigImpl(settingsFile, DefaultConfigType.CASE_SETTINGS);
        settings.load();

        settings.node().set(definition.settings());
        setMeta(settings);
        settings.save();
        configManager.load(settingsFile);

        // items.yml
        File itemsFile = new File(caseFolder, "items.yml");
        ConfigImpl items = new ConfigImpl(itemsFile, DefaultConfigType.CASE_ITEMS);
        items.load();

        items.node("items").set(definition.items());
        setMeta(items);
        items.save();
        configManager.load(itemsFile);

        // menu/default_menu.yml
        File menuFile = new File(caseFolder, "menu/default_menu.yml");
        ConfigImpl menu = new ConfigImpl(menuFile, DefaultConfigType.CASE_MENU);
        menu.load();

        menu.node().set(definition.menus().get(0));
        setMeta(menu);
        menu.save();
        configManager.load(menuFile);

        if (config.delete()) {
            DCAPI.getInstance().getPlatform().getLogger().info("The old case file: " + file.getName() + " was deleted");
        }
    }

    private void setMeta(ConfigImpl config) throws SerializationException {
        ConfigurationNode root = config.node("config");
        root.node("version").set(1);
        root.node("type").set(config.type().getName());
    }
}
