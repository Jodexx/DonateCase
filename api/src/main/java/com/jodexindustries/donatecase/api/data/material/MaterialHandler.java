package com.jodexindustries.donatecase.api.data.material;

import org.jetbrains.annotations.NotNull;

/**
 * Interface for handling item initializing
 */
public interface MaterialHandler<I> {

    /**
     * Called when the item is initialized
     *
     * @param context Material id context
     * @return ItemStack of completed material
     */
    @NotNull
    I handle(@NotNull String context);
}
