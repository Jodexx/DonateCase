package com.jodexindustries.donatecase.api.impl.materials;

import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import day.dean.skullcreator.SkullCreator;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MCURLMaterialHandlerImpl implements MaterialHandler {

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {
        return SkullCreator.itemFromUrl("http://textures.minecraft.net/texture/" + context);
    }
}
