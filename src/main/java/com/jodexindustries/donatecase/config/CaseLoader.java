package com.jodexindustries.donatecase.config;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.GUITypedItemManager;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.GUI;
import com.jodexindustries.donatecase.api.data.gui.GUITypedItem;
import com.jodexindustries.donatecase.api.events.DonateCaseReloadEvent;
import com.jodexindustries.donatecase.tools.Logger;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class for loading CaseData's from cases folder
 */
public class CaseLoader {
    private final DonateCase plugin;

    /**
     * Default constructor
     *
     * @param plugin DonateCase plugin object
     */
    public CaseLoader(DonateCase plugin) {
        this.plugin = plugin;
    }

    /**
     * Load all cases from "cases" folder to memory
     */
    public void load() {
        Case.caseData.clear();
        int count = 0;

        for (String caseType : plugin.config.getCasesConfig().getCases().keySet()) {
            YamlConfiguration config = plugin.config.getCasesConfig().getCase(caseType).getSecond();
            ConfigurationSection caseSection = config.getConfigurationSection("case");

            if (caseSection == null) {
                logWarning("Case " + caseType + " has a broken case section, skipped.");
                continue;
            }

            CaseData caseData = loadCaseData(caseType, caseSection);

            if (caseData != null) {
                Case.caseData.put(caseType, caseData);
                count++;
            }
        }

        DonateCaseReloadEvent reloadEvent = new DonateCaseReloadEvent(plugin, DonateCaseReloadEvent.Type.CASES);
        Bukkit.getPluginManager().callEvent(reloadEvent);

        Logger.log("&aLoaded &c" + count + "&a cases!");
    }

    private CaseData loadCaseData(String caseType, ConfigurationSection caseSection) {
        CaseData.OpenType openType = CaseData.OpenType.getOpenType(caseSection.getString("OpenType", "GUI"));
        String caseTitle = Tools.rc(caseSection.getString("Title", ""));
        String caseDisplayName = Tools.rc(caseSection.getString("DisplayName", ""));
        String animationName = caseSection.getString("Animation");

        if (animationName == null) {
            logWarning("Case " + caseType + " has no animation, skipped.");
            return null;
        }

        CaseData.Hologram hologram = loadHologram(caseSection);
        Map<String, CaseData.Item> items = loadItems(caseType, caseSection);

        CaseData.HistoryData[] historyData = loadHistoryData(caseType);
        Map<String, Integer> levelGroups = loadLevelGroups(caseSection);

        GUI gui = loadGUI(caseSection);

        List<String> noKeyActions = caseSection.getStringList("NoKeyActions");

        return new CaseData(caseType, caseDisplayName, caseTitle, animationName, items, historyData,
                hologram, levelGroups, gui, noKeyActions, openType);
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
        int index = itemSection.getInt("Index");
        String giveType = itemSection.getString("GiveType", "ONE");
        List<String> actions = itemSection.getStringList("Actions");
        List<String> alternativeActions = itemSection.getStringList("AlternativeActions");
        Map<String, CaseData.Item.RandomAction> randomActions = loadRandomActions(itemSection);
        CaseData.Item.Material material = loadMaterial(itemSection);

        return new CaseData.Item(item, group, chance, index, material, giveType, actions, randomActions, alternativeActions);
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

    private CaseData.Item.Material loadMaterial(ConfigurationSection itemSection) {
        String id = itemSection.getString("Item.ID", "AIR");
        String itemDisplayName = Tools.rc(itemSection.getString("Item.DisplayName"));
        boolean enchanted = itemSection.getBoolean("Item.Enchanted");
        int modelData = itemSection.getInt("Item.ModelData", -1);
        String[] rgb = Tools.parseRGB(itemSection.getString("Item.Rgb"));

        ItemStack itemStack = Tools.loadCaseItem(id);

        return new CaseData.Item.Material(id, itemStack, itemDisplayName, enchanted, null, modelData, rgb);
    }

    private CaseData.HistoryData[] loadHistoryData(String caseType) {
        CaseData.HistoryData[] historyData = new CaseData.HistoryData[10];
            if (!plugin.sql) {
                historyData = plugin.config.getData().getHistoryData(caseType);
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

        if (guiSection != null) {
            int size = guiSection.getInt("Size", 45);
            int updateRate = guiSection.getInt("UpdateRate", -1);
            if (!isValidGuiSize(size)) {
                size = 54;
                logWarning("Wrong GUI size: " + size + ".Using 54");
            }
            ConfigurationSection items = guiSection.getConfigurationSection("Items");

            if (items != null) {
                Map<String, GUI.Item> itemMap = loadGUIItems(items);
                return new GUI(size, itemMap, updateRate);
            }
        }

        return null;
    }

    @NotNull
    private Map<String, GUI.Item> loadGUIItems(@NotNull ConfigurationSection itemsSection) {
        HashMap<String, GUI.Item> itemMap = new HashMap<>();
        Set<Integer> currentSlots = new HashSet<>();

        for (String i : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(i);
            if (itemSection != null) {
                GUI.Item item = loadGUIItem(i, itemSection, currentSlots);
                itemMap.put(i, item);
            }
        }
        return itemMap;
    }


    private GUI.Item loadGUIItem(String i, @NotNull ConfigurationSection itemSection, Set<Integer> currentSlots) {
        String id = itemSection.getString("Material");
        String displayName = Tools.rc(itemSection.getString("DisplayName"));
        boolean enchanted = itemSection.getBoolean("Enchanted");
        String itemType = itemSection.getString("Type", "DEFAULT");
        List<String> lore = Tools.rc(itemSection.getStringList("Lore"));
        int modelData = itemSection.getInt("ModelData", -1);
        String[] rgb = Tools.parseRGB(itemSection.getString("Rgb"));
        List<Integer> slots = getItemSlots(itemSection);

        if (slots.removeIf(currentSlots::contains))
            plugin.getLogger().warning("Item " + i + " contains duplicated slots, removing..");

        currentSlots.addAll(slots);

        ItemStack itemStack = null;

        if(itemType.equalsIgnoreCase("DEFAULT")) {
            itemStack = Tools.loadCaseItem(id);
        } else {
            GUITypedItem typedItem = GUITypedItemManager.getFromString(itemType);
            if (typedItem != null && typedItem.isLoadOnCase()) itemStack = Tools.loadCaseItem(id);
        }

        CaseData.Item.Material material = new CaseData.Item.Material(id, itemStack, displayName, enchanted,
                lore, modelData, rgb);

        return new GUI.Item(i, itemType, material, slots);
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

    private static boolean isValidGuiSize(int size) {
        return size >= 9 && size <= 54 && size % 9 == 0;
    }
}