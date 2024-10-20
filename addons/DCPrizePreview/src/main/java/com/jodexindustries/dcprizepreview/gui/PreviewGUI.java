package com.jodexindustries.dcprizepreview.gui;

import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.GUI;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class PreviewGUI {

    public static Inventory loadGUI(CaseData caseData) {
        Collection<CaseData.Item> items = caseData.getItems().values();
        GUI gui = caseData.getGui();
        if(gui == null) return null;

        Inventory inventory = Bukkit.createInventory(null, getSize(items.size()), gui.getTitle());
        items = items.stream().sorted(Comparator.comparingInt(CaseData.Item::getIndex)).collect(Collectors.toList());
        for (CaseData.Item item : items) {
            CaseData.Item.Material material = item.getMaterial();
            material.updateMeta();
            inventory.addItem(material.getItemStack());
        }
        return inventory;
    }

    private static int getSize(int items) {
        if (items > 45) return 54;
        if (items > 36) return 45;
        if (items > 27) return 36;
        if (items > 18) return 27;
        if (items > 9) return 18;
        return 9;
    }

}