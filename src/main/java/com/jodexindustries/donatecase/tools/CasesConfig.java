package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.dc.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
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
