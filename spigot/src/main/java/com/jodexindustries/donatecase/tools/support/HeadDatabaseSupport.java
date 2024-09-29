package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.tools.Logger;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class HeadDatabaseSupport implements Listener {
    private boolean itemsLoaded = false;

    public HeadDatabaseSupport(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Logger.log("&aHooked to &bHeadDatabase");
    }

    @EventHandler
    public void onItemsLoaded(DatabaseLoadEvent e) {
        itemsLoaded = true;
    }

    @NotNull
    public ItemStack getSkull(@NotNull String id) {
        ItemStack item = new ItemStack(Material.STONE);

        if (itemsLoaded) {
            HeadDatabaseAPI api = new HeadDatabaseAPI();
            try {
                item = api.getItemHead(id);
            } catch (NullPointerException nullPointerException) {
                Logger.log("&eCould not find the head you were looking for");
            }
        } else {
            Logger.log("&eHeadDatabase skulls not loaded! Try to &6/dc reload");
        }
        return item;
    }

}