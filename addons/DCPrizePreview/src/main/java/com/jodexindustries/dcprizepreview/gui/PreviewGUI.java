package com.jodexindustries.dcprizepreview.gui;

import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.api.data.casedata.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class PreviewGUI {

    public static Inventory loadGUI(CaseDataBukkit caseData) {
        Collection<CaseDataItem<CaseDataMaterialBukkit, ItemStack>> items = caseData.getItems().values();
        GUI<CaseDataMaterialBukkit> gui = caseData.getGui();
        if(gui == null) return null;

        Inventory inventory = Bukkit.createInventory(null, getSize(items.size()), gui.getTitle());
        items = items.stream().sorted(Comparator.comparingInt(CaseDataItem::getIndex)).collect(Collectors.toList());
        for (CaseDataItem<CaseDataMaterialBukkit, ItemStack>item : items) {
            CaseDataMaterialBukkit material = item.getMaterial();
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