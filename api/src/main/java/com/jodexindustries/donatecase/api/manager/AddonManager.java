package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.addon.PowerReason;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Interface for managing addons, including loading, enabling, disabling, and unloading addons.
 * Addons can be managed by name, file, or instance.
 */
public interface AddonManager {

    /**
     * Loads all addons from the "addons" folder.
     */
    void loadAddons();

    /**
     * Loads a specific addon from a given file.
     *
     * @param file the addon jar file to load
     * @return true if the addon was successfully loaded, false otherwise
     */
    boolean loadAddon(File file);

    /**
     * Enables all loaded addons, specifying a reason for enabling.
     *
     * @param reason the reason for enabling the addons
     */
    void enableAddons(PowerReason reason);

    /**
     * Enables a specific addon by name, with a specified reason.
     *
     * @param addon  the name of the addon to enable
     * @param reason the reason for enabling the addon
     * @return true if the addon was successfully enabled, false otherwise
     */
    boolean enableAddon(@NotNull String addon, PowerReason reason);

    /**
     * Enables a specific addon by instance, with a specified reason.
     *
     * @param addon  the instance of the addon to enable
     * @param reason the reason for enabling the addon
     * @return true if the addon was successfully enabled, false otherwise
     */
    boolean enableAddon(@NotNull InternalJavaAddon addon, PowerReason reason);

    /**
     * Disables a specific addon by name, with a specified reason.
     *
     * @param addon  the name of the addon to disable
     * @param reason the reason for disabling the addon
     * @return true if the addon was successfully disabled, false otherwise
     */
    boolean disableAddon(@NotNull String addon, PowerReason reason);

    /**
     * Disables a specific addon by instance, with a specified reason.
     *
     * @param addon  the instance of the addon to disable
     * @param reason the reason for disabling the addon
     * @return true if the addon was successfully disabled, false otherwise
     */
    boolean disableAddon(@NotNull InternalJavaAddon addon, PowerReason reason);

    /**
     * Unloads all loaded addons, specifying a reason for unloading.
     *
     * @param reason the reason for unloading the addons
     */
    void unloadAddons(PowerReason reason);

    /**
     * Unloads a specific addon by name, with a specified reason.
     *
     * @param addon  the name of the addon to unload
     * @param reason the reason for unloading the addon
     * @return true if the addon was successfully unloaded, false otherwise
     */
    boolean unloadAddon(@NotNull String addon, PowerReason reason);

    /**
     * Unloads a specific addon by instance, with a specified reason.
     *
     * @param addon  the instance of the addon to unload
     * @param reason the reason for unloading the addon
     * @return true if the addon was successfully unloaded, false otherwise
     */
    boolean unloadAddon(@NotNull InternalJavaAddon addon, PowerReason reason);

    /**
     * Retrieves an addon by its name.
     *
     * @param addon the name of the addon to retrieve
     * @return the addon instance if found, null otherwise
     */
    @Nullable
    InternalJavaAddon getAddon(String addon);

    /**
     * Retrieves the main class of an addon by its binary name.
     *
     * @param name    the binary name of the class
     * @param resolve if true, resolves the class
     * @return the resulting {@code Class} object if found, null otherwise
     */
    @Nullable
    Class<?> getClassByName(String name, boolean resolve);
}
