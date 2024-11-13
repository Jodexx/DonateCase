package com.jodexindustries.donatecase.api.addon.internal;

import com.jodexindustries.donatecase.api.addon.Addon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;

public interface InternalAddon extends Addon {

    /**
     * Called when this plugin is disabled
     */
    void onDisable();

    /**
     * Called when this addon is enabled
     */
    void onEnable();

    /**
     * Called after an addon is loaded but before it has been enabled.
     * <p>
     * When multiple addons are loaded, the onLoad() for all addons is
     * called before any onEnable() is called.
     */
    void onLoad();

    /**
     * Saves the raw contents of any resource embedded with an addon's .jar
     * file assuming it can be found using {@link #getResource(String)}.
     * <p>
     * The resource is saved into the addon's data folder using the same
     * hierarchy as the .jar file (subdirectories are preserved).
     *
     * @param resourcePath the embedded resource path to look for within the
     *     addon's .jar file. (No preceding slash).
     * @param replace if true, the embedded resource will overwrite the
     *     contents of an existing file.
     * @throws IllegalArgumentException if the resource path is null, empty,
     *     or points to a nonexistent resource.
     */
    void saveResource(@NotNull String resourcePath, boolean replace);

    /**
     * Gets an embedded resource in this addon
     *
     * @param filename Filename of the resource
     * @return File if found, otherwise null
     */
    @Nullable
    InputStream getResource(@NotNull String filename);

    /**
     * Returns the addon.yml file containing the details for this addon
     *
     * @return Contents of the addon.yml file
     */
    @NotNull
    InternalAddonDescription getDescription();

    @NotNull
    Addon getDonateCase();
}
