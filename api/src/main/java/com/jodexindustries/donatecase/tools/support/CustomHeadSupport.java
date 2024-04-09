package com.jodexindustries.donatecase.tools.support;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomHeadSupport {
    public static ItemStack getSkull(@NotNull String category, String id, String displayName, List<String> lore) {
        return new ItemStack(Material.STONE);
    }
}
