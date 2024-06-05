package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.tools.Logger;
import com.jodexindustries.donatecase.tools.Tools;
import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.api.CustomHeadsAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomHeadSupport {
    public static ItemStack getSkull(@NotNull String category, String id, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(Material.STONE);
        if(Case.getInstance().getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
            CustomHeadsAPI api = CustomHeads.getApi();
            try {
                item = api.getHead(category, Integer.parseInt(id));
            } catch (NullPointerException nullPointerException) {
                Logger.log("&eCould not find the head you were looking for by CustomHeads support. Category: " + category + " Id: " + id);
            }
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null) {
                if(displayName != null) {
                    itemMeta.setDisplayName(Tools.rc(displayName));
                }
                if(lore != null) {
                    itemMeta.setLore(Tools.rc(lore));
                }
            }
            item.setItemMeta(itemMeta);
        } else {
            Logger.log("&eYou're using an item from CustomHeads, but it's not loaded on the server!");
        }
        return item;
    }
}
