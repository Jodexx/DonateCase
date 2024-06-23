package com.jodexindustries.donatecase.gui;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.MaterialType;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.support.CustomHeadSupport;
import com.jodexindustries.donatecase.tools.support.HeadDatabaseSupport;
import com.jodexindustries.donatecase.tools.support.ItemsAdderSupport;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jodexindustries.donatecase.api.Case.playersGui;

public class CaseGui {
    private Inventory inventory;

    public CaseGui(Player p, CaseData caseData) {
        String title = caseData.getCaseTitle();
        String caseType = caseData.getCaseType();
        YamlConfiguration configCase = Case.getCasesConfig().getCase(caseType);
        int size = configCase.getInt("case.Gui.Size", 45);

        if (isValidGuiSize(size)) {
            inventory = Bukkit.createInventory(null, size, Tools.rc(title));
            ConfigurationSection items = configCase.getConfigurationSection("case.Gui.Items");
            Bukkit.getScheduler().runTaskAsynchronously(Case.getInstance(), () -> {
                if (items != null) {
                    List<CaseData.HistoryData> globalHistoryData = Case.getSortedHistoryData();
                    items.getKeys(false).forEach(item -> processItem(caseType, p, item, configCase, globalHistoryData));
                }
            });
            p.openInventory(inventory);
        } else {
            handleInvalidSize(p);
        }
    }

    private boolean isValidGuiSize(int size) {
        return size >= 9 && size <= 54 && size % 9 == 0;
    }

    private void handleInvalidSize(Player p) {
        Tools.msg(p, "&cSomething wrong! Contact with server administrator!");
        Case.getInstance().getLogger().warning("Wrong GUI size! Use: 9, 18, 27, 36, 45, 54");
        playersGui.remove(p.getUniqueId());
    }

    private void processItem(String caseType, Player p, String item, YamlConfiguration configCase, List<CaseData.HistoryData> globalHistoryData) {
        String material = configCase.getString("case.Gui.Items." + item + ".Material", "STONE");
        String displayName = PAPISupport.setPlaceholders(p, configCase.getString("case.Gui.Items." + item + ".DisplayName", "None"));
        boolean enchanted = configCase.getBoolean("case.Gui.Items." + item + ".Enchanted");
        String itemType = configCase.getString("case.Gui.Items." + item + ".Type", "DEFAULT");
        List<String> lore = setPlaceholders(p, Tools.rc(configCase.getStringList("case.Gui.Items." + item + ".Lore")));
        int modelData = configCase.getInt("case.Gui.Items." + item + ".ModelData", -1);
        String[] rgb = getRgb(configCase, material, item);

        if (itemType.startsWith("HISTORY")) {
            Object[] objects = handleHistoryItem(caseType, item, configCase, globalHistoryData, itemType, displayName, lore);
            if (objects[0] != null) {
                material = (String) objects[0];
            }
            if (objects[1] != null) {
                displayName = (String) objects[1];
            }
            if (objects[2] != null) {
                lore = (List<String>) objects[2];
            }
        }

        List<String> slots = getItemSlots(configCase, item);
        ItemStack itemStack = getItem(p, caseType, material, displayName, lore, enchanted, rgb, modelData);
        slots.forEach(slot -> inventory.setItem(Integer.parseInt(slot), itemStack));
    }

    private Object[] handleHistoryItem(String caseType, String item, YamlConfiguration configCase, List<CaseData.HistoryData> globalHistoryData, String itemType, String displayName, List<String> lore) {
        Object[] objects = new Object[3];

        String[] typeArgs = itemType.split("-");
        int index = Integer.parseInt(typeArgs[1]);
        caseType = (typeArgs.length >= 3) ? typeArgs[2] : caseType;
        boolean isGlobal = caseType.equalsIgnoreCase("GLOBAL");

        CaseData historyCaseData = isGlobal ? null : Case.getCase(caseType);
        if (historyCaseData == null && !isGlobal) {
            Case.getInstance().getLogger().warning("Case " + caseType + " HistoryData is null!");
            return objects;
        }
        if (!isGlobal) {
            historyCaseData = historyCaseData.clone();
        }

        CaseData.HistoryData data = getHistoryData(caseType, isGlobal, globalHistoryData, index, historyCaseData);
        if (data == null) return objects;

        if (isGlobal) historyCaseData = Case.getCase(data.getCaseType());
        if (historyCaseData == null) return objects;

        CaseData.Item historyItem = historyCaseData.getItem(data.getItem());
        if (historyItem == null) return objects;

        String material = configCase.getString("case.Gui.Items." + item + ".Material", "HEAD:" + data.getPlayerName());
        if (material.equalsIgnoreCase("DEFAULT")) {
            material = historyItem.getMaterial().getId();
        }

        CaseData.Item.RandomAction randomAction = historyItem.getRandomAction(data.getAction());
        String randomActionDisplayName = randomAction != null ? randomAction.getDisplayName() : "";
        DateFormat formatter = new SimpleDateFormat(Case.getCustomConfig().getConfig().getString("DonatCase.DateFormat", "dd.MM HH:mm:ss"));
        String dateFormatted = formatter.format(new Date(data.getTime()));
        String groupDisplayName = data.getItem() != null ? historyItem.getMaterial().getDisplayName() : "open_case_again";
        String[] template = {
                "%action%:" + data.getAction(),
                "%actiondisplayname%:" + randomActionDisplayName,
                "%casedisplayname%:" + historyCaseData.getCaseDisplayName(),
                "%casename%:" + data.getCaseType(),
                "%casetitle%:" + historyCaseData.getCaseTitle(),
                "%time%:" + dateFormatted,
                "%group%:" + data.getGroup(),
                "%player%:" + data.getPlayerName(),
                "%groupdisplayname%:" + groupDisplayName
        };

        displayName = Tools.rt(displayName, template);
        lore = Tools.rt(lore, template);
        objects[0] = material;
        objects[1] = displayName;
        objects[2] = lore;

        return objects;
    }

