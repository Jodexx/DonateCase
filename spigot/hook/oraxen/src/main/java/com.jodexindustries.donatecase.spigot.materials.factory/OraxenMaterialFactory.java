package com.jodexindustries.donatecase.spigot.materials.factory;

import com.google.j2objc.annotations.UsedByReflection;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import com.jodexindustries.donatecase.api.data.material.MaterialFactory;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import com.jodexindustries.donatecase.api.platform.Platform;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@UsedByReflection
public class OraxenMaterialFactory implements MaterialFactory {

    @UsedByReflection
    public static final OraxenMaterialFactory INSTANCE = new OraxenMaterialFactory();

    @Override
    public @Nullable CaseMaterial create(Platform platform) {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("Oraxen")) {
            return null;
        }

        return CaseMaterial.builder()
                .id("ORAXEN")
                .addon(platform)
                .handler(getHandler(platform))
                .description("Items from Oraxen plugin")
                .build();
    }

    private static MaterialHandler getHandler(Platform platform) {
        return context -> {
            ItemStack item = new ItemStack(Material.STONE);

            ItemBuilder itemBuilder = OraxenItems.getItemById(context);
            if (itemBuilder != null) {
                item = itemBuilder.getReferenceClone();
            } else {
                platform.getLogger().warning("Could not find the item you were looking for by Oraxen support. ID: " + context);
            }
            return item;
        };
    }
}
