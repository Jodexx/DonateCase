package com.jodexindustries.donatecase.spigot.materials;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class BASE64MaterialHandlerImpl implements MaterialHandler {

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {

        try {
            return (ItemStack) DCAPI.getInstance().getPlatform().getTools().createSkullFromTexture(context);
        } catch (Exception e) {
            DCAPI.getInstance().getPlatform().getLogger().log(Level.WARNING, "Error with handling item: " + context, e);
        }

        return new ItemStack(Material.AIR);
    }

}