    private List<String> setPlaceholders(Player p, List<String> lore) {
        return lore.stream()
                .map(line -> PAPISupport.setPlaceholders(p, line))
                .map(Tools::rc)
                .collect(Collectors.toList());
    }

    @Nullable
    private String[] getRgb(YamlConfiguration configCase, String material, String item) {
        String[] rgb = null;
        if (material.toUpperCase().startsWith("LEATHER_")) {
            String rgbString = configCase.getString("case.Gui.Items." + item + ".Rgb");
            if (rgbString != null) {
                rgb = rgbString.replace(" ", "").split(",");
            }
        }
        return rgb;
    }

    private CaseData.HistoryData getHistoryData(String caseType, boolean isGlobal, List<CaseData.HistoryData> globalHistoryData, int index, CaseData historyCaseData) {
        CaseData.HistoryData data = null;
        if (isGlobal) {
            if (globalHistoryData.size() <= index) return null;
            data = globalHistoryData.get(index);
        } else {
            if (!Case.getInstance().sql) {
                data = historyCaseData.getHistoryData()[index];
            } else {
                List<CaseData.HistoryData> dbData = Case.sortHistoryDataByCase(globalHistoryData, caseType);
                if (!dbData.isEmpty() && dbData.size() > index) {
                    data = dbData.get(index);
                }
            }
        }
        return data;
    }

    private List<String> getItemSlots(YamlConfiguration configCase, String item) {
        if (configCase.isList("case.Gui.Items." + item + ".Slots")) {
            return getItemSlotsListed(configCase, item);
        } else {
            return getItemSlotsRanged(configCase, item);
        }
    }

    private List<String> getItemSlotsListed(YamlConfiguration configCase, String item) {
        List<String> slots = new ArrayList<>();
        List<String> temp = configCase.getStringList("case.Gui.Items." + item + ".Slots");
        for (String slot : temp) {
            String[] values = slot.split("-", 2);
            if (values.length == 2) {
                for (int i = Integer.parseInt(values[0]); i <= Integer.parseInt(values[1]); i++) {
                    slots.add(String.valueOf(i));
                }
            } else {
                slots.add(slot);
            }
        }
        return slots;
    }

    private List<String> getItemSlotsRanged(YamlConfiguration configCase, String item) {
        String[] slotArgs = configCase.getString("case.Gui.Items." + item + ".Slots", "0-0").split("-");
        int range1 = Integer.parseInt(slotArgs[0]);
        int range2 = slotArgs.length >= 2 ? Integer.parseInt(slotArgs[1]) : range1;
        return IntStream.rangeClosed(range1, range2).mapToObj(String::valueOf).collect(Collectors.toList());
    }

    private ItemStack getItem(Player player, String caseType, String material, String displayName, List<String> lore, boolean enchanted, String[] rgb, int modelData) {
        List<String> newLore = new ArrayList<>();
        if (lore != null) {
            for (String string : lore) {
                String placeholder = Tools.getLocalPlaceholder(string);
                if (placeholder.startsWith("keys")) {
                    if (placeholder.startsWith("keys_")) {
                        String[] parts = placeholder.split("_");
                        if (parts.length >= 2) {
                            caseType = parts[1];
                        }
                    }
                    if(player != null) {
                        newLore.add(string.replace("%" + placeholder + "%", String.valueOf(Case.getKeys(caseType, player.getName()))));
                    }
                } else {
                    newLore.add(string);
                }
            }
        }

        String[] materialParts = material.split(":");
        MaterialType materialType = Tools.getMaterialType(materialParts[0]);

        if (materialType == null) {
            Case.getInstance().getLogger().warning("Material \"" + materialParts[0] + "\" not found! Case: " + caseType);
            return new ItemStack(Material.STONE);
        }

        switch (materialType) {
            case HEAD:
                return Tools.getPlayerHead(materialParts[1], displayName, Tools.rt(newLore, "%case%:" + caseType));
            case HDB:
                return HeadDatabaseSupport.getSkull(materialParts[1], displayName, Tools.rt(newLore, "%case%:" + caseType));
            case CH:
                return CustomHeadSupport.getSkull(materialParts[1], materialParts[2], displayName, Tools.rt(newLore, "%case%:" + caseType));
            case IA:
                return ItemsAdderSupport.getItem(materialParts[1] + ":" + materialParts[2], displayName, Tools.rt(newLore, "%case%:" + caseType));
            case BASE64:
                return Tools.getBASE64Skull(materialParts[1], displayName, Tools.rt(newLore, "%case%:" + caseType));
            default:
                byte data = -1;
                if(materialParts.length > 1)  {
                    try {
                        data = Byte.parseByte(materialParts[1]);
                    } catch (NumberFormatException ignored) {}
                }
                return Tools.createItem(Material.getMaterial(materialParts[0]), 1, data, displayName, Tools.rt(newLore, "%case%:" + caseType), enchanted, rgb, modelData);
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}
