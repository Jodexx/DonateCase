package com.jodexindustries.donatecase.impl.materials;

import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import com.jodexindustries.donatecase.tools.SkullCreator;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MCURLMaterialHandlerImpl implements MaterialHandler<ItemStack> {

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {
        return SkullCreator.itemFromUrl("http://textures.minecraft.net/texture/" + context);
    }
}
