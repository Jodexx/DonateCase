package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.DonateCase;
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
    private static final  List<InternalAddonClassLoader> loaders = new CopyOnWriteArrayList<>();
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
        loadAddons(PowerReason.DONATE_CASE);
    }

    /**
     * Load all addons from "addons" folder with reason
     */
    public void loadAddons(PowerReason reason) {
        File addonsDir = new File(Case.getInstance().getDataFolder(), "addons");
        File[] files = addonsDir.listFiles();
        if(!addonsDir.exists()) {
            addonsDir.mkdir();
        }
        if(files != null) {
            for (File file : files) {
                loadAddon(file, reason);
            }
        }
    }

    /**
     * Load specific addon
     * @param file addon jar file
     * @return true, if successful
     */
    public boolean loadAddon(File file) {
        return loadAddon(file, PowerReason.DONATE_CASE);
    }

    /**
     * Load specific addon with reason
     * @param file addon jar file
     * @param reason Load reason
     * @return true, if successful
     */
    public boolean loadAddon(File file, PowerReason reason) {
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
                loader = new InternalAddonClassLoader(DonateCase.instance.getClass().getClassLoader(), description, file, this);
            } catch (IOException | InvalidAddonException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                InternalJavaAddon addon = loader.getAddon();
                addons.put(description.getName(), addon);
                loaders.add(loader);
                enableAddon(addon, reason);
                return true;
            } catch (Throwable e) {
                addons.remove(description.getName());
                try {
                    loader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
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
     * Enable addon by name
     * @param addon addon name
     */
    public void enableAddon(@NotNull String addon) {
        InternalJavaAddon javaInternalAddon = addons.get(addon);
        enableAddon(javaInternalAddon, PowerReason.DONATE_CASE);
    }

    /**
     * Enable addon by name with reason
     * @param addon addon name
     * @param reason Enable reason
     */
    public void enableAddon(@NotNull String addon, PowerReason reason) {
        InternalJavaAddon javaInternalAddon = addons.get(addon);
        enableAddon(javaInternalAddon, reason);
    }

    public void enableAddon(@NotNull InternalJavaAddon addon) {
        enableAddon(addon, PowerReason.DONATE_CASE);
    }

    /**
     * Enable addon by instance with reason
     * @param addon addon instance
     * @param reason Enable reason
     */
    public void enableAddon(@NotNull InternalJavaAddon addon, PowerReason reason) {
        if(!addon.isEnabled()) {
            Case.getInstance().getLogger().info("Enabling " + addon.getName() + " addon v" + addon.getVersion());
            addon.setEnabled(true);

            AddonEnableEvent addonEnableEvent = new AddonEnableEvent(addon, this.addon, reason);
            Bukkit.getPluginManager().callEvent(addonEnableEvent);
        } else {
            Case.getInstance().getLogger().warning("Addon with name " + addon.getName() + " already enabled!");
        }
    }

    /**
     * Disable addon by name
     * @param addon addon name
     */
    public void disableAddon(@NotNull String addon) {
        InternalJavaAddon javaInternalAddon = addons.get(addon);
        disableAddon(javaInternalAddon);
    }

    /**
     * Disable addon by name with reason
     * @param addon addon name
     * @param reason Disable reason
     */
    public void disableAddon(@NotNull String addon, PowerReason reason) {
        InternalJavaAddon javaInternalAddon = addons.get(addon);
        disableAddon(javaInternalAddon, reason);
    }

    /**
     * Disable addon by instance
     * @param addon addon instance
     */
    public void disableAddon(@NotNull InternalJavaAddon addon) {
        disableAddon(addon, PowerReason.DONATE_CASE);
    }

    /**
     * Disable addon by instance with reason
     * @param addon addon instance
     * @param reason Disable reason
     */
    public void disableAddon(@NotNull InternalJavaAddon addon, PowerReason reason) {
        if(addon.isEnabled()) {
            Case.getInstance().getLogger().info("Disabling " + addon.getName() + " addon v" + addon.getVersion());
            addon.setEnabled(false);
            AddonDisableEvent addonDisableEvent = new AddonDisableEvent(addon, this.addon, reason);
            Bukkit.getPluginManager().callEvent(addonDisableEvent);
        } else {
            Case.getInstance().getLogger().warning("Addon with name " + addon.getName() + " already disabled!");
        }
    }

    /**
     * Disable all loaded addons
     */
    public void unloadAddons() {
        List<String> list = new ArrayList<>(addons.keySet());
        for (String addon : list) {
            unloadAddon(addon);
        }
        addons.clear();
    }

    /**
     * Unload addon by name
     * @param addon addon name
     * @return true, if successful
     */
    public boolean unloadAddon(@NotNull String addon) {
        InternalJavaAddon javaInternalAddon = addons.get(addon);
        if(javaInternalAddon == null) {
            Case.getInstance().getLogger().warning("Addon " + addon + " already unloaded!");
            return false;
        } else {
            return unloadAddon(javaInternalAddon);
        }
    }

    /**
     * Unload addon by instance
     * @param addon addon instance
     * @return true, if successful
     */
    public boolean unloadAddon(@NotNull InternalJavaAddon addon) {
        try {
            disableAddon(addon);
            addons.remove(addon.getName());
            loaders.remove(addon.getUrlClassLoader());
            addon.getUrlClassLoader().close();
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get addon by name
     * @param addon addon name
     * @return addon
     */
    @Nullable
    public InternalJavaAddon getAddon(String addon) {
        return addons.get(addon);
    }

    public Collection<InternalJavaAddon> getAddons() {
        return addons.values();
    }

    private void loadLibraries(List<String> libraries) {
        LibraryManager manager = DonateCase.instance.libraryManager;
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
                    e.printStackTrace();
                }
            }
        }
    }

    @Nullable
    public Class<?> getClassByName(String name, boolean resolve) {
        for (InternalAddonClassLoader loader : loaders){
            try {
                return loader.loadClass0(name, resolve, false);
            }catch (ClassNotFoundException ignore){
            }
        }
        return null;
    }

    public enum PowerReason {
        DONATE_CASE,
        ADDON
    }
}
