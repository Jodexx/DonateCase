package com.jodexindustries.donatecase.gui;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.GUITypedItemManager;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.GUI;
import com.jodexindustries.donatecase.api.data.gui.GUITypedItem;
import com.jodexindustries.donatecase.api.data.gui.TypedItemHandler;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for initializing case GUI
 */
public class CaseGui {
    private final Inventory inventory;
    private List<CaseData.HistoryData> globalHistoryData;

    public CaseGui(Player p, CaseData caseData) {
        String title = caseData.getCaseTitle();
        GUI gui = caseData.getGui();
        inventory = Bukkit.createInventory(null, gui.getSize(), Tools.rc(title));
        Bukkit.getScheduler().runTaskAsynchronously(Case.getInstance(), () ->
                Case.getAsyncSortedHistoryData().thenAcceptAsync((historyData) -> {
                    globalHistoryData = historyData;
            for (GUI.Item item : gui.getItems().values()) {
                processItem(caseData, p, item.clone());
            }
        }));
        p.openInventory(inventory);
    }

    public List<CaseData.HistoryData> getGlobalHistoryData() {
        return globalHistoryData;
    }

    private void processItem(CaseData caseData, Player p, GUI.Item item) {
        String itemType = item.getType();
        if(itemType != null) {
            String temp = GUITypedItemManager.getByStart(itemType);
            if(temp != null) {
                GUITypedItem typedItem = GUITypedItemManager.getRegisteredItem(temp);
                if(typedItem != null) {
                    TypedItemHandler handler = typedItem.getItemHandler();
                    if(handler != null) {
                        item = handler.handle(this, caseData, item);
                    }
                }
            }
        }
        CaseData.Item.Material material = item.getMaterial();

        // update item placeholders
        material.setDisplayName(PAPISupport.setPlaceholders(p, material.getDisplayName()));
        material.setLore(setPlaceholders(p, Tools.rc(material.getLore())));

        ItemStack itemStack = getItem(p, caseData.getCaseType(), material);
        item.getSlots().forEach(slot -> inventory.setItem(slot, itemStack));
    }



    private List<String> setPlaceholders(Player p, List<String> lore) {
        return lore.stream()
                .map(line -> PAPISupport.setPlaceholders(p, line))
                .map(Tools::rc)
                .collect(Collectors.toList());
    }


    private ItemStack getItem(Player player, String caseType, CaseData.Item.Material item) {
        List<String> newLore = new ArrayList<>();

        List<String> lore = item.getLore();
        String material = item.getId();
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
        item.setLore(newLore);

        if (material == null) return new ItemStack(Material.AIR);

        return Tools.getCaseItem(item);
    }


    public Inventory getInventory() {
        return inventory;
    }
}
