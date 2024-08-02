package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.addon.internal.InternalAddonClassLoader;
import com.jodexindustries.donatecase.api.addon.internal.InternalAddonDescription;
import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import com.jodexindustries.donatecase.api.addon.internal.InvalidAddonException;
import com.jodexindustries.donatecase.api.events.AddonDisableEvent;
import com.jodexindustries.donatecase.api.events.AddonEnableEvent;
import com.alessiodp.libby.Library;
import com.alessiodp.libby.LibraryManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

/**
 * Class for managing add-ons, enabling and disabling.
 */
public class AddonManager {

    private static final Map<String, InternalJavaAddon> addons = new HashMap<>();
    private static final List<InternalAddonClassLoader> loaders = new CopyOnWriteArrayList<>();
    private final Addon addon;

    /**
     * Default constructor
     * @param addon An addon that will manage other addons
     */
    public AddonManager(Addon addon) {
        this.addon = addon;
    }

    /**
     * Load all addons from "addons" folder
     */
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

    /**
     * Load specific addon
     * @param file addon jar file
     * @return true, if successful
     */
    public boolean loadAddon(File file) {
        if (file.isFile() && file.getName().endsWith(".jar")) {
            InternalAddonDescription description;
            try {
                description = new InternalAddonDescription(file);
            } catch (IOException | InvalidAddonException e) {
                throw new RuntimeException(e);
            }

            Case.getInstance().getLogger().info("Loading " + description.getName() + " addon v" + description.getVersion());
            if (addons.get(description.getName()) != null) {
                if (description.getName().equalsIgnoreCase("DonateCase")) {
                    Case.getInstance().getLogger().warning("Addon " + file.getName() + " trying to load with DonateCase name! Abort.");
                    return false;
                }
                Case.getInstance().getLogger().warning("Addon with name " + description.getName() + " already loaded!");
                return false;
            }

            loadLibraries(description.getLibraries());
            InternalAddonClassLoader loader;
            try {
                loader = new InternalAddonClassLoader(Case.getInstance().getClass().getClassLoader(), description, file, this);
            } catch (Throwable t) {
                Case.getInstance().getLogger().log(Level.SEVERE,
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
                    Case.getInstance().getLogger().log(Level.SEVERE, e.getLocalizedMessage(), e.getCause());
                }
                if (e.getCause() instanceof ClassNotFoundException) {
                    ClassNotFoundException error = (ClassNotFoundException) e.getCause();
                    if (error.getLocalizedMessage().contains("JavaAddon")) {
                        Case.getInstance().getLogger().log(Level.SEVERE,
                                "Error occurred while enabling addon " + description.getName() + " v" + description.getVersion() +
                                        "\nIncompatible DonateCaseAPI! Contact with developer or update addon!", e);
                        return false;
                    }
                }
                Case.getInstance().getLogger().log(Level.SEVERE,
                        "Error occurred while enabling addon " + description.getName() + " v" + description.getVersion(), e);
            }
        }
        return false;
    }

    /**
     * Enable all loaded addons
     */
    public void enableAddons() {
        addons.values().forEach(addon -> enableAddon(addon, PowerReason.DONATE_CASE));
    }

    /**
     * Enable addon by name
     * @param addon addon name
     */
    @Deprecated
    public boolean enableAddon(@NotNull String addon) {
        return enableAddon(addon, PowerReason.DONATE_CASE);
    }

    /**
     * Enable addon by name with reason
     * @param addon addon name
     * @param reason Enable reason
     */
    public boolean enableAddon(@NotNull String addon, PowerReason reason) {
        InternalJavaAddon javaInternalAddon = addons.get(addon);
        if(javaInternalAddon == null) return false;
        return enableAddon(javaInternalAddon, reason);
    }

    /**
     * Enable addon by instance
     * @param addon addon name
     */
    @Deprecated
    public boolean enableAddon(@NotNull InternalJavaAddon addon) {
        return enableAddon(addon, PowerReason.DONATE_CASE);
    }

    /**
     * Enable addon by instance with reason
     * @param addon addon instance
     * @param reason Enable reason
     */
    public boolean enableAddon(@NotNull InternalJavaAddon addon, PowerReason reason) {
        try {
            if (!addon.isEnabled()) {
                Case.getInstance().getLogger().info("Enabling " + addon.getName() + " addon v" + addon.getVersion());
                addon.setEnabled(true);

                AddonEnableEvent addonEnableEvent = new AddonEnableEvent(addon, this.addon, reason);
                Bukkit.getPluginManager().callEvent(addonEnableEvent);
                return true;
            }
        } catch (Throwable t) {
            Case.getInstance().getLogger().log(Level.SEVERE,
                    "Error occurred while enabling addon " + addon.getName() + " v" + addon.getVersion(), t);
        }
        return false;
    }

    /**
     * Disable addon by name
     * @param addon addon name
     */
    @Deprecated
    public boolean disableAddon(@NotNull String addon) {
        return disableAddon(addon, PowerReason.ADDON);
    }

    /**
     * Disable addon by name with reason
     * @param addon addon name
     * @param reason Disable reason
     */
    public boolean disableAddon(@NotNull String addon, PowerReason reason) {
        InternalJavaAddon javaInternalAddon = addons.get(addon);
        if (javaInternalAddon == null) return false;
        return disableAddon(javaInternalAddon, reason);
    }

    /**
     * Disable addon by instance
     * @param addon addon instance
     */
    @Deprecated
    public boolean disableAddon(@NotNull InternalJavaAddon addon) {
        return disableAddon(addon, PowerReason.DONATE_CASE);
    }

    /**
     * Disable addon by instance with reason
     * @param addon addon instance
     * @param reason Disable reason
     */
    public boolean disableAddon(@NotNull InternalJavaAddon addon, PowerReason reason) {
        try {
            if (addon.isEnabled()) {
                Case.getInstance().getLogger().info("Disabling " + addon.getName() + " addon v" + addon.getVersion());
                addon.setEnabled(false);
                AddonDisableEvent addonDisableEvent = new AddonDisableEvent(addon, this.addon, reason);
                Bukkit.getPluginManager().callEvent(addonDisableEvent);
                return true;
            }
        } catch (Throwable t) {
            Case.getInstance().getLogger().log(Level.SEVERE,
                    "Error occurred while disabling addon " + addon.getName() + " v" + addon.getVersion(), t);
        }
        return false;
    }

    /**
     * Unload all loaded addons with reason
     * @param reason Unload reason
     */
    public void unloadAddons(PowerReason reason) {
        List<InternalJavaAddon> list = new ArrayList<>(addons.values());
        list.forEach(addon -> unloadAddon(addon, reason));
        addons.clear();
    }

    /**
     * Unload all loaded addons
     */
    @Deprecated
    public void unloadAddons() {
        unloadAddons(PowerReason.ADDON);
    }

    /**
     * Unload addon by name
     * @param addon addon name
     * @return true, if successful
     */
    @Deprecated
    public boolean unloadAddon(@NotNull String addon) {
        return unloadAddon(addon, PowerReason.ADDON);
    }

    /**
     * Unload addon by name with reason
     * @param addon addon name
     * @param reason Unload reason
     * @return true, if successful
     */
    public boolean unloadAddon(@NotNull String addon, PowerReason reason) {
        InternalJavaAddon javaInternalAddon = addons.get(addon);
        if(javaInternalAddon == null) return false;
        return unloadAddon(javaInternalAddon, reason);
    }

    /**
     * Unload addon by instance
     * @param addon addon instance
     * @return true, if successful
     */
    @Deprecated
    public boolean unloadAddon(@NotNull InternalJavaAddon addon) {
        return unloadAddon(addon, PowerReason.ADDON);
    }

    /**
     * Unload addon by instance
     * @param addon addon instance
     * @return true, if successful
     */
    public boolean unloadAddon(@NotNull InternalJavaAddon addon, PowerReason reason) {
        try {
            disableAddon(addon, reason);
            addons.remove(addon.getName());
            loaders.remove(addon.getUrlClassLoader());
            addon.getUrlClassLoader().close();
            return true;
        } catch (Throwable e) {
            Case.getInstance().getLogger().log(Level.SEVERE, e.getLocalizedMessage(), e.getCause());
        }
        return false;
    }

    /**
     * Get addon by name
     * @param addon addon name
     * @return addon
     */
    @Nullable
    public static InternalJavaAddon getAddon(String addon) {
        return addons.get(addon);
    }

    public static File getAddonsFolder() {
        return new File(Case.getInstance().getDataFolder(), "addons");
    }

    @Nullable
    public static InternalAddonClassLoader getAddonClassLoader(File file) {
        return loaders.stream().filter(loader -> loader.getFile().equals(file)).findFirst().orElse(null);
    }

    public static Collection<InternalJavaAddon> getAddons() {
        return addons.values();
    }

    private void loadLibraries(List<String> libraries) {
        LibraryManager manager = Case.getInstance().libraryManager;
        for (String lib : libraries) {
            String[] params = lib.split(":");
            if(params.length == 3) {
                String groupId = params[0];
                String artifactId = params[1];
                String version = params[2];
                Library library = Library.builder()
                        .groupId(groupId)
                        .artifactId(artifactId)
                        .version(version)
                        .build();
                try {
                    manager.loadLibrary(library);
                } catch (RuntimeException e) {
                    Case.getInstance().getLogger().log(Level.WARNING, "Error with loading library " + lib, e);
                }
            }
        }
    }

    @Nullable
    public Class<?> getClassByName(String name, boolean resolve) {
        for (InternalAddonClassLoader loader : loaders) {
            try {
                return loader.loadClass0(name, resolve, false);
            } catch (ClassNotFoundException ignore) {}
        }
        return null;
    }

    public enum PowerReason {
        DONATE_CASE,
        ADDON
    }
}
