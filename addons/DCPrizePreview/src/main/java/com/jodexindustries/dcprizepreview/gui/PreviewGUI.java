package com.jodexindustries.dcprizepreview.gui;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGui;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class PreviewGUI {

    public static Inventory loadGUI(CaseData caseData) {
        Collection<CaseDataItem> items = caseData.getItems().values();
        CaseGui caseGui = caseData.getCaseGui();
        if(caseGui == null) return null;

        Inventory inventory = Bukkit.createInventory(null, getSize(items.size()), caseGui.getTitle());
        items = items.stream().sorted(Comparator.comparingInt(CaseDataItem::getIndex)).collect(Collectors.toList());
        for (CaseDataItem item : items) {
            CaseDataMaterial material = item.getMaterial();
            material.updateMeta();
            inventory.addItem((ItemStack) material.getItemStack());
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