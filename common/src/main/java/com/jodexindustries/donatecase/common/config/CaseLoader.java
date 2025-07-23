package com.jodexindustries.donatecase.common.config;

import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.config.Loadable;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseItems;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMenu;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseSettings;
import com.jodexindustries.donatecase.api.event.plugin.DonateCaseReloadEvent;
import com.jodexindustries.donatecase.common.DonateCase;
import com.jodexindustries.donatecase.common.config.converter.DefaultConfigType;
import com.jodexindustries.donatecase.common.managers.CaseManagerImpl;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class CaseLoader implements Loadable {

    private static final String CASES_FOLDER = "cases";
    private static final String DEFAULT_NAME = "default";
    private static final List<String> DEFAULT_CASE = Arrays.asList(
            "cases/" + DEFAULT_NAME + "/settings.yml",
            "cases/" + DEFAULT_NAME + "/items.yml",
            "cases/" + DEFAULT_NAME + "/menu/" + DEFAULT_NAME + "_menu.yml"
    );

    private final DonateCase api;

    public CaseLoader(DonateCase api) {
        this.api = api;
    }

    @Override
    public void load() {
        CaseManagerImpl caseManager = api.getCaseManager();

        caseManager.caseDefinitionMap.clear();

        for (Map.Entry<String, List<Config>> entry : getCases().entrySet()) {
            String caseFolder = entry.getKey();

            CaseSettings settings = null;
            CaseItems items = null;

            List<CaseMenu> menus = new ArrayList<>();

            for (Config config : entry.getValue()) {
                DefaultConfigType type = (DefaultConfigType) config.type();

                if (type.isUnknown()) continue;

                switch (type) {
                    case CASE_SETTINGS: {
                        if (settings != null) break;

                        settings = config.getSerialized(CaseSettings.class);
                        break;
                    }

                    case CASE_ITEMS: {
                        if (items != null) break;

                        items = config.getSerialized(CaseItems.class);
                        break;
                    }

                    case CASE_MENU: {
                        CaseMenu menu = config.getSerialized(CaseMenu.class);
                        if (menu == null) break;

                        if (menu.id() == null) {
                            String id = config.getNameWithoutExtension();
                            menu.id(id);
                            info(caseFolder, "Case menu id is undefined! Using file name: " + id);
                        }

                        menus.add(menu);
                        break;
                    }
                }
            }

            if (settings == null) {
                warn(caseFolder, "Case settings not found! Create a new config file with type 'case_settings'.");
                continue;
            }

            if (items == null) {
                items = new CaseItems();
            }

            if (menus.isEmpty()) {
                warn(caseFolder, "Case menus list is empty! Create a new config file with type 'case_menu'.");
                continue;
            }

            if (settings.type() == null || settings.type().isEmpty()) {
                info(caseFolder, "Case type is undefined. Using folder name: '" + caseFolder + "'.");
                settings.type(caseFolder);
            }

            if (caseManager.hasByType(settings.type())) {
                warn(caseFolder, "Duplicate case detected! Case type '" + settings.type() + "' already exists. Skipping.");
                continue;
            }

            CaseMenu defaultMenu = null;

            if (settings.defaultMenu() == null || settings.defaultMenu().isEmpty()) {
                defaultMenu = menus.get(0);
                String first = defaultMenu.id();
                info(caseFolder, "Case default menu is undefined. Using the first available: '" + first + "'.");
                settings.defaultMenu(first);
            }

            // update meta for all materials
            items.updateItemsMeta(api.getPlatform().getMetaUpdater());

            CaseDefinition definition = new CaseDefinition(settings, items, menus, defaultMenu);

            caseManager.caseDefinitionMap.put(settings.type(), definition);
        }

        api.getEventBus().post(new DonateCaseReloadEvent(DonateCaseReloadEvent.Type.CASES));
        api.getPlatform().getLogger().info("Loaded " + caseManager.caseDefinitionMap.size() + " cases!");
    }

    private void info(String caseFolder, String message) {
        log(Level.INFO, caseFolder, message);
    }

    private void warn(String caseFolder, String message) {
        log(Level.WARNING, caseFolder, message);
    }

    private void log(Level level, String caseFolder, String message) {
        api.getPlatform().getLogger().log(level, "[CaseLoader] [" + caseFolder + "] " + message);
    }

    private Map<String, List<Config>> getCases() {
        Map<String, List<Config>> cases = new HashMap<>();

        for (Config config : api.getConfigManager().get().values()) {
            if (!(config.type() instanceof DefaultConfigType)) continue;

            String path = config.path();

            // plugins/DonateCase/cases/case_type/another_files.yml
            String[] parts = path.split("/");

            if (parts.length >= 4) {
                if (!parts[2].equals(CASES_FOLDER)) continue;

                String caseFolder = parts[3];

                if (caseFolder.equals(config.file().getName())) continue;

                cases.computeIfAbsent(caseFolder, k -> new ArrayList<>()).add(config);
            }
        }

        if(cases.isEmpty()) cases.put(DEFAULT_NAME, saveDefault());

        return cases;
    }

    private List<Config> saveDefault() {
        File parent = api.getPlatform().getDataFolder();

        List<Config> list = new ArrayList<>();

        for (String file : DEFAULT_CASE) {
            api.getPlatform().saveResource(file, false);
            list.add(api.getConfigManager().load(new File(parent, file)));
        }

        return list;
    }

}