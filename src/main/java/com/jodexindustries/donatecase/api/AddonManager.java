package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.addon.internal.InternalJavaAddon;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        if(!addonsDir.exists()) {
            addonsDir.mkdir();
        }
        for (File file : addonsDir.listFiles()) {
            loadAddon(file);
        }
    }

    /**
     * Load specific addon
     * @param file addon jar file
     */
    public void loadAddon(File file) {
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
                    if(addons.get(name) != null) {
                        Case.getInstance().getLogger().warning("Addon with name " + name + " already loaded!");
                        return;
                    }
                    URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader());
                    try {
                        Class<?> mainClass = Class.forName(mainClassName, true, loader);
                        Case.getInstance().getLogger().info("Loading " + name + " addon v" + version);
                        InternalJavaAddon addon = (InternalJavaAddon) mainClass.getDeclaredConstructor().newInstance();
                        addon.init(version, name, file, loader);
                        addons.put(file.getName(), addon);
                        addon.onEnable();
                    } catch (Throwable e) {
                        if(e.getCause() instanceof ClassNotFoundException) {
                            ClassNotFoundException error = (ClassNotFoundException) e.getCause();
                            if(error.getLocalizedMessage().contains("JavaAddon")) {
                                Case.getInstance().getLogger().log(Level.SEVERE,
                                        "Error occurred while enabling addon " + name + " v" + version +
                                                "\nIncompatible DonateCaseAPI! Contact with developer or update addon!", e);
                                return;
                            }
                        }
                        Case.getInstance().getLogger().log(Level.SEVERE,
                                "Error occurred while enabling addon " + name + " v" + version, e);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Disable addon by name
     * @param addon addon name
     */
    public void disableAddon(String addon) {
        InternalJavaAddon javaInternalAddon = addons.get(addon);
        if(javaInternalAddon == null) {
            Case.getInstance().getLogger().warning("Addon with name " + addon + " already disabled!");
        } else {
            try {
                javaInternalAddon.onDisable();
                addons.remove(addon);
            } catch (Exception e) {
                e.printStackTrace();
                closeClassLoader(javaInternalAddon.getUrlClassLoader());
            }
            closeClassLoader(javaInternalAddon.getUrlClassLoader());
        }
    }
    private void closeClassLoader(URLClassLoader loader) {
        try {
            loader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disable all loaded addons
     */
    public void disableAddons() {
        List<String> list = new ArrayList<>(addons.keySet());
        for (String addon : list) {
            disableAddon(addon);
        }
        addons.clear();
    }
}
