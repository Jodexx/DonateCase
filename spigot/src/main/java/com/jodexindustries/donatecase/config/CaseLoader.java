package com.jodexindustries.donatecase.config;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import com.jodexindustries.donatecase.gui.CaseGui;
import com.jodexindustries.donatecase.impl.managers.GUITypedItemManagerImpl;
import com.jodexindustries.donatecase.api.data.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.*;
import com.jodexindustries.donatecase.api.data.casedata.gui.GUI;
import com.jodexindustries.donatecase.api.data.casedata.gui.GUITypedItem;
import com.jodexindustries.donatecase.api.events.DonateCaseReloadEvent;
import com.jodexindustries.donatecase.tools.Logger;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
                plugin.getLogger().warning("Case " + caseType + " has a broken case section, skipped.");
                continue;
            }

            CaseDataBukkit caseData = loadCaseData(caseType, caseSection);

            if (caseData != null) {
                Case.caseData.put(caseType, caseData);
                count++;
            }
        }

        DonateCaseReloadEvent reloadEvent = new DonateCaseReloadEvent(plugin, DonateCaseReloadEvent.Type.CASES);
        Bukkit.getPluginManager().callEvent(reloadEvent);

        Logger.log("&aLoaded &c" + count + "&a cases!");
    }

    private CaseDataBukkit loadCaseData(String caseType, ConfigurationSection caseSection) {
        OpenType openType = OpenType.getOpenType(caseSection.getString("OpenType", "GUI"));
        String caseTitle = Tools.rc(caseSection.getString("Title", ""));
        String caseDisplayName = Tools.rc(caseSection.getString("DisplayName", ""));
        String animationName = caseSection.getString("Animation");
        ConfigurationSection animationSettings = caseSection.getConfigurationSection("AnimationSettings");

        if (animationName == null) {
            plugin.getLogger().warning("Case " + caseType + " has no animation, skipped.");
            return null;
        }

        CaseDataHologram hologram = loadHologram(caseSection.getConfigurationSection("Hologram"));
        Map<String, CaseDataItem<CaseDataMaterialBukkit>> items = loadItems(caseType, caseSection);

        Map<String, Integer> levelGroups = loadLevelGroups(caseSection);

        GUI<CaseDataMaterialBukkit> gui = loadGUI(caseSection);

        if(gui != null && gui.getTitle().isEmpty()) gui.setTitle(caseTitle);

        List<String> noKeyActions = caseSection.getStringList("NoKeyActions");

        return new CaseDataBukkit(caseType, caseDisplayName, animationName, items, new CaseDataHistory[10],
                hologram, levelGroups, gui, noKeyActions, openType, animationSettings);
    }

    private CaseDataHologram loadHologram(ConfigurationSection caseSection) {
        if (caseSection == null) return new CaseDataHologram();

        boolean hologramEnabled = caseSection.getBoolean("Toggle");
        double hologramHeight = caseSection.getDouble("Height");
        int range = caseSection.getInt("Range");
        List<String> hologramMessage = caseSection.getStringList("Message");

        return hologramEnabled
                ? new CaseDataHologram(true, hologramHeight, range, hologramMessage)
                : new CaseDataHologram();
    }

    private Map<String, CaseDataItem<CaseDataMaterialBukkit>> loadItems(String caseType, ConfigurationSection caseSection) {
        Map<String, CaseDataItem<CaseDataMaterialBukkit>> items = new HashMap<>();
        ConfigurationSection itemsSection = caseSection.getConfigurationSection("Items");

        if (itemsSection != null) {
            for (String item : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(item);

                if (itemSection == null) {
                    plugin.getLogger().warning("Case " + caseType + " has a broken item " + item + " section, skipped.");
                    continue;
                }

                CaseDataItem<CaseDataMaterialBukkit> caseItem = loadItem(item, itemSection);
                if(caseItem != null) items.put(item, caseItem);
            }
        } else {
            plugin.getLogger().warning("Case " + caseType + " has a broken case.Items section");
        }

        return items;
    }

    private CaseDataItem<CaseDataMaterialBukkit> loadItem(String item, ConfigurationSection itemSection) {
        String group = itemSection.getString("Group", "");
        double chance = itemSection.getDouble("Chance");
        int index = itemSection.getInt("Index");
        String giveType = itemSection.getString("GiveType", "ONE");
        List<String> actions = itemSection.getStringList("Actions");
        List<String> alternativeActions = itemSection.getStringList("AlternativeActions");
        Map<String, CaseDataItem.RandomAction> randomActions = loadRandomActions(itemSection);

        ConfigurationSection materialSection = itemSection.getConfigurationSection("Item");
        if(materialSection == null) return null;

        CaseDataMaterialBukkit material = loadMaterial(materialSection, true);
        material.updateMeta();

        return new CaseDataItem<>(item, group, chance, index, material, giveType, actions, randomActions, alternativeActions);
    }

    private Map<String, CaseDataItem.RandomAction> loadRandomActions(ConfigurationSection itemSection) {
        Map<String, CaseDataItem.RandomAction> randomActions = new HashMap<>();
        ConfigurationSection randomActionsSection = itemSection.getConfigurationSection("RandomActions");

        if (randomActionsSection != null) {
            for (String randomAction : randomActionsSection.getKeys(false)) {
                ConfigurationSection randomActionSection = randomActionsSection.getConfigurationSection(randomAction);

                if (randomActionSection != null) {
                    double actionChance = randomActionSection.getDouble("Chance");
                    List<String> randomActionsList = randomActionSection.getStringList("Actions");
                    String displayName = randomActionSection.getString("DisplayName");

                    CaseDataItem.RandomAction randomActionObject = new CaseDataItem.RandomAction(actionChance, randomActionsList, displayName);
                    randomActions.put(randomAction, randomActionObject);
                }
            }
        }

        return randomActions;
    }

    @NotNull
    private CaseDataMaterialBukkit loadMaterial(ConfigurationSection itemSection, boolean withItemStack) {
        String id = itemSection.getString("ID") != null ? itemSection.getString("ID") :
                itemSection.getString("Material");
        String itemDisplayName = Tools.rc(itemSection.getString("DisplayName"));
        List<String> lore = Tools.rc(itemSection.getStringList("Lore"));
        boolean enchanted = itemSection.getBoolean("Enchanted");
        int modelData = itemSection.getInt("ModelData", -1);
        String[] rgb = Tools.parseRGB(itemSection.getString("Rgb"));

        ItemStack itemStack = null;
        if(withItemStack) itemStack = Tools.loadCaseItem(id);

        return new CaseDataMaterialBukkit(id, itemStack, itemDisplayName, enchanted, lore, modelData, rgb);
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

    @Nullable
    private GUI<CaseDataMaterialBukkit> loadGUI(ConfigurationSection caseSection) {
        ConfigurationSection guiSection = caseSection.getConfigurationSection("Gui");

        if (guiSection != null) {
            String title = Tools.rc(guiSection.getString("Title", ""));
            int size = guiSection.getInt("Size", 45);
            int updateRate = guiSection.getInt("UpdateRate", -1);
            if (!isValidGuiSize(size)) {
                size = 54;
                plugin.getLogger().warning("Wrong GUI size: " + size + ".Using 54");
            }
            ConfigurationSection items = guiSection.getConfigurationSection("Items");

            Map<String, GUI.Item<CaseDataMaterialBukkit>> itemMap = loadGUIItems(items);
            return new GUI<>(title, size, itemMap, updateRate);
        }

        return null;
    }

    @NotNull
    private Map<String, GUI.Item<CaseDataMaterialBukkit>> loadGUIItems(@Nullable ConfigurationSection itemsSection) {
        HashMap<String, GUI.Item<CaseDataMaterialBukkit>> itemMap = new HashMap<>();

        if(itemsSection == null) return itemMap;

        Set<Integer> currentSlots = new HashSet<>();

        for (String i : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(i);
            if (itemSection != null) {
                GUI.Item<CaseDataMaterialBukkit> item = loadGUIItem(i, itemSection, currentSlots);
                if(item != null) itemMap.put(i, item);
            }
        }
        return itemMap;
    }


    private GUI.Item<CaseDataMaterialBukkit> loadGUIItem(String i, @NotNull ConfigurationSection itemSection, Set<Integer> currentSlots) {
        String id = itemSection.getString("Material");
        String itemType = itemSection.getString("Type", "DEFAULT");
        List<Integer> slots = getItemSlots(itemSection);

        if(slots.isEmpty()) {
            plugin.getLogger().warning("Item " + i + " has no specified slots");
            return null;
        }

        if (slots.removeIf(currentSlots::contains))
            plugin.getLogger().warning("Item " + i + " contains duplicated slots, removing..");

        currentSlots.addAll(slots);

        CaseDataMaterialBukkit material = loadMaterial(itemSection, false);

        ItemStack itemStack = null;

        if(itemType.equalsIgnoreCase("DEFAULT")) {
            itemStack = Tools.loadCaseItem(id);
        } else {
            GUITypedItem<CaseDataMaterialBukkit, CaseGui, CaseGuiClickEvent> typedItem = GUITypedItemManagerImpl.getFromString(itemType);
            if (typedItem != null) {
                if(typedItem.isLoadOnCase()) itemStack = Tools.loadCaseItem(id);
            } else {
                itemStack = Tools.loadCaseItem(id);
            }
        }

        material.setItemStack(itemStack);

        return new GUI.Item<>(i, itemType, material, slots);
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
        String slots = itemSection.getString("Slots");

        if(slots == null || slots.isEmpty()) return new ArrayList<>();

        String[] slotArgs = slots.split("-");
        int range1 = Integer.parseInt(slotArgs[0]);
        int range2 = slotArgs.length >= 2 ? Integer.parseInt(slotArgs[1]) : range1;
        return IntStream.rangeClosed(range1, range2).boxed().collect(Collectors.toList());
    }

    private static boolean isValidGuiSize(int size) {
        return size >= 9 && size <= 54 && size % 9 == 0;
    }
}