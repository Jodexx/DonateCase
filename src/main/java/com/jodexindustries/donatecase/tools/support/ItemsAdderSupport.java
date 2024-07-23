package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.tools.Logger;
import com.jodexindustries.donatecase.tools.Tools;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import dev.lone.itemsadder.api.NotActuallyItemsAdderException;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemsAdderSupport implements Listener {
    private boolean itemsLoaded = false;

    public ItemsAdderSupport(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Logger.log("&aHooked to &bItemsAdder");
    }

    @EventHandler
    public void onItemsLoaded(ItemsAdderLoadDataEvent e) {
        itemsLoaded = true;
    }

    public ItemStack getItem(@NotNull String namespace, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(Material.STONE);
        if (itemsLoaded) {
            try {
                CustomStack stack = CustomStack.getInstance(namespace);
                if (stack != null) {
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
            Logger.log("&eCould not find the item you were looking for by ItemsAdder support. Namespace: " + namespace);
        }
        return item;
    }

    public boolean areItemsLoaded() {
        return itemsLoaded;
    }
}
