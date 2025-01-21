package com.jodexindustries.donatecase.api.addon.internal;

import com.jodexindustries.donatecase.api.platform.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.logging.Level;

/**
 * Abstract class for JavaAddon realization, like BukkitPlugin
 */
public abstract class InternalJavaAddon implements InternalAddon {

    private boolean isEnabled = false;

    private ClassLoader classLoader;
    private InternalAddonLogger internalAddonLogger;
    private File file;
    private InternalAddonClassLoader urlClassLoader;
    private InternalAddonDescription description;
    private Platform platform;

    public InternalJavaAddon() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        if (classLoader instanceof InternalAddonClassLoader) {
            ((InternalAddonClassLoader) classLoader).initialize(this);
        } else {
            throw new IllegalArgumentException("InternalJavaAddon requires " + InternalAddonClassLoader.class.getName());
        }
    }

    void init(InternalAddonDescription description, File file, InternalAddonClassLoader loader, Platform platform) {
        this.description = description;
        this.file = file;
        this.classLoader = this.getClass().getClassLoader();
        this.urlClassLoader = loader;
        this.platform = platform;
        this.internalAddonLogger = new InternalAddonLogger(this);
    }

    /**
     * Sets the enabled state of this addon
     *
     * @param enabled true if enabled, otherwise false
     */
    public final void setEnabled(final boolean enabled) {
        if (isEnabled != enabled) {
            isEnabled = enabled;

            if (isEnabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onLoad() {
    }

    @Override
    public final @NotNull File getDataFolder() {
        File data = new File(getPlatform().getDataFolder(), "addons/" + getDescription().getName());
        if (!data.exists()) data.mkdir();
        return data;
    }

    @Override
    public final String getVersion() {
        return getDescription().getVersion();
    }

    @Override
    public final @NotNull String getName() {
        return getDescription().getName();
    }

    @Override
    public final void saveResource(@NotNull String resourcePath, boolean replace) {
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
                OutputStream out = Files.newOutputStream(outFile.toPath());
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
    public final InputStream getResource(@NotNull String filename) {

        try {
            URL url = getUrlClassLoader().getResource(filename);

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

    public final ClassLoader getClassLoader() {
        return classLoader;
    }

    public final @NotNull InternalAddonLogger getLogger() {
        return internalAddonLogger;
    }

    public final InternalAddonClassLoader getUrlClassLoader() {
        return urlClassLoader;
    }

    @Override
    public final @NotNull InternalAddonDescription getDescription() {
        return description;
    }

    @Override
    public final @NotNull Platform getPlatform() {
        return platform;
    }
}
