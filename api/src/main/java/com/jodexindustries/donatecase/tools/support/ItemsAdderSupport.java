package com.jodexindustries.donatecase.tools.support;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemsAdderSupport {
    public static ItemStack getItem(@NotNull String namespace, String displayName, List<String> lore) {
        return new ItemStack(Material.STONE);
    }
}
