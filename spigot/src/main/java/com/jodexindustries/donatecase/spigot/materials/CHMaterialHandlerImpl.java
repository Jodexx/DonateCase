package com.jodexindustries.donatecase.spigot.materials;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.api.CustomHeadsAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CHMaterialHandlerImpl implements MaterialHandler {

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {
        ItemStack item = new ItemStack(Material.STONE);

        String[] args = context.split(":");
        if (args.length < 2) return item;

        String category = args[0];

        try {
            int id = Integer.parseInt(args[1]);

            CustomHeadsAPI api = CustomHeads.getApi();
            item = api.getHead(category, id);
        } catch (Exception e) {
            DCAPI.getInstance().getPlatform().getLogger().warning("Could not find the head you were looking for by CustomHeads support. Category: " + category);
        }

        return item;
    }
}