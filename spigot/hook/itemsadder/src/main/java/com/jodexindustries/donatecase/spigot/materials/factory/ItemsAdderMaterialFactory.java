package com.jodexindustries.donatecase.spigot.materials.factory;

import com.google.j2objc.annotations.UsedByReflection;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import com.jodexindustries.donatecase.api.data.material.CaseMaterialException;
import com.jodexindustries.donatecase.api.data.material.MaterialFactory;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import com.jodexindustries.donatecase.api.platform.Platform;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

@UsedByReflection
public class ItemsAdderMaterialFactory implements MaterialFactory {

    @UsedByReflection
    public static final ItemsAdderMaterialFactory INSTANCE = new ItemsAdderMaterialFactory();

    @Override
    public @Nullable CaseMaterial create(Platform platform) {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("ItemsAdder")) {
            return null;
        }

        return CaseMaterial.builder()
                .id("IA")
                .addon(platform)
                .handler(new Handler(platform))
                .description("Items from ItemsAdder plugin")
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
            try {
                CustomStack stack = CustomStack.getInstance(context);
                item = stack.getItemStack();
            } catch (Exception e) {
                platform.getLogger().log(Level.WARNING,
                        "Could not find the item you were looking for by ItemsAdder support. Namespace: ", e);
            }
            return item;
        }
    }

}
