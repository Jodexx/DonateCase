package com.jodexindustries.donatecase.spigot.materials.factory;

import com.google.j2objc.annotations.UsedByReflection;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import com.jodexindustries.donatecase.api.data.material.MaterialFactory;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import com.jodexindustries.donatecase.api.platform.Platform;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@UsedByReflection
public class NexoMaterialFactory implements MaterialFactory {

    @UsedByReflection
    public static final NexoMaterialFactory INSTANCE = new NexoMaterialFactory();

    @Override
    public @Nullable CaseMaterial create(Platform platform) {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("Nexo")) {
            return null;
        }

        return CaseMaterial.builder()
                .id("NEXO")
                .addon(platform)
                .handler(getHandler(platform))
                .description("Items from Nexo plugin")
                .build();
    }

    private static MaterialHandler getHandler(Platform platform) {
        return context -> {
            ItemStack item = new ItemStack(Material.STONE);

            ItemBuilder itemBuilder = NexoItems.itemFromId(context);
            if (itemBuilder != null) {
                item = itemBuilder.build();
            } else {
                platform.getLogger().warning("Could not find the item you were looking for by Nexo support. ID: " + context);
            }
            return item;
        };
    }

}
