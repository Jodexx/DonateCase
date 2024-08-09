package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.tools.Logger;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

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

    public ItemStack getItem(@NotNull String namespace) {
        ItemStack item = new ItemStack(Material.STONE);
        if (itemsLoaded) {
            try {
                CustomStack stack = CustomStack.getInstance(namespace);
                if (stack != null) {
                    item = stack.getItemStack();
                } else {
                    Logger.log("&eCould not find the item you were looking for by ItemsAdder support. Namespace: " + namespace);
                }
            } catch (Exception ignored) {
                Logger.log("&eCould not find the item you were looking for by ItemsAdder support. Namespace: " + namespace);
            }
        } else {
            Logger.log("&eItemsAdder items not loaded! Try to &6/dc reload");
        }
        return item;
    }

}
