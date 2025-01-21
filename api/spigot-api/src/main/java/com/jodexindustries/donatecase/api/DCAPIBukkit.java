package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.manager.HologramManager;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

/**
 * Abstract class representing the API interface for the DonateCase system in a Bukkit environment.
 * This class provides methods for interacting with the various components of the plugin.
 */
public abstract class DCAPIBukkit extends DCAPI {

    @Nullable
    private static Class<? extends DCAPIBukkit> clazz = null;

    /**
     * Retrieves an instance of DCAPIBukkit using the provided Addon.
     *
     * @param addon The Addon associated with the DCAPI instance.
     * @return The DCAPIBukkit instance.
     * @throws IllegalArgumentException If DCAPI is not loaded.
     * @throws RuntimeException If an error occurs during instantiation.
     */
    @NotNull
    public static DCAPIBukkit get(Addon addon) {
        if(clazz == null) throw new IllegalArgumentException("DCAPI is not loaded!");
        try {
            return clazz.getDeclaredConstructor(Addon.class).newInstance(addon);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves an instance of DCAPIBukkit using the provided Plugin.
     *
     * @param plugin The Plugin associated with the DCAPI instance.
     * @return The DCAPIBukkit instance.
     * @throws IllegalArgumentException If DCAPI is not loaded.
     * @throws RuntimeException If an error occurs during instantiation.
     */
    @NotNull
    public static DCAPIBukkit get(Plugin plugin) {
        if(clazz == null) throw new IllegalArgumentException("DCAPI is not loaded!");
        try {
            return clazz.getDeclaredConstructor(Plugin.class).newInstance(plugin);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers a class that extends DCAPIBukkit for use with the API.
     *
     * @param clazz The class extending DCAPIBukkit to register.
     */
    @ApiStatus.Internal
    public static void register(final Class<? extends DCAPIBukkit> clazz) {
        DCAPIBukkit.clazz = clazz;
    }

    /**
     * Unregisters the current DCAPIBukkit class, effectively invalidating the API instance.
     */
    @ApiStatus.Internal
    public static void unregister() {
        DCAPIBukkit.clazz = null;
    }

    /**
     * Abstract method that should return the Plugin instance associated with DonateCase.
     *
     * @return The Plugin instance.
     */
    @NotNull
    public abstract Plugin getDonateCase();

    @Nullable
    public abstract HologramManager getHologramManager();

}
