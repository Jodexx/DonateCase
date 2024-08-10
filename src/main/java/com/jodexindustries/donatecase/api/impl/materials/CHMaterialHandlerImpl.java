package com.jodexindustries.donatecase.api.impl.materials;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import com.jodexindustries.donatecase.tools.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CHMaterialHandlerImpl implements MaterialHandler {

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {
        ItemStack item = new ItemStack(Material.STONE);

        String[] parts = context.split(":");
        if(parts.length != 2) return item;

        if (DonateCase.instance.customHeadsSupport != null) {
            item = DonateCase.instance.customHeadsSupport.getSkull(parts[0], parts[1]);
        } else {
            Logger.log("&eYou're using an item from CustomHeads, but it's not loaded on the server! Context: " + context);
        }

        return item;
    }
}
