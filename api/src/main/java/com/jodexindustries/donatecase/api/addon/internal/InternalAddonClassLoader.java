package com.jodexindustries.donatecase.api.addon.internal;

import com.google.common.io.ByteStreams;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.manager.AddonManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class InternalAddonClassLoader extends URLClassLoader {

    private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();
    private final InternalAddonDescription description;
    private final File file;
    private final JarFile jar;
    private final Manifest manifest;
    private final URL url;
    private final AddonManager manager;
    private final InternalJavaAddon addon;
    private final Addon donateCase;

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public InternalAddonClassLoader(@Nullable ClassLoader parent, InternalAddonDescription description, AddonManager manager, Addon donateCase) throws IOException, InvalidAddonException, ClassNotFoundException {
        super(new URL[]{description.getFile().toURI().toURL()}, parent);

        this.description = description;
        this.file = description.getFile();
        this.jar = new JarFile(file);
        this.manifest = jar.getManifest();
        this.url = file.toURI().toURL();
        this.manager = manager;
        this.donateCase = donateCase;

        try {
            Class<?> jarClass;
            try {
                jarClass = Class.forName(description.getMainClass(), true, this);
            } catch (ClassNotFoundException ex) {
                throw new ClassNotFoundException("Cannot find main class `" + description.getMainClass() + "'", ex);
            }

            Class<? extends InternalJavaAddon> pluginClass;

            try {
                pluginClass = jarClass.asSubclass(InternalJavaAddon.class);
            } catch (ClassCastException ex) {
                throw new ClassCastException("Main class `" + description.getMainClass() + "' does not extend JavaAddon");
            }

            addon = pluginClass.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new InvalidAddonException("No public constructor", e);
        } catch (InstantiationException ex) {
            throw new InvalidAddonException("Abnormal addon type", ex);
        }
    }

    @Override
    public URL getResource(String name) {
        return findResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return findResources(name);
    }

    synchronized void initialize(@NotNull InternalJavaAddon module) {
        if (module.getClass().getClassLoader() != this) {
            throw new IllegalArgumentException("Cannot initialize module outside of this class loader");
        }
        module.init(description, file, this, donateCase);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return loadClass0(name, resolve, true);
    }

    public Class<?> loadClass0(@NotNull String name, boolean resolve, boolean global) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException ignored) {
        }

        if (global) {
            Class<?> result = manager.getClassByName(name, resolve);

            if (result != null && result.getClassLoader() instanceof InternalAddonClassLoader) {
                return result;
            }
        }
        throw new ClassNotFoundException(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.startsWith("org.bukkit.") || name.startsWith("net.minecraft.")) {
            throw new ClassNotFoundException(name);
        }
        Class<?> result = classes.get(name);

        if (result == null) {
            String path = name.replace('.', '/').concat(".class");
            JarEntry entry = jar.getJarEntry(path);

            if (entry != null) {
                byte[] classBytes;

                try (InputStream is = jar.getInputStream(entry)) {
                    classBytes = ByteStreams.toByteArray(is);
                } catch (IOException ex) {
                    throw new ClassNotFoundException(name, ex);
                }

                int dot = name.lastIndexOf('.');
                if (dot != -1) {
                    String pkgName = name.substring(0, dot);
                    if (getPackage(pkgName) == null) {
                        try {
                            if (manifest != null) {
                                definePackage(pkgName, manifest, url);
                            } else {
                                definePackage(pkgName, null, null, null, null, null, null, null);
                            }
                        } catch (IllegalArgumentException ex) {
                            if (getPackage(pkgName) == null) {
                                throw new IllegalStateException("Cannot find package " + pkgName);
                            }
                        }
                    }
                }

                CodeSigner[] signers = entry.getCodeSigners();
                CodeSource source = new CodeSource(url, signers);

                result = defineClass(name, classBytes, 0, classBytes.length, source);
            }

            if (result == null) {
                result = super.findClass(name);
            }

            classes.put(name, result);
        }

        return result;
    }

    public static void saveFromInputStream(InputStream in, File outFile) throws IOException {
        OutputStream out = Files.newOutputStream(outFile.toPath());
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();
    }

    public File getFile() {
        return file;
    }

    public InternalJavaAddon getAddon() {
        return addon;
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            jar.close();
        }
    }
}