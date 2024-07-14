package com.jodexindustries.donatecase.api.addon.internal;

import com.jodexindustries.donatecase.api.AddonManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class InternalAddonClassLoader extends URLClassLoader {

    private final InternalAddonDescription description;
    private final File file;
    private final AddonManager manager;
    private final InternalJavaAddon addon;

    public InternalAddonClassLoader(@Nullable ClassLoader parent, InternalAddonDescription description,
                                    File file, AddonManager manager) throws IOException, InvalidAddonException, ClassNotFoundException {
        super(new URL[]{file.toURI().toURL()}, parent);

        this.description = description;
        this.file = file;
        this.manager = manager;

        try {
            Class<?> jarClass;
            try {
                jarClass = Class.forName(description.getMainClass(), true, this);
            } catch (ClassNotFoundException ex) {
                throw new ClassNotFoundException("Cannot find main class `" + description.getMainClass()  + "'", ex);
            }

            Class<? extends InternalJavaAddon> pluginClass;

            try {
                pluginClass = jarClass.asSubclass(InternalJavaAddon.class);
            } catch (ClassCastException ex) {
                throw new ClassCastException("Main class `" + description.getMainClass() + "' does not extend JavaAddon");
            }

            addon = pluginClass.newInstance();
        } catch (IllegalAccessException ex) {
            throw new InvalidAddonException("No public constructor", ex);
        } catch (InstantiationException ex) {
            throw new InvalidAddonException("Abnormal addon type", ex);
        }
    }

    synchronized void initialize(@NotNull InternalJavaAddon module) {
        if (module.getClass().getClassLoader() != this) {
            throw new IllegalArgumentException("Cannot initialize module outside of this class loader");
        }
        module.init(description, file, this);
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return loadClass0(name, resolve, true);
    }

    public Class<?> loadClass0(@NotNull String name, boolean resolve, boolean global) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException ignored) {}

        if (global){
            Class<?> result = manager.getClassByName(name, resolve);

            if (result != null && result.getClassLoader() instanceof InternalAddonClassLoader) {
                return result;
            }
        }
        throw new ClassNotFoundException(name);
    }

    public File getFile() {
        return file;
    }

    public InternalJavaAddon getAddon() {
        return addon;
    }
}