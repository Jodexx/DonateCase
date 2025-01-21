package com.jodexindustries.donatecase.impl.materials;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HDBMaterialHandlerImpl implements MaterialHandler {

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {
        ItemStack item = new ItemStack(Material.STONE);


        HeadDatabaseAPI api = new HeadDatabaseAPI();
        try {
            item = api.getItemHead(context);
        } catch (NullPointerException e) {
            DCAPI.getInstance().getPlatform().getLogger().warning("Could not find the head you were looking for: " + context);
        }

        return item;
    }
}
