package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.CaseData;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaseLoader {
   private final DonateCase plugin;
    public CaseLoader(DonateCase plugin) {
        this.plugin = plugin;
    }

    public void load() {
        Case.caseData.clear();
        int count = 0;

        for (String caseType : plugin.casesConfig.getCases().keySet()) {
            YamlConfiguration config = plugin.casesConfig.getCase(caseType);
            ConfigurationSection caseSection = config.getConfigurationSection("case");

            if (caseSection == null) {
                logWarning("Case " + caseType + " has a broken case section, skipped.");
                continue;
            }

            CaseData caseData = loadCaseData(caseType, caseSection);
            Case.caseData.put(caseType, caseData);
            count++;
        }

        Logger.log("&aLoaded &c" + count + "&a cases!");
    }

    private CaseData loadCaseData(String caseType, ConfigurationSection caseSection) {
        String caseTitle = getStringOrDefault(caseSection, "Title");
        String caseDisplayName = getStringOrDefault(caseSection, "DisplayName");
        String animationName = caseSection.getString("Animation");

        CaseData.AnimationSound sound = loadAnimationSound(caseSection);
        CaseData.Hologram hologram = loadHologram(caseSection);
        Map<String, CaseData.Item> items = loadItems(caseType, caseSection);
        CaseData.HistoryData[] historyData = loadHistoryData(caseType);
        Map<String, Integer> levelGroups = loadLevelGroups(caseSection);

        return new CaseData(caseType, caseDisplayName, caseTitle, animationName, sound, items, historyData, hologram, levelGroups);
    }

    private String getStringOrDefault(ConfigurationSection section, String path) {
        String value = section.getString(path);
        return value == null ? "" : Tools.rc(value);
    }

    private CaseData.AnimationSound loadAnimationSound(ConfigurationSection caseSection) {
        String animationSound = caseSection.getString("AnimationSound", "NULL").toUpperCase();
        float volume = (float) caseSection.getDouble("Sound.Volume");
        float pitch = (float) caseSection.getDouble("Sound.Pitch");

        Sound bukkitSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        try {
            bukkitSound = Sound.valueOf(animationSound);
        } catch (IllegalArgumentException ignored) {}

        return new CaseData.AnimationSound(bukkitSound, volume, pitch);
    }

    private CaseData.Hologram loadHologram(ConfigurationSection caseSection) {
        boolean hologramEnabled = caseSection.getBoolean("Hologram.Toggle");
        double hologramHeight = caseSection.getDouble("Hologram.Height");
        int range = caseSection.getInt("Hologram.Range");
        List<String> hologramMessage = caseSection.getStringList("Hologram.Message");

        return hologramEnabled
                ? new CaseData.Hologram(true, hologramHeight, range, hologramMessage)
                : new CaseData.Hologram();
    }

    private Map<String, CaseData.Item> loadItems(String caseType, ConfigurationSection caseSection) {
        Map<String, CaseData.Item> items = new HashMap<>();
        ConfigurationSection itemsSection = caseSection.getConfigurationSection("Items");

        if (itemsSection != null) {
            for (String item : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(item);

                if (itemSection == null) {
                    logWarning("Case " + caseType + " has a broken item " + item + " section, skipped.");
                    continue;
                }

                CaseData.Item caseItem = loadItem(item, itemSection);
                items.put(item, caseItem);
            }
        } else {
            logWarning("Case " + caseType + " has a broken case.Items section");
        }

        return items;
    }

    private CaseData.Item loadItem(String item, ConfigurationSection itemSection) {
        String group = itemSection.getString("Group", "");
        int chance = itemSection.getInt("Chance");
        String giveType = itemSection.getString("GiveType", "ONE");
        List<String> actions = itemSection.getStringList("Actions");
        List<String> alternativeActions = itemSection.getStringList("AlternativeActions");
        Map<String, CaseData.Item.RandomAction> randomActions = loadRandomActions(itemSection);
        String[] rgb = loadRgb(itemSection);
        CaseData.Item.Material material = loadMaterial(itemSection);

        return new CaseData.Item(item, group, chance, material, giveType, actions, randomActions, rgb, alternativeActions);
    }

    private Map<String, CaseData.Item.RandomAction> loadRandomActions(ConfigurationSection itemSection) {
        Map<String, CaseData.Item.RandomAction> randomActions = new HashMap<>();
        ConfigurationSection randomActionsSection = itemSection.getConfigurationSection("RandomActions");

        if (randomActionsSection != null) {
            for (String randomAction : randomActionsSection.getKeys(false)) {
                ConfigurationSection randomActionSection = randomActionsSection.getConfigurationSection(randomAction);

                if (randomActionSection != null) {
                    int actionChance = randomActionSection.getInt("Chance");
                    List<String> randomActionsList = randomActionSection.getStringList("Actions");
                    String displayName = randomActionSection.getString("DisplayName");

                    CaseData.Item.RandomAction randomActionObject = new CaseData.Item.RandomAction(actionChance, randomActionsList, displayName);
                    randomActions.put(randomAction, randomActionObject);
                }
            }
        }

        return randomActions;
    }

    private String[] loadRgb(ConfigurationSection itemSection) {
        String rgbString = itemSection.getString("Item.Rgb");
        return rgbString == null ? null : rgbString.replaceAll(" ", "").split(",");
    }

    private CaseData.Item.Material loadMaterial(ConfigurationSection itemSection) {
        String id = itemSection.getString("Item.ID", "STONE");
        String itemDisplayName = Tools.rc(itemSection.getString("Item.DisplayName"));
        boolean enchanted = itemSection.getBoolean("Item.Enchanted");
        ItemStack itemStack = Tools.getCaseItem(itemDisplayName, id, enchanted, loadRgb(itemSection));

        return new CaseData.Item.Material(id, itemStack, itemDisplayName, enchanted);
    }

    private CaseData.HistoryData[] loadHistoryData(String caseType) {
        CaseData.HistoryData[] historyData = new CaseData.HistoryData[10];

        if (!plugin.sql) {
            ConfigurationSection dataSection = plugin.customConfig.getData().getConfigurationSection("Data");

            if (dataSection != null) {
                ConfigurationSection section = dataSection.getConfigurationSection(caseType);

                if (section != null) {
                    for (String i : section.getKeys(false)) {
                        ConfigurationSection caseDataSection = section.getConfigurationSection(i);

                        if (caseDataSection != null) {
                            CaseData.HistoryData data = new CaseData.HistoryData(
                                    caseDataSection.getString("Item"),
                                    caseType,
                                    caseDataSection.getString("Player"),
                                    caseDataSection.getLong("Time"),
                                    caseDataSection.getString("Group"),
                                    caseDataSection.getString("Action"));

                            historyData[Integer.parseInt(i)] = data;
                        }
                    }
                }
            }
        }

        return historyData;
    }

    private Map<String, Integer> loadLevelGroups(ConfigurationSection caseSection) {
        Map<String, Integer> levelGroups = new HashMap<>();
        ConfigurationSection lgSection = caseSection.getConfigurationSection("LevelGroups");

        if (lgSection != null) {
            for (String group : lgSection.getKeys(false)) {
                int level = lgSection.getInt(group);
                levelGroups.put(group, level);
            }
        }

        return levelGroups;
    }

    private void logWarning(String message) {
        plugin.getLogger().warning(message);
    }

}
