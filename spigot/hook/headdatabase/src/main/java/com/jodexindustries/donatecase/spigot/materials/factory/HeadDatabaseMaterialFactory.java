package com.jodexindustries.donatecase.spigot.materials.factory;

import com.google.j2objc.annotations.UsedByReflection;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import com.jodexindustries.donatecase.api.data.material.CaseMaterialException;
import com.jodexindustries.donatecase.api.data.material.MaterialFactory;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import com.jodexindustries.donatecase.api.platform.Platform;
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
    public @Nullable CaseMaterial create(Platform platform) {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("HeadDatabase")) {
            return null;
        }

        return CaseMaterial.builder()
                .id("HDB")
                .addon(platform)
                .handler(new Handler(platform))
                .description("Heads from HeadDatabase plugin")
                .build();
    }

    static class Handler implements MaterialHandler {

        private final Platform platform;

        Handler(Platform platform) {
            this.platform = platform;
        }

        @Override
        public @NotNull Object handle(@NotNull String context) throws CaseMaterialException {
            ItemStack item = new ItemStack(Material.STONE);
            HeadDatabaseAPI api = new HeadDatabaseAPI();
            try {
                item = api.getItemHead(context);
            } catch (NullPointerException e) {
                platform.getLogger().warning("Could not find the head you were looking for: " + context);
            }
            return item;
        }
    }

}
