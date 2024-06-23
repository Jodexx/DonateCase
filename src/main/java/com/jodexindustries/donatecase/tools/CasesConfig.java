package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.api.Case;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for loading cases configurations
 */
public class CasesConfig {
    private final Map<String, YamlConfiguration> cases = new HashMap<>();

    /**
     * Default initialization constructor
     */
    public CasesConfig() {
        for (File file : Tools.getCasesInFolder()) {
            if(file.getName().contains(".yml")) {
                String name = file.getName().split(".yml")[0];
                int index = file.getName().length();
                if(file.getName().substring(index - 3, index).equalsIgnoreCase("yml")) {
                    YamlConfiguration caseConfig = YamlConfiguration.loadConfiguration(file);
                    if(caseConfig.getConfigurationSection("case") != null) {
                        boolean isOld = false;
                        ConfigurationSection items = caseConfig.getConfigurationSection("case.Items");
                        if(items == null) {
                            Case.getInstance().getLogger().warning("Case " + name + " has a broken case.Items section, skipped.");
                            continue;
                        }
                        for (String item : items.getKeys(false)) {
                            List<String> actions = new ArrayList<>();
                            String giveCommand = caseConfig.getString("case.Items." + item + ".GiveCommand");
                            List<String> giveCommands = caseConfig.getStringList("case.Items." + item + ".Commands");
                            ConfigurationSection giveSection = caseConfig.getConfigurationSection("case.Items." + item + ".GiveCommands");
                            if (giveCommand != null || !giveCommands.isEmpty() || giveSection != null) {
                                if (!isOld) {
                                    isOld = true;
                                    Case.getInstance().getLogger().warning("Case " + name + " outdated! Converting...");
                                }
                                String title = caseConfig.getString("case.Items." + item + ".Title");
                                String subTitle = caseConfig.getString("case.Items." + item + ".SubTitle");
                                List<String> broadcast = caseConfig.getStringList("case.Items." + item + ".Broadcast");
                                actions.add("[command] " + giveCommand);
                                for (String command : giveCommands) {
                                    actions.add("[command] " + command);
                                }
                                actions.add("[title] " + title + ";" + subTitle);
                                for (String line : broadcast) {
                                    actions.add("[broadcast] " + line);
                                }
                                if (giveSection != null) {
                                    for (String choice : giveSection.getKeys(false)) {
                                        int chance = caseConfig.getInt("case.Items." + item + ".GiveCommands." + choice + ".Chance");
                                        List<String> choiceActions = new ArrayList<>();
                                        List<String> choiceCommands = caseConfig.getStringList("case.Items." + item + ".GiveCommands." + choice + ".Commands");
                                        for (String choiceCommand : choiceCommands) {
                                            choiceActions.add("[command] " + choiceCommand);
                                        }
                                        List<String> choiceBroadcasts = caseConfig.getStringList("case.Items." + item + ".GiveCommands." + choice + ".Broadcast");
                                        for (String choiceBroadcast : choiceBroadcasts) {
                                            choiceActions.add("[broadcast] " + choiceBroadcast);
                                        }
                                        caseConfig.set("case.Items." + item + ".RandomActions." + choice + ".Chance", chance);
                                        caseConfig.set("case.Items." + item + ".RandomActions." + choice + ".Actions", choiceActions);
                                    }
                                }
                                caseConfig.set("case.Items." + item + ".Title", null);
                                caseConfig.set("case.Items." + item + ".SubTitle", null);
                                caseConfig.set("case.Items." + item + ".Commands", null);
                                caseConfig.set("case.Items." + item + ".GiveCommand", null);
                                caseConfig.set("case.Items." + item + ".Broadcast", null);
                                caseConfig.set("case.Items." + item + ".GiveCommands", null);
                                caseConfig.set("case.Items." + item + ".Actions", actions);
                                try {
                                    caseConfig.save(file);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        cases.put(name, caseConfig);
                    } else {
                        Case.getInstance().getLogger().warning("Case " + name + " has a broken case section, skipped.");
                    }
                }
            }
        }
    }

    /**
     * Get all cases configurations
     * @return map of configuration
     */
    public Map<String, YamlConfiguration> getCases() {
        return cases;
    }

    /**
     * Get case configuration
     * @param name Case type (file name without .yml)
     * @return case configuration
     */
     public YamlConfiguration getCase(String name) {
        return cases.get(name);
     }
}
