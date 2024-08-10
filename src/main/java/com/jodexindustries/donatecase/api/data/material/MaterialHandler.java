package com.jodexindustries.donatecase.api.data.material;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @since 2.2.4.8
 */
public interface MaterialHandler {

    /**
     * Called when the item is initialized
     * @param context Material id context
     * @return ItemStack of completed material
     */
    @NotNull
    ItemStack handle(@NotNull String context);
}
