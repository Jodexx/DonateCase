package com.jodexindustries.donatecase.gui;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.GUI;
import com.jodexindustries.donatecase.api.data.MaterialType;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.support.CustomHeadSupport;
import com.jodexindustries.donatecase.tools.support.HeadDatabaseSupport;
import com.jodexindustries.donatecase.tools.support.ItemsAdderSupport;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CaseGui {
    private final Inventory inventory;

    public CaseGui(Player p, CaseData caseData) {
        String title = caseData.getCaseTitle();
        String caseType = caseData.getCaseType();
        GUI gui = caseData.getGui();
        inventory = Bukkit.createInventory(null, gui.getSize(), Tools.rc(title));
        Bukkit.getScheduler().runTaskAsynchronously(Case.getInstance(), () -> {
            List<CaseData.HistoryData> globalHistoryData = Case.getSortedHistoryData();
            for (GUI.Item item : gui.getItems().values()) {
                processItem(caseType, p, item.clone(), globalHistoryData);
            }
        });
        p.openInventory(inventory);
    }

    private void processItem(String caseType, Player p, GUI.Item item, List<CaseData.HistoryData> globalHistoryData) {
        if (item.getType().startsWith("HISTORY")) {
            Object[] objects = handleHistoryItem(caseType, item, globalHistoryData);
            if (objects[0] != null) {
                item.getMaterial().setId((String) objects[0]);
            }
            if (objects[1] != null) {
                item.getMaterial().setDisplayName((String) objects[1]);
            }
            if (objects[2] != null) {
                item.getMaterial().setLore((List<String>) objects[2]);
            }
        }
        // update item placeholders
        item.getMaterial().setDisplayName(PAPISupport.setPlaceholders(p, item.getMaterial().getDisplayName()));
        item.getMaterial().setLore(setPlaceholders(p, Tools.rc(item.getMaterial().getLore())));

        ItemStack itemStack = getItem(p, caseType, item);
        item.getSlots().forEach(slot -> inventory.setItem(slot, itemStack));
    }

    private Object[] handleHistoryItem(String caseType, GUI.Item item, List<CaseData.HistoryData> globalHistoryData) {
        Object[] objects = new Object[3];

        String[] typeArgs = item.getType().split("-");
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
        String material = item.getMaterial().getId();
        if(material == null) material = "HEAD:" + data.getPlayerName();

        if (material.equalsIgnoreCase("DEFAULT")) material = historyItem.getMaterial().getId();

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

        String displayName = Tools.rt(item.getMaterial().getDisplayName(), template);
        List<String> lore = Tools.rt(item.getMaterial().getLore(), template);
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

    private ItemStack getItem(Player player, String caseType, GUI.Item item) {
        List<String> newLore = new ArrayList<>();

        List<String> lore = item.getMaterial().getLore();
        String material = item.getMaterial().getId();
        String displayName = item.getMaterial().getDisplayName();
        boolean enchanted = item.getMaterial().isEnchanted();
        String[] rgb = item.getRgb();
        int modelData = item.getModelData();
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
        if (material == null) {
            Case.getInstance().getLogger().warning("Material \"" + material + "\" is null! Case: " + caseType + " Item: " + item.getItemName());
            return new ItemStack(Material.STONE);
        }
        String[] materialParts = material.split(":");
        MaterialType materialType = Tools.getMaterialType(materialParts[0]);

        if (materialType == null) {
            Case.getInstance().getLogger().warning("Material \"" + materialParts[0] + "\" not found! Case: " + caseType + " Item: " + item.getItemName());
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
