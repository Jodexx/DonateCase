package com.jodexindustries.donatecase.api.materials;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import com.jodexindustries.donatecase.tools.Logger;
import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.api.CustomHeadsAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CHMaterialHandlerImpl implements MaterialHandler {

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {
        ItemStack item = new ItemStack(Material.STONE);

        String[] parts = context.split(":");
        if(parts.length != 2) return item;

        if(Case.getInstance().getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
            CustomHeadsAPI api = CustomHeads.getApi();
            try {
                item = api.getHead(parts[0], Integer.parseInt(parts[1]));
            } catch (Exception nullPointerException) {
                Logger.log("&eCould not find the head you were looking for by CustomHeads support. Category: " + parts[0] + " Id: " + parts[1]);
            }
        } else {
            Logger.log("&eYou're using an item from CustomHeads, but it's not loaded on the server!");
        }
        return item;
    }
}
