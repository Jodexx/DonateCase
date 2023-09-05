package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.Logger;
import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.api.CustomHeadsAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CustomHeadSupport {
    public static ItemStack getSkull(String category, String id, String displayname) {
        if(Main.instance.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
            CustomHeadsAPI api = CustomHeads.getApi();
            ItemStack item = new ItemStack(Material.STONE);
            try {
                item = api.getHead(category, Integer.parseInt(id));
            } catch (NullPointerException nullPointerException) {
                Logger.log("Could not find the head you were looking for");
            }
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(Main.t.rc(displayname));
            }
            item.setItemMeta(itemMeta);
            return item;
        } else {
            return null;
        }
    }
    public static ItemStack getSkull(String category, String id, String displayname, List<String> lore) {
        if(Main.instance.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
            CustomHeadsAPI api = CustomHeads.getApi();
            ItemStack item = new ItemStack(Material.STONE);
            try {
                item = api.getHead(category, Integer.parseInt(id));
            } catch (NullPointerException nullPointerException) {
                Logger.log("Could not find the head you were looking for");
            }
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(Main.t.rc(displayname));
            }
            if(lore != null && itemMeta != null) {
                itemMeta.setLore(Main.t.rc(lore));
            }
            item.setItemMeta(itemMeta);
            item.setItemMeta(itemMeta);
            return item;
        } else {
            return null;
        }
    }
}
