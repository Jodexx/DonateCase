package com.jodexindustries.donatecase.api.addon;

import com.jodexindustries.donatecase.dc.Main;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Map;
import java.util.logging.Level;

public abstract class JavaAddon implements Addon {
    private String version;
    private String name;
    private ClassLoader classLoader;
    private AddonLogger addonLogger;
    private File file;
    private URLClassLoader urlClassLoader;

    public JavaAddon() {}

    public void init(String version, String name, File file, URLClassLoader loader) {
        this.version = version;
        this.name = name;
        this.file = file;
        this.classLoader = this.getClass().getClassLoader();
        this.urlClassLoader = loader;
        this.addonLogger = new AddonLogger(this);
    }


    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {}

    @Override
    public Plugin getDonateCase() {
        return Main.instance;
    }
    @Override
    public File getDataFolder() {
        File data = new File(getDonateCase().getDataFolder(), "addons/" + name);
        if(!data.exists()) {
            data.mkdir();
        }
        return data;
    }
    @Override
    public String getVersion() {
        return version;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public void saveResource(@NotNull String resourcePath, boolean replace) {
        if (resourcePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + file);
        }

        File outFile = new File(getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(getDataFolder(), resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }
    @Nullable
    @Override
    public InputStream getResource(@NotNull String filename) {

        try {
            URL url = getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public AddonLogger getLogger() {
        return addonLogger;
    }

    public static String getNameByClassLoader(ClassLoader classLoader) {
        InputStream input = classLoader.getResourceAsStream("addon.yml");
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(input);
        return (String) data.get("name");
    }

    public URLClassLoader getUrlClassLoader() {
        return urlClassLoader;
    }
}
