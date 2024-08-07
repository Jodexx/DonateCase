package com.jodexindustries.donatecase.gui;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.GUI;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.Trio;
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

/**
 * Class for initializing case GUI
 */
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
            Trio<String, String, List<String>> trio = handleHistoryItem(caseType, item, globalHistoryData);
            if (trio.getFirst() != null) item.getMaterial().setId(trio.getFirst());
            if (trio.getSecond() != null) item.getMaterial().setDisplayName(trio.getSecond());
            if (trio.getThird() != null) item.getMaterial().setLore(trio.getThird());
        }
        // update item placeholders
        item.getMaterial().setDisplayName(PAPISupport.setPlaceholders(p, item.getMaterial().getDisplayName()));
        item.getMaterial().setLore(setPlaceholders(p, Tools.rc(item.getMaterial().getLore())));

        ItemStack itemStack = getItem(p, caseType, item);
        item.getSlots().forEach(slot -> inventory.setItem(slot, itemStack));
    }

    private Trio<String, String, List<String>> handleHistoryItem(String caseType, GUI.Item item, List<CaseData.HistoryData> globalHistoryData) {
        Trio<String, String, List<String>> trio = new Trio<>();

        String[] typeArgs = item.getType().split("-");
        int index = Integer.parseInt(typeArgs[1]);
        caseType = (typeArgs.length >= 3) ? typeArgs[2] : caseType;
        boolean isGlobal = caseType.equalsIgnoreCase("GLOBAL");

        CaseData historyCaseData = isGlobal ? null : Case.getCase(caseType);
        if (historyCaseData == null && !isGlobal) {
            Case.getInstance().getLogger().warning("Case " + caseType + " HistoryData is null!");
            return trio;
        }

        if (!isGlobal) historyCaseData = historyCaseData.clone();

        CaseData.HistoryData data = getHistoryData(caseType, isGlobal, globalHistoryData, index, historyCaseData);
        if (data == null) return trio;

        if (isGlobal) historyCaseData = Case.getCase(data.getCaseType());
        if (historyCaseData == null) return trio;

        CaseData.Item historyItem = historyCaseData.getItem(data.getItem());
        if (historyItem == null) return trio;
        String material = item.getMaterial().getId();
        if(material == null) material = "HEAD:" + data.getPlayerName();

        if (material.equalsIgnoreCase("DEFAULT")) material = historyItem.getMaterial().getId();

        String[] template = getTemplate(historyCaseData, data, historyItem);

        String displayName = Tools.rt(item.getMaterial().getDisplayName(), template);
        List<String> lore = Tools.rt(item.getMaterial().getLore(), template);

        trio.setFirst(material);
        trio.setSecond(displayName);
        trio.setThird(lore);

        return trio;
    }

    private String[] getTemplate(CaseData historyCaseData, CaseData.HistoryData data, CaseData.Item historyItem) {

        DateFormat formatter = new SimpleDateFormat(Case.getConfig().getConfig().getString("DonatCase.DateFormat", "dd.MM HH:mm:ss"));
        String dateFormatted = formatter.format(new Date(data.getTime()));
        String group = data.getGroup();
        String groupDisplayName = data.getItem() != null ? historyItem.getMaterial().getDisplayName() : "group_not_found";
        String action = data.getAction() != null ? data.getAction() : group;

        String randomActionDisplayName = "random_action_not_found";
        if(data.getAction() != null && !data.getAction().isEmpty()) {
            CaseData.Item.RandomAction randomAction = historyItem.getRandomAction(data.getAction());
            if(randomAction != null) {
                randomActionDisplayName = randomAction.getDisplayName();
            }
        } else {
            randomActionDisplayName = groupDisplayName;
        }

        return new String[]{
                "%action%:" + action,
                "%actiondisplayname%:" + randomActionDisplayName,
                "%casedisplayname%:" + historyCaseData.getCaseDisplayName(),
                "%casename%:" + data.getCaseType(),
                "%casetitle%:" + historyCaseData.getCaseTitle(),
                "%time%:" + dateFormatted,
                "%group%:" + group,
                "%player%:" + data.getPlayerName(),
                "%groupdisplayname%:" + groupDisplayName
        };
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
        int modelData = item.getMaterial().getModelData();
        if (lore != null) {
            lore = Tools.rt(lore, "%case%:" + caseType);
            for (String string : lore) {
                String placeholder = Tools.getLocalPlaceholder(string);
                String tempCaseType = caseType;
                if (placeholder.startsWith("keys")) {
                    if (placeholder.startsWith("keys_")) {
                        String[] parts = placeholder.split("_");
                        if (parts.length >= 2) {
                            tempCaseType = parts[1];
                        }
                    }
                    if(player != null) {
                        newLore.add(string.replace("%" + placeholder + "%", String.valueOf(Case.getKeysCache(tempCaseType, player.getName()))));
                    }
                } else {
                    newLore.add(string);
                }
            }
        }
        if (material == null) {
            return new ItemStack(Material.AIR);
        }

        return Tools.getCaseItem(material, displayName, newLore, enchanted, rgb, modelData);
    }


    public Inventory getInventory() {
        return inventory;
    }
}
