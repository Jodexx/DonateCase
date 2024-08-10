package com.jodexindustries.donatecase.api.impl.materials;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import com.jodexindustries.donatecase.tools.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class OraxenMaterialHandlerImpl implements MaterialHandler {

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {
        ItemStack item = new ItemStack(Material.STONE);

        if(DonateCase.instance.oraxenSupport != null) {
            item = DonateCase.instance.oraxenSupport.getItem(context);
        } else {
            Logger.log("&eYou're using an item from Oraxen, but it's not loaded on the server! Context: " + context);
        }
        return item;
    }
}
