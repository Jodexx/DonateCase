package com.jodexindustries.donatecase.impl.managers;

import com.jodexindustries.donatecase.BuildConstants;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.addon.PowerReason;
import com.jodexindustries.donatecase.api.addon.external.ExternalJavaAddon;
import com.jodexindustries.donatecase.api.addon.internal.InternalAddonClassLoader;
import com.jodexindustries.donatecase.api.addon.internal.InternalAddonDescription;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import com.jodexindustries.donatecase.api.addon.internal.InvalidAddonException;
import com.jodexindustries.donatecase.api.events.AddonDisableEvent;
import com.jodexindustries.donatecase.api.events.AddonEnableEvent;
import com.jodexindustries.donatecase.api.manager.AddonManager;
import com.jodexindustries.donatecase.api.tools.DCTools;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import static com.jodexindustries.donatecase.DonateCase.instance;
import static com.jodexindustries.donatecase.api.tools.DCTools.getPluginVersion;

/**
 * Class for managing add-ons, enabling and disabling.
 */
public class AddonManagerImpl implements AddonManager {

    /**
     * Map of all loaded addons
     */
    private static final Map<String, InternalJavaAddon> addons = new HashMap<>();
    /**
     * List of all addons loaders
     */
    public static final List<InternalAddonClassLoader> loaders = new CopyOnWriteArrayList<>();

    private final Addon donateCase = new ExternalJavaAddon(instance);
    private final Addon addon;

    private final File addonsFolder;

    /**
     * Default constructor
     *
     * @param addon An addon that will manage other addons
     */
    public AddonManagerImpl(Addon addon) {
        this.addon = addon;
        this.addonsFolder = new File(donateCase.getDataFolder(), "addons");
    }

    @Override
    public void loadAddons() {
        File addonsDir = getAddonsFolder();
        File[] files = addonsDir.listFiles();
        if (!addonsDir.exists()) {
            addonsDir.mkdir();
        }
        if (files != null) {
            for (File file : files) {
                loadAddon(file);
            }
        }
    }

    @Override
    public boolean loadAddon(File file) {
        if (file.isFile() && file.getName().endsWith(".jar")) {
            InternalAddonDescription description;
            try {
                description = new InternalAddonDescription(file);
            } catch (IOException | InvalidAddonException e) {
                throw new RuntimeException(e);
            }

            addon.getLogger().info("Loading " + description.getName() + " addon v" + description.getVersion());
            if (addons.get(description.getName()) != null) {
                if (description.getName().equalsIgnoreCase("DonateCase")) {
                    addon.getLogger().warning("Addon " + file.getName() + " trying to load with DonateCase name! Abort.");
                    return false;
                }
                addon.getLogger().warning("Addon with name " + description.getName() + " already loaded!");
                return false;
            }

            if (description.getApiVersion() != null) {
                int addonVersion = DCTools.getPluginVersion(description.getApiVersion());
                int pluginVersion = DCTools.getPluginVersion(BuildConstants.api);

                if (pluginVersion < addonVersion) {
                    addon.getLogger().warning("Addon " + description.getName() + " API version (" + description.getApiVersion()
                            + ") incompatible with current API version (" + BuildConstants.api + ")! Abort.");
                    return false;
                }
            }

            InternalAddonClassLoader loader;
            try {
                loader = new InternalAddonClassLoader(addon.getClass().getClassLoader(), description, file, this, donateCase);
            } catch (Throwable t) {
                addon.getLogger().log(Level.SEVERE,
                        "Error occurred while loading addon " + description.getName() + " v" + description.getVersion(), t);
                return false;
            }
            try {
                InternalJavaAddon addon = loader.getAddon();
                addons.put(description.getName(), addon);
                loaders.add(loader);
                addon.onLoad();
                return true;
            } catch (Throwable e) {
                addons.remove(description.getName());
                try {
                    loader.close();
                } catch (IOException ex) {
                    addon.getLogger().log(Level.SEVERE, e.getLocalizedMessage(), e.getCause());
                }
                if (e.getCause() instanceof ClassNotFoundException) {
                    ClassNotFoundException error = (ClassNotFoundException) e.getCause();
                    if (error.getLocalizedMessage().contains("JavaAddon")) {
                        addon.getLogger().log(Level.SEVERE,
                                "Error occurred while enabling addon " + description.getName() + " v" + description.getVersion() +
                                        "\nIncompatible DonateCaseAPI! Contact with developer or update addon!", e);
                        return false;
                    }
                }
                addon.getLogger().log(Level.SEVERE,
                        "Error occurred while enabling addon " + description.getName() + " v" + description.getVersion(), e);
            }
        }
        return false;
    }

