package com.jodexindustries.donatecase.materials;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class OraxenMaterialHandlerImpl implements MaterialHandler {

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {
        ItemStack item = new ItemStack(Material.STONE);

        ItemBuilder itemBuilder = OraxenItems.getItemById(context);
        if (itemBuilder != null) {
            item = itemBuilder.getReferenceClone();
        } else {
            DCAPI.getInstance().getPlatform().getLogger().warning("Could not find the item you were looking for by Oraxen support. ID: " + context);
        }
        return item;
    }
}
