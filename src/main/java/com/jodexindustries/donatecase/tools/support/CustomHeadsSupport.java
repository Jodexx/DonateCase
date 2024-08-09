package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.tools.Logger;
import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.api.CustomHeadsAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CustomHeadsSupport {

    public CustomHeadsSupport() {
        Logger.log("&aHooked to &bCustomHeads");
    }

    @NotNull
    public ItemStack getSkull(String category, String id) {
        ItemStack item = new ItemStack(Material.STONE);

        CustomHeadsAPI api = CustomHeads.getApi();
        try {
            item = api.getHead(category, Integer.parseInt(id));
        } catch (Exception nullPointerException) {
            Logger.log("&eCould not find the head you were looking for by CustomHeads support. Category: " + category + " Id: " + id);
        }

        return item;
    }
}
