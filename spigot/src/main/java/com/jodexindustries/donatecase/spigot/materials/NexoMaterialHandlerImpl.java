package com.jodexindustries.donatecase.spigot.materials;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NexoMaterialHandlerImpl implements MaterialHandler {

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {
        ItemStack item = new ItemStack(Material.STONE);

        ItemBuilder itemBuilder = NexoItems.itemFromId(context);
        if (itemBuilder != null) {
            item = itemBuilder.build();
        } else {
            DCAPI.getInstance().getPlatform().getLogger().warning("Could not find the item you were looking for by Nexo support. ID: " + context);
        }
        return item;
    }
}
