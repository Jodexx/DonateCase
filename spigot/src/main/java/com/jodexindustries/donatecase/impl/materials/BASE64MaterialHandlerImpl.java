package com.jodexindustries.donatecase.impl.materials;

import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import com.jodexindustries.donatecase.tools.SkullCreator;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BASE64MaterialHandlerImpl implements MaterialHandler<ItemStack> {

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {
        return SkullCreator.itemFromBase64(context);
    }
}
