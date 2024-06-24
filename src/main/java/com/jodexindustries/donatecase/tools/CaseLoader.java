package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.GUI;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        GUI gui = loadGUI(caseSection);

        return new CaseData(caseType, caseDisplayName, caseTitle, animationName, sound, items, historyData, hologram, levelGroups, gui);
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
        String[] rgb = loadRgb(itemSection, "Item.Rgb");
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

    private String[] loadRgb(ConfigurationSection itemSection, String path) {
        String rgbString = itemSection.getString(path);
        return rgbString == null ? null : rgbString.replaceAll(" ", "").split(",");
    }

    private CaseData.Item.Material loadMaterial(ConfigurationSection itemSection) {
        String id = itemSection.getString("Item.ID", "STONE");
        String itemDisplayName = Tools.rc(itemSection.getString("Item.DisplayName"));
        boolean enchanted = itemSection.getBoolean("Item.Enchanted");
        ItemStack itemStack = Tools.getCaseItem(itemDisplayName, id, enchanted, loadRgb(itemSection, "Item.Rg"));

        return new CaseData.Item.Material(id, itemStack, itemDisplayName, enchanted, null);
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

    private GUI loadGUI(ConfigurationSection caseSection) {
        ConfigurationSection guiSection = caseSection.getConfigurationSection("Gui");

        if(guiSection != null) {
            int size = guiSection.getInt("Size", 45);
            if(!isValidGuiSize(size)) {
                size = 54;
                logWarning("Wrong GUI size: " + size + ".Using 54");
            }
            ConfigurationSection items = guiSection.getConfigurationSection("Items");

            if(items != null) {
                Map<String, GUI.Item> itemMap = loadGUIItems(items);
                return new GUI(size, itemMap);
            }
        }

        return null;
    }

    @NotNull
    private Map<String, GUI.Item> loadGUIItems(@NotNull ConfigurationSection itemsSection) {
        HashMap<String, GUI.Item> itemMap = new HashMap<>();
        for (String i : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(i);
            if(itemSection != null) {
                GUI.Item item = loadGUIItem(i, itemSection);
                itemMap.put(i, item);
            }
        }
        return itemMap;
    }


    private GUI.Item loadGUIItem(String i, @NotNull ConfigurationSection itemSection) {
        String id = itemSection.getString("Material");
        String displayName = itemSection.getString("DisplayName", "");
        boolean enchanted = itemSection.getBoolean("Enchanted");
        String itemType = itemSection.getString("Type", "DEFAULT");
        List<String> lore = itemSection.getStringList("Lore");
        int modelData = itemSection.getInt("ModelData", -1);
        String[] rgb = loadRgb(itemSection, "Rgb");
        List<Integer> slots = getItemSlots(itemSection);


        CaseData.Item.Material material = new CaseData.Item.Material(id, null, displayName, enchanted, lore);

        return new GUI.Item(i, itemType, material, slots, modelData, rgb);
    }


    private List<Integer> getItemSlots(ConfigurationSection itemSection) {
        if (itemSection.isList("Slots")) {
            return getItemSlotsListed(itemSection);
        } else {
            return getItemSlotsRanged(itemSection);
        }
    }

    private List<Integer> getItemSlotsListed(ConfigurationSection itemSection) {
        List<Integer> slots = new ArrayList<>();
        List<String> temp = itemSection.getStringList("Slots");
        for (String slot : temp) {
            String[] values = slot.split("-", 2);
            if (values.length == 2) {
                for (int i = Integer.parseInt(values[0]); i <= Integer.parseInt(values[1]); i++) {
                    slots.add(i);
                }
            } else {
                slots.add(Integer.parseInt(slot));
            }
        }
        return slots;
    }

    private List<Integer> getItemSlotsRanged(ConfigurationSection itemSection) {
        String[] slotArgs = itemSection.getString("Slots", "0-0").split("-");
        int range1 = Integer.parseInt(slotArgs[0]);
        int range2 = slotArgs.length >= 2 ? Integer.parseInt(slotArgs[1]) : range1;
        return IntStream.rangeClosed(range1, range2).boxed().collect(Collectors.toList());
    }



    private void logWarning(String message) {
        plugin.getLogger().warning(message);
    }

    private boolean isValidGuiSize(int size) {
        return size >= 9 && size <= 54 && size % 9 == 0;
    }

}