    @Override
    public void enableAddons(PowerReason reason) {
        Collection<InternalJavaAddon> list = addons.values();
        for (InternalJavaAddon internalJavaAddon : list) {
            enableAddon(internalJavaAddon, reason);
        }
    }

    @Override
    public boolean enableAddon(@NotNull String addon, PowerReason reason) {
        InternalJavaAddon javaInternalAddon = addons.get(addon);
        if (javaInternalAddon == null) return false;
        return enableAddon(javaInternalAddon, reason);
    }

    @Override
    public boolean enableAddon(@NotNull InternalJavaAddon addon, PowerReason reason) {
        try {
            if (!addon.isEnabled()) {
                addon.getLogger().info("Enabling " + addon.getName() + " addon v" + addon.getVersion());
                addon.setEnabled(true);

                AddonEnableEvent addonEnableEvent = new AddonEnableEvent(addon, this.addon, reason);
                Bukkit.getPluginManager().callEvent(addonEnableEvent);
                return true;
            }
        } catch (Throwable t) {
            addon.getLogger().log(Level.SEVERE,
                    "Error occurred while enabling addon " + addon.getName() + " v" + addon.getVersion(), t);
        }
        return false;
    }

    @Override
    public boolean disableAddon(@NotNull String addon, PowerReason reason) {
        InternalJavaAddon javaInternalAddon = addons.get(addon);
        if (javaInternalAddon == null) return false;
        return disableAddon(javaInternalAddon, reason);
    }

    @Override
    public boolean disableAddon(@NotNull InternalJavaAddon addon, PowerReason reason) {
        try {
            if (addon.isEnabled()) {
                addon.getLogger().info("Disabling " + addon.getName() + " addon v" + addon.getVersion());
                addon.setEnabled(false);
                AddonDisableEvent addonDisableEvent = new AddonDisableEvent(addon, this.addon, reason);
                Bukkit.getPluginManager().callEvent(addonDisableEvent);
                return true;
            }
        } catch (Throwable t) {
            addon.getLogger().log(Level.SEVERE,
                    "Error occurred while disabling addon " + addon.getName() + " v" + addon.getVersion(), t);
        }
        return false;
    }

    @Override
    public void unloadAddons(PowerReason reason) {
        List<InternalJavaAddon> list = new ArrayList<>(addons.values());
        for (InternalJavaAddon internalJavaAddon : list) {
            unloadAddon(internalJavaAddon, reason);
        }
        addons.clear();
    }

    @Override
    public boolean unloadAddon(@NotNull String addon, PowerReason reason) {
        InternalJavaAddon javaInternalAddon = addons.get(addon);
        if (javaInternalAddon == null) return false;
        return unloadAddon(javaInternalAddon, reason);
    }

    @Override
    public boolean unloadAddon(@NotNull InternalJavaAddon addon, PowerReason reason) {
        try {
            disableAddon(addon, reason);
            addons.remove(addon.getName());
            loaders.remove(addon.getUrlClassLoader());
            addon.getUrlClassLoader().close();
            return true;
        } catch (Throwable e) {
            addon.getLogger().log(Level.SEVERE, e.getLocalizedMessage(), e.getCause());
        }
        return false;
    }

    @Nullable
    @Override
    public InternalJavaAddon getAddon(String addon) {
        return addons.get(addon);
    }

    @Override
    public @NotNull Map<String, InternalJavaAddon> getAddons() {
        return addons;
    }

    /**
     * Gets "addons" folder
     *
     * @return The folder
     */
    @NotNull
    public File getAddonsFolder() {
        return addonsFolder;
    }

    /**
     * Gets InternalAddonClassLoader
     *
     * @param file Addon jar file
     * @return InternalAddon ClassLoader
     */
    @Nullable
    public static InternalAddonClassLoader getAddonClassLoader(File file) {
        return loaders.stream().filter(loader -> loader.getFile().equals(file)).findFirst().orElse(null);
    }


    /**
     * Gets main addon class loader
     *
     * @param name    The binary name of the class
     * @param resolve If {@code true} then resolve the class
     * @return The resulting {@code Class} object
     */
    @Nullable
    @Override
    public Class<?> getClassByName(String name, boolean resolve) {
        for (InternalAddonClassLoader loader : loaders) {
            try {
                return loader.loadClass0(name, resolve, false);
            } catch (ClassNotFoundException ignore) {
            }
        }
        return null;
    }

}