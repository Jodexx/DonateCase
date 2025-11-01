package com.jodexindustries.donatecase.spigot.materials;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HEADMaterialHandlerImpl implements MaterialHandler {

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {
        return (ItemStack) DCAPI.getInstance().getPlatform().getTools().createSkullFromPlayer(context).join();
    }
}
