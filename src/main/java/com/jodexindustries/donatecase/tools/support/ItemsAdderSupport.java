package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.tools.Logger;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.jodexindustries.donatecase.DonateCase.instance;

public class ItemsAdderSupport {
    public static ItemStack getItem(@NotNull String namespace, String displayName, List<String> lore) {
        if(instance.getServer().getPluginManager().isPluginEnabled("ItemsAdder")) {
            CustomStack stack = CustomStack.getInstance(namespace);
            if (stack != null) {
                ItemStack item = stack.getItemStack();
                ItemMeta itemMeta = item.getItemMeta();
                if(displayName != null) {
                    itemMeta.setDisplayName(DonateCase.t.rc(displayName));
                }
                if (lore != null) {
                    itemMeta.setLore(DonateCase.t.rc(lore));
                }
                item.setItemMeta(itemMeta);
                return item;
            } else {
                Logger.log("&eCould not find the item you were looking for");
            }
        } else {
            Logger.log("&eYou're using an item from ItemsAdder, but it's not loaded on the server!");
        }
        return new ItemStack(Material.STONE);
    }
}
