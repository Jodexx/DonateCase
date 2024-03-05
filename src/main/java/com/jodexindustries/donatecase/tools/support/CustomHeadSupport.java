package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.tools.Logger;
import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.api.CustomHeadsAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.jodexindustries.donatecase.DonateCase.instance;

public class CustomHeadSupport {
    public static ItemStack getSkull(@NotNull String category, String id, String displayName, List<String> lore) {
        if(instance.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
            CustomHeadsAPI api = CustomHeads.getApi();
            ItemStack item = new ItemStack(Material.STONE);
            try {
                item = api.getHead(category, Integer.parseInt(id));
            } catch (NullPointerException nullPointerException) {
                Logger.log("&eCould not find the head you were looking for");
            }
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null) {
                if(displayName != null) {
                    itemMeta.setDisplayName(DonateCase.t.rc(displayName));
                }
                if(lore != null) {
                    itemMeta.setLore(DonateCase.t.rc(lore));
                }
            }
            item.setItemMeta(itemMeta);
            return item;
        } else {
            Logger.log("&eYou're using an item from CustomHeads, but it's not loaded on the server!");
        }
        return new ItemStack(Material.STONE);
    }
}
