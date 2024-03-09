package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.tools.Logger;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static com.jodexindustries.donatecase.DonateCase.instance;

public class HeadDatabaseSupport {
    public static ItemStack getSkull(String id, String displayName, List<String> lore) {
        if (instance.getServer().getPluginManager().isPluginEnabled("HeadDataBase")) {
            HeadDatabaseAPI api = new HeadDatabaseAPI();
            ItemStack item = new ItemStack(Material.STONE);
            try {
                item = api.getItemHead(id);
            } catch (NullPointerException nullPointerException) {
                Logger.log("&eCould not find the head you were looking for");
            }
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null) {
                if(displayName != null) {
                    itemMeta.setDisplayName(DonateCase.t.rc(displayName));
                }
                if (lore != null) {
                    itemMeta.setLore(DonateCase.t.rc(lore));
                }
                item.setItemMeta(itemMeta);
            }
            return item;
        } else {
            Logger.log("&eYou're using an head from HeadDataBase, but it's not loaded on the server!");
        }
        return new ItemStack(Material.STONE);
    }
}
