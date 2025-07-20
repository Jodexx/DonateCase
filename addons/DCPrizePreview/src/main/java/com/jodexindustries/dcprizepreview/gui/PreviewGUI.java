package com.jodexindustries.dcprizepreview.gui;

import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseItem;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMaterial;
import com.jodexindustries.donatecase.api.tools.DCTools;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class PreviewGUI {

    public static @NotNull Inventory loadGUI(@NotNull CaseDefinition definition) {
        Collection<CaseItem> items = definition.items().items().values();

        Inventory inventory = Bukkit.createInventory(null, getSize(items.size()), DCTools.rc(definition.defaultMenu().title()));
        items = items.stream().sorted(Comparator.comparingInt(CaseItem::index)).collect(Collectors.toList());
        for (CaseItem item : items) {
            CaseMaterial material = item.material();
            material.updateMeta();
            inventory.addItem((ItemStack) material.itemStack());
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