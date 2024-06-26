package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.tools.Logger;
import com.jodexindustries.donatecase.tools.Tools;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class HeadDatabaseSupport {
    public static ItemStack getSkull(String id, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(Material.STONE);
        if (Case.getInstance().getServer().getPluginManager().isPluginEnabled("HeadDataBase")) {
            HeadDatabaseAPI api = new HeadDatabaseAPI();
            try {
                item = api.getItemHead(id);
            } catch (NullPointerException nullPointerException) {
                Logger.log("&eCould not find the head you were looking for");
            }
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null) {
                if(displayName != null) {
                    itemMeta.setDisplayName(Tools.rc(displayName));
                }
                if (lore != null) {
                    itemMeta.setLore(Tools.rc(lore));
                }
                item.setItemMeta(itemMeta);
            }
        } else {
            Logger.log("&eYou're using an head from HeadDataBase, but it's not loaded on the server!");
        }
        return item;
    }
}
