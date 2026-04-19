package com.jodexindustries.donatecase.spigot.materials.factory;

import com.google.j2objc.annotations.UsedByReflection;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.material.CaseMaterial;
import com.jodexindustries.donatecase.api.data.material.CaseMaterialException;
import com.jodexindustries.donatecase.api.data.material.MaterialFactory;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.api.CustomHeadsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UsedByReflection
public class CustomHeadsMaterialFactory implements MaterialFactory {

    @UsedByReflection
    public static final CustomHeadsMaterialFactory INSTANCE = new CustomHeadsMaterialFactory();

    @Override
    public @Nullable CaseMaterial create(Addon addon) {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
            return null;
        }

        return CaseMaterial.builder()
                .id("CH")
                .addon(addon)
                .handler(new Handler(addon))
                .description("Heads from CustomHeads plugin")
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

            String[] args = context.split(":");
            if (args.length < 2) return item;

            String category = args[0];

            try {
                int id = Integer.parseInt(args[1]);

                CustomHeadsAPI api = CustomHeads.getApi();
                item = api.getHead(category, id);
            } catch (Exception e) {
                addon.getLogger().warning("Could not find the head you were looking for by CustomHeads support. Category: " + category);
            }
            return item;
        }
    }

}
