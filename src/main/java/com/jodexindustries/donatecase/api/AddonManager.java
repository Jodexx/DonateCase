package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import com.jodexindustries.donatecase.api.events.AddonDisableEvent;
import com.jodexindustries.donatecase.api.events.AddonEnableEvent;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

/**
 * Class for managing add-ons, enabling and disabling.
 */
public class AddonManager {
    private static final Map<String, InternalJavaAddon> addons = new HashMap<>();

    /**
     * Load all addons from "addons" folder
     */
    public void loadAddons() {
        File addonsDir = new File(Case.getInstance().getDataFolder(), "addons");
        File[] files = addonsDir.listFiles();
        if(!addonsDir.exists()) {
            addonsDir.mkdir();
        }
        if(files != null) {
            for (File file : files) {
                loadAddon(file);
            }
        }
    }

    /**
     * Load specific addon
     * @param file addon jar file
     */
    public boolean loadAddon(File file) {
        if (file.isFile() && file.getName().endsWith(".jar")) {
            try (JarFile jarFile = new JarFile(file)) {
                JarEntry entry = jarFile.getJarEntry("addon.yml");
                if (entry != null) {
                    InputStream input = jarFile.getInputStream(entry);
                    Yaml yaml = new Yaml();
                    Map<String, Object> data = yaml.load(input);
                    String name = (String) data.get("name");
                    String mainClassName = (String) data.get("main");
                    String version = String.valueOf(data.get("version"));
                    Case.getInstance().getLogger().info("Loading " + name + " addon v" + version);
                    if(addons.get(name) != null) {
                        if(name.equalsIgnoreCase("DonateCase")) {
                            Case.getInstance().getLogger().warning("Addon " + file.getName() + " trying to load with DonateCase name! Abort.");
                            return false;
                        }
                        Case.getInstance().getLogger().warning("Addon with name " + name + " already loaded!");
                        return false;
                    }
                    URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader());
                    try {
                        Class<?> mainClass = Class.forName(mainClassName, true, loader);
                        InternalJavaAddon addon = (InternalJavaAddon) mainClass.getDeclaredConstructor().newInstance();
                        addon.init(version, name, file, loader);
                        addons.put(name, addon);
                        enableAddon(addon);
                        return true;
                    } catch (Throwable e) {
                        addons.remove(name);
                        closeClassLoader(loader);
                        if(e.getCause() instanceof ClassNotFoundException) {
                            ClassNotFoundException error = (ClassNotFoundException) e.getCause();
                            if(error.getLocalizedMessage().contains("JavaAddon")) {
                                Case.getInstance().getLogger().log(Level.SEVERE,
                                        "Error occurred while enabling addon " + name + " v" + version +
                                                "\nIncompatible DonateCaseAPI! Contact with developer or update addon!", e);
                                return false;
                            }
                        }
                        Case.getInstance().getLogger().log(Level.SEVERE,
                                "Error occurred while enabling addon " + name + " v" + version, e);
                    }
                } else {
                    Case.getInstance().getLogger().warning("Addon " + file.getName() + " trying to load without addon.yml! Abort.");
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        enableAddon(javaInternalAddon);
    }

    /**
     * Enable addon by instance
     * @param addon addon instance
     */
    public void enableAddon(@NotNull InternalJavaAddon addon) {
        if(!addon.isEnabled()) {
            Case.getInstance().getLogger().info("Enabling " + addon.getName() + " addon v" + addon.getVersion());
            addon.setEnabled(true);

            AddonEnableEvent addonEnableEvent = new AddonEnableEvent(addon);
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
     * Disable addon by instance
     * @param addon addon instance
     */
    public void disableAddon(@NotNull InternalJavaAddon addon) {
        if(addon.isEnabled()) {
            Case.getInstance().getLogger().info("Disabling " + addon.getName() + " addon v" + addon.getVersion());
            addon.setEnabled(false);
            AddonDisableEvent addonDisableEvent = new AddonDisableEvent(addon);
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
     */
    public boolean unloadAddon(@NotNull InternalJavaAddon addon) {
        try {
            disableAddon(addon);
            addons.remove(addon.getName());
            closeClassLoader(addon.getUrlClassLoader());
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

    private boolean closeClassLoader(URLClassLoader loader) {
        try {
            loader.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
