package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.tools.Logger;
import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.api.CustomHeadsAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CustomHeadSupport {
    public static ItemStack getSkull(String category, String id, String displayName, List<String> lore) {
        if(DonateCase.instance.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
            CustomHeadsAPI api = CustomHeads.getApi();
            ItemStack item = new ItemStack(Material.STONE);
            try {
                item = api.getHead(category, Integer.parseInt(id));
            } catch (NullPointerException nullPointerException) {
                Logger.log("Could not find the head you were looking for");
            }
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(DonateCase.t.rc(displayName));
            }
            if(lore != null && itemMeta != null) {
                itemMeta.setLore(DonateCase.t.rc(lore));
            }
            item.setItemMeta(itemMeta);
            item.setItemMeta(itemMeta);
            return item;
        } else {
            return null;
        }
    }
}
