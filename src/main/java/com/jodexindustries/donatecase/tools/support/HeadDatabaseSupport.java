package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.tools.Logger;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class HeadDatabaseSupport {
    public static ItemStack getSkull(String id, String displayName, List<String> lore) {
        HeadDatabaseAPI api = new HeadDatabaseAPI();
        ItemStack item = new ItemStack(Material.STONE);
        try {
            item = api.getItemHead(id);
        } catch (NullPointerException nullPointerException) {
            Logger.log("Could not find the head you were looking for");
        }
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(DonateCase.t.rc(displayName));
        if(lore != null) {
            itemMeta.setLore(DonateCase.t.rc(lore));
        }
        item.setItemMeta(itemMeta);
        return item;
    }
}
