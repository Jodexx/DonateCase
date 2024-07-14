package com.jodexindustries.donatecase.api.addon.internal;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.CaseManager;
import org.bukkit.plugin.Plugin;
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
    private CaseManager caseAPI;
    private InternalAddonDescription description;

    public InternalJavaAddon() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        if (classLoader instanceof InternalAddonClassLoader) {
            ((InternalAddonClassLoader) classLoader).initialize(this);
        } else {
            throw new IllegalArgumentException("InternalJavaAddon requires " + InternalAddonClassLoader.class.getName());
        }
    }

    void init(InternalAddonDescription description, File file, InternalAddonClassLoader loader) {
        this.description = description;
        this.file = file;
        this.classLoader = this.getClass().getClassLoader();
        this.urlClassLoader = loader;
        this.internalAddonLogger = new InternalAddonLogger(this);
        this.caseAPI = new CaseManager(this);
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
    public void onDisable() {}

    @Override
    public void onEnable() {}

    @Override
    public CaseManager getCaseAPI() {
        return this.caseAPI;
    }

    @Override
    public Plugin getDonateCase() {
        return Case.getInstance();
    }
    @Override
    public @NotNull File getDataFolder() {
        File data = new File(getDonateCase().getDataFolder(), "addons/" + getDescription().getName());
        if(!data.exists()) {
            data.mkdir();
        }
        return data;
    }
    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }
    @Override
    public @NotNull String getName() {
        return getDescription().getName();
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

    public @NotNull InternalAddonLogger getLogger() {
        return internalAddonLogger;
    }

    public InternalAddonClassLoader getUrlClassLoader() {
        return urlClassLoader;
    }

    public InternalAddonDescription getDescription() {
        return description;
    }
}
