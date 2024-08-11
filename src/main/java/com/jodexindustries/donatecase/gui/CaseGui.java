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
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Class for initializing case GUI
 */
public class CaseGui {
    private final Inventory inventory;
    private final CaseData caseData;
    private List<CaseData.HistoryData> globalHistoryData;

    public CaseGui(Player p, CaseData caseData) {
        this.caseData = caseData;

        String title = caseData.getCaseTitle();
        GUI gui = caseData.getGui();
        inventory = Bukkit.createInventory(null, gui.getSize(), Tools.rc(title));
        Bukkit.getScheduler().runTaskAsynchronously(Case.getInstance(), () ->
                Case.getAsyncSortedHistoryData().thenAcceptAsync((historyData) -> {
                    globalHistoryData = historyData;
                    for (GUI.Item item : gui.getItems().values()) {
                        try {
                            processItem(p, item);
                        } catch (Throwable e) {
                            Case.getInstance().getLogger().log(Level.WARNING,
                                    "Error occurred while loading item " + item.getItemName() + ":", e);
                        }
                    }
                }));
        p.openInventory(inventory);
    }

    public CaseData getCaseData() {
        return caseData;
    }

    public List<CaseData.HistoryData> getGlobalHistoryData() {
        return globalHistoryData;
    }

    private void processItem(Player p, GUI.Item item) {
        String itemType = item.getType();
        if (!itemType.equalsIgnoreCase("DEFAULT")) {
            String temp = GUITypedItemManager.getByStart(itemType);
            if (temp != null) {
                GUITypedItem typedItem = GUITypedItemManager.getRegisteredItem(temp);
                if (typedItem != null) {
                    TypedItemHandler handler = typedItem.getItemHandler();
                    if (handler != null) item = handler.handle(this, item);
                }
            }
        }
        CaseData.Item.Material material = item.getMaterial();

        // update item placeholders
        material.setDisplayName(PAPISupport.setPlaceholders(p, material.getDisplayName()));
        material.setLore(setPlaceholders(p, caseData.getCaseType(), material.getLore()));

        ItemStack itemStack = Tools.getCaseItem(material);
        item.getSlots().forEach(slot -> inventory.setItem(slot, itemStack));
    }


    private List<String> setPlaceholders(Player p, String caseType, List<String> lore) {
        lore = Tools.rt(lore, "%case%:" + caseType);
        List<String> newLore = new ArrayList<>(lore.size());

        for (String string : lore) {
            String placeholder = Tools.getLocalPlaceholder(string);

            if (placeholder.startsWith("keys") && p != null) {

                if (placeholder.startsWith("keys_")) {
                    String[] parts = placeholder.split("_", 2);
                    if (parts.length == 2) {
                        caseType = parts[1];
                    }
                }

                string = string.replace("%" + placeholder + "%",
                        String.valueOf(Case.getKeysCache(caseType, p.getName())));
            }

            newLore.add(string);
        }

        return newLore.stream()
                .map(line -> PAPISupport.setPlaceholders(p, line))
                .map(Tools::rc)
                .collect(Collectors.toList());
    }

    public Inventory getInventory() {
        return inventory;
    }
}
