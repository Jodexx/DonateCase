package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.tools.Logger;
import com.jodexindustries.donatecase.tools.Tools;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.NotActuallyItemsAdderException;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.jodexindustries.donatecase.DonateCase.instance;

public class ItemsAdderSupport {
    public static ItemStack getItem(@NotNull String namespace, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(Material.STONE);
        if(instance.getServer().getPluginManager().isPluginEnabled("ItemsAdder")) {
            try {
                CustomStack stack = CustomStack.getInstance(namespace);
                if(stack != null) {
                    item = stack.getItemStack();
                    ItemMeta itemMeta = item.getItemMeta();
                    if (displayName != null) {
                        itemMeta.setDisplayName(Tools.rc(displayName));
                    }
                    if (lore != null) {
                        itemMeta.setLore(Tools.rc(lore));
                    }
                    item.setItemMeta(itemMeta);
                } else {
                    Logger.log("&eCould not find the item you were looking for by ItemsAdder support. Namespace: " + namespace);
                }
            } catch (NotActuallyItemsAdderException ignored) {
                Logger.log("&eCould not find the item you were looking for by ItemsAdder support. Namespace: " + namespace);
            }
            } else {
            Logger.log("&eYou're using an item from ItemsAdder, but it's not loaded on the server!");
        }
        return item;
    }
}
