package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.tools.Logger;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.api.events.OraxenItemsLoadedEvent;
import io.th0rgal.oraxen.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class OraxenSupport implements Listener {
    private boolean itemsLoaded = false;

    public OraxenSupport(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Logger.log("&aHooked to &bOraxen");
    }

    @EventHandler
    public void onItemsLoaded(OraxenItemsLoadedEvent e) {
        itemsLoaded = true;
    }

    @NotNull
    public ItemStack getItem(@NotNull String id) {
        ItemStack item = new ItemStack(Material.STONE);

        if (itemsLoaded) {
            ItemBuilder itemBuilder = OraxenItems.getItemById(id);
            if (itemBuilder != null) {
                item = itemBuilder.getReferenceClone();
            } else {
                Logger.log("&eCould not find the item you were looking for by Oraxen support. ID: " + id);
            }
        } else {
            Logger.log("&eOraxen items not loaded! Try to &6/dc reload");
        }
        return item;
    }

}
