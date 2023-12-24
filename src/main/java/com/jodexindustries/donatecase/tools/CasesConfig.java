package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.dc.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.awt.font.FontRenderContext;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CasesConfig {
    public Map<String, YamlConfiguration> cases = new HashMap<>();
    public CasesConfig() {
        for (File file : Main.t.getCasesInFolder()) {
            if(file.getName().contains(".yml")) {
                String name = file.getName().split(".yml")[0];
                int index = file.getName().length();
                if(file.getName().substring(index - 3, index).equalsIgnoreCase("yml")) {
                    YamlConfiguration caseConfig = YamlConfiguration.loadConfiguration(file);
                    if(caseConfig.getConfigurationSection("case") != null) {
                        boolean isOld = false;
                        for (String item : caseConfig.getConfigurationSection("case.Items").getKeys(false)) {
                            List<String> actions = new ArrayList<>();
                            String giveCommand = caseConfig.getString("case.Items." + item + ".GiveCommand");
                            List<String> giveCommands = caseConfig.getStringList("case.Items." + item + ".Commands");
                            if (giveCommand != null || !giveCommands.isEmpty()) {
                                if (!isOld) {
                                    isOld = true;
                                    Main.instance.getLogger().warning("Case " + name + " outdated! Converting...");
                                }
                                String title = caseConfig.getString("case.Items." + item + ".Title");
                                String subTitle = caseConfig.getString("case.Items." + item + ".SubTitle");
                                List<String> broadcast = caseConfig.getStringList("case.Items." + item + ".Broadcast");
                                ConfigurationSection giveSection = caseConfig.getConfigurationSection("case.Items." + item + ".GiveCommands");
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
                    }
                }
            }
        }
    }

    public Map<String, YamlConfiguration> getCases() {
        return cases;
    }
     public YamlConfiguration getCase(String name) {
        return cases.get(name);
     }
}
