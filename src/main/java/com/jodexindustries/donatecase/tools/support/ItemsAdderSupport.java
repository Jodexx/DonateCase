package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.dc.Main;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemsAdderSupport {
    public static ItemStack getItem(@NotNull String namespace, String displayName, List<String> lore) {
        CustomStack stack = CustomStack.getInstance(namespace);
        if (stack != null) {
            ItemStack item = stack.getItemStack();
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(Main.t.rc(displayName));
            if(lore != null) {
                itemMeta.setLore(Main.t.rc(lore));
            }
            item.setItemMeta(itemMeta);
            return item;
        }
        return null;
    }
}
