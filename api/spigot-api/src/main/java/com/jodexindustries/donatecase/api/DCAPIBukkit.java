package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.animation.JavaAnimationBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import com.jodexindustries.donatecase.api.gui.CaseGui;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public abstract class DCAPIBukkit implements DCAPI<Player, JavaAnimationBukkit, CaseDataMaterialBukkit,
        CaseGui, CaseGuiClickEvent, ItemStack, CommandSender, Location, CaseDataBukkit> {

    @Nullable
    private static Class<? extends DCAPIBukkit> clazz = null;

    @NotNull
    public static DCAPIBukkit get(Addon addon) {
        if(clazz == null) throw new IllegalArgumentException("DCAPI is not loaded!");
        try {
            return clazz.getDeclaredConstructor(Addon.class).newInstance(addon);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static DCAPIBukkit get(Plugin plugin) {
        if(clazz == null) throw new IllegalArgumentException("DCAPI is not loaded!");
        try {
            return clazz.getDeclaredConstructor(Plugin.class).newInstance(plugin);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @ApiStatus.Internal
    public static void register(final Class<? extends DCAPIBukkit> clazz) {
        DCAPIBukkit.clazz = clazz;
    }

    @ApiStatus.Internal
    public static void unregister() {
        DCAPIBukkit.clazz = null;
    }
}
