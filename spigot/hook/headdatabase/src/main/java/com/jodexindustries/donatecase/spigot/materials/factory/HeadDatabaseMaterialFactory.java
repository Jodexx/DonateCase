package com.jodexindustries.donatecase.spigot.materials.factory;

import com.google.j2objc.annotations.UsedByReflection;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import com.jodexindustries.donatecase.api.data.material.CaseMaterialException;
import com.jodexindustries.donatecase.api.data.material.MaterialFactory;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UsedByReflection
public class HeadDatabaseMaterialFactory implements MaterialFactory {

    @UsedByReflection
    public static final HeadDatabaseMaterialFactory INSTANCE = new HeadDatabaseMaterialFactory();

    @Override
    public @Nullable CaseMaterial create(Addon addon) {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("HeadDatabase")) {
            return null;
        }

        return CaseMaterial.builder()
                .id("HDB")
                .addon(addon)
                .handler(new Handler(addon))
                .description("Heads from HeadDatabase plugin")
                .build();
    }

    static class Handler implements MaterialHandler {

        private final Addon addon;

        Handler(Addon addon) {
            this.addon = addon;
        }

        @Override
        public @NotNull Object handle(@NotNull String context) throws CaseMaterialException {
            ItemStack item = new ItemStack(Material.STONE);
            HeadDatabaseAPI api = new HeadDatabaseAPI();
            try {
                item = api.getItemHead(context);
            } catch (NullPointerException e) {
                addon.getLogger().warning("Could not find the head you were looking for: " + context);
            }
            return item;
        }
    }

}
