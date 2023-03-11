package com.github.cc007.headsplugin.api.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface HeadToItemstackMapper {
    /**
     * Get a list of {@link ItemStack}s, based on the provided list of {@link Head}s.
     * The number of items per stack is set to 1.
     *
     * @param heads the list of heads
     * @return the list of itemstacks
     */
    List<ItemStack> getItemStacks(List<Head> heads);

    /**
     * Get a list of {@link ItemStack}s, based on the provided list of {@link Head}s.
     *
     * @param heads the list of heads
     * @param quantity the number of items per stack
     * @return the list of itemstacks
     */
    List<ItemStack> getItemStacks(List<Head> heads, int quantity);

    /**
     * Get an {@link ItemStack}, based on the provided {@link Head}.
     * The number of items per stack is set to 1.
     *
     * @param head the head
     * @return the itemstack
     */
    ItemStack getItemStack(Head head);

    /**
     * Get an {@link ItemStack}, based on the provided {@link Head}.
     *
     * @param head the head
     * @param quantity the number of items per stack
     * @return the itemstack
     */
    ItemStack getItemStack(Head head, int quantity);
}
