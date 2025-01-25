package com.jodexindustries.donatecase.impl.materials;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.material.MaterialHandler;
import com.jodexindustries.donatecase.tools.SkullCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class BASE64MaterialHandlerImpl implements MaterialHandler {

    @Override
    public @NotNull ItemStack handle(@NotNull String context) {
        try {
            return SkullCreator.itemFromBase64(context);
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            DCAPI.getInstance().getPlatform().getLogger().log(Level.WARNING, "Error with handling item: " + context, e);
        }

        return new ItemStack(Material.AIR);
    }

}
