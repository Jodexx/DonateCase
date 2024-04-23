package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.addon.JavaAddon;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Class for managing add-ons, enabling and disabling.
 */
public class AddonManager {
    private static final Map<String, JavaAddon> addons = new HashMap<>();
    private final Addon addon;
    public AddonManager(Addon addon) {
        this.addon = addon;
    }
    /**
     * Load all addons from "addons" folder
     */
    public void loadAddons() {
        File addonsDir = new File(addon.getDonateCase().getDataFolder(), "addons");
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
                        addon.getDonateCase().getLogger().warning("Addon with name " + name + " already loaded!");
                        return;
                    }
                    URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader());
                    Class<?> mainClass = Class.forName(mainClassName, true, loader);
                    addon.getDonateCase().getLogger().info("Loading " + name + " addon v" + version);
                    JavaAddon addon = (JavaAddon) mainClass.getDeclaredConstructor().newInstance();
                    addon.init(version, name, file, loader);
                    addons.put(file.getName(), addon);
                    addon.onEnable();
                }
            } catch (IOException | ClassNotFoundException | InvocationTargetException | InstantiationException |
                     IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Disable addon by name
     * @param addon addon name
     */
    public void disableAddon(String addon) {
        JavaAddon javaAddon = addons.get(addon);
        if(javaAddon == null) {
            this.addon.getDonateCase().getLogger().warning("Addon with name " + addon + " already disabled!");
        } else {
            try {
                javaAddon.onDisable();
                addons.remove(addon);
            } catch (Exception e) {
                e.printStackTrace();
                closeClassLoader(javaAddon.getUrlClassLoader());
            }
            closeClassLoader(javaAddon.getUrlClassLoader());
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
