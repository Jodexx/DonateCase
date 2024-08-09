package com.jodexindustries.donatecase.api.materials;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import com.jodexindustries.donatecase.tools.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class IAMaterialHandlerImpl implements MaterialHandler {

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {
        ItemStack item = new ItemStack(Material.STONE);

        if (DonateCase.instance.itemsAdderSupport != null) {
            item = DonateCase.instance.itemsAdderSupport.getItem(context);
        } else {
            Logger.log("&eYou're using an item from ItemsAdder, but it's not loaded on the server!");
        }
        return item;
    }
}
