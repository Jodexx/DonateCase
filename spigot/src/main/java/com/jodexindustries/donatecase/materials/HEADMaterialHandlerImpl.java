package com.jodexindustries.donatecase.materials;

import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public class HEADMaterialHandlerImpl implements MaterialHandler {

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {

        Material type = Material.PLAYER_HEAD;
        ItemStack item = new ItemStack(type);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setOwner(context);
            item.setItemMeta(meta);
        }
        return item;
    }
}
