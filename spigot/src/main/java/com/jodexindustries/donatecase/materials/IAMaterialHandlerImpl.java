package com.jodexindustries.donatecase.materials;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class IAMaterialHandlerImpl implements MaterialHandler {

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {
        ItemStack item = new ItemStack(Material.STONE);
        try {
            CustomStack stack = CustomStack.getInstance(context);
            item = stack.getItemStack();
        } catch (Exception e) {
            DCAPI.getInstance().getPlatform().getLogger().log(Level.WARNING,
                    "Could not find the item you were looking for by ItemsAdder support. Namespace: ", e);
        }
        return item;
    }
}
