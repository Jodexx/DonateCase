package com.jodexindustries.donatecase.tools.support;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;


public class HeadDatabaseSupport {
    public static ItemStack getSkull(String id, String displayName, List<String> lore) {
        return new ItemStack(Material.STONE);
    }
}
