package com.jodexindustries.donatecase.papermc.remapper;

import com.google.j2objc.annotations.UsedByReflection;
import com.jodexindustries.donatecase.api.io.FilesLookup;
import com.jodexindustries.donatecase.api.io.FilesLookupProvider;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@UsedByReflection
public final class PluginRemapperFilesLookupProvider implements FilesLookupProvider {

    private final Method create;
    private final Method rewritePluginDirectory;
    private final Method rewritePlugin;
    private final Method loadingPlugins;
    private final Method pluginsEnabled;
    private final Method shutdown;

    public PluginRemapperFilesLookupProvider() {
        try {
            Class<?> clazz = Class.forName("io.papermc.paper.pluginremap.PluginRemapper");

            this.create = clazz.getMethod("create", Path.class);
            this.rewritePluginDirectory = clazz.getMethod("rewritePluginDirectory", List.class);
            this.rewritePlugin = clazz.getMethod("rewritePlugin", Path.class);
            this.loadingPlugins = clazz.getMethod("loadingPlugins");
            this.pluginsEnabled = clazz.getMethod("pluginsEnabled");
            this.shutdown = clazz.getMethod("shutdown");
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Paper PluginRemapper is unavailable", ex);
        }
    }

    @Override
    public FilesLookup createFilesLookup(File folder) {
        try {
            Object remapper = create.invoke(null, folder.toPath());

            loadingPlugins.invoke(remapper);

            List<Path> jars = new ArrayList<>();

            File[] files = folder.listFiles(file ->
                    file.isFile() && file.getName().endsWith(".jar"));

            if (files != null) {
                for (File file : files) {
                    jars.add(file.toPath());
                }
            }

            rewritePluginDirectory.invoke(remapper, jars);

            File remappedFolder = new File(folder, ".paper-remapped");
            if (!remappedFolder.isDirectory()) {
                throw new IllegalStateException("Remapped folder was not created.");
            }

            return new Lookup(
                    remappedFolder,
                    remapper,
                    rewritePlugin,
                    pluginsEnabled,
                    shutdown
            );
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to initialize PluginRemapper", ex);
        }
    }

    private static final class Lookup implements FilesLookup {

        private final File remappedFolder;
        private final Method rewritePlugin;
        private final Method pluginsEnabled;
        private final Method shutdown;

        private Object remapper;

        private Lookup(File remappedFolder,
                       Object remapper,
                       Method rewritePlugin,
                       Method pluginsEnabled,
                       Method shutdown) {
            this.remappedFolder = remappedFolder;
            this.remapper = remapper;
            this.rewritePlugin = rewritePlugin;
            this.pluginsEnabled = pluginsEnabled;
            this.shutdown = shutdown;
        }

        @Override
        public File getFile(String name) {
            try {
                Path path = (Path) rewritePlugin.invoke(
                        remapper,
                        new File(remappedFolder, name).toPath()
                );

                return path.toFile();
            } catch (ReflectiveOperationException ex) {
                throw new RuntimeException("Failed to remap " + name, ex);
            }
        }

        @Override
        public void close() {
            if (remapper == null)
                return;

            try {
                pluginsEnabled.invoke(remapper);
                shutdown.invoke(remapper);
            } catch (ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            } finally {
                remapper = null;
            }
        }
    }
}