package com.jodexindustries.donatecase.api.addon.internal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class InternalAddonDescription {

    private final File file;
    private final JarFile jar;
    private final String name;
    private final String mainClass;
    private final String version;
    private final String apiVersion;
    private final List<String> authors;
    private final List<String> depend = new ArrayList<>();
    private final List<String> softDepend = new ArrayList<>();
    /**
     * Constructor to load addon description from a JAR file.
     *
     * @param file The addon JAR file.
     * @throws IOException If an I/O error occurs while reading the file.
     * @throws InvalidAddonException If the addon does not contain a valid addon.yml descriptor.
     */
    public InternalAddonDescription(File file) throws IOException, InvalidAddonException {
        this.file = file;
        this.jar = new JarFile(file);

        JarEntry entry = jar.getJarEntry("addon.yml");
        if (entry == null) {
            throw new InvalidAddonException("Addon " + file.getName() + " trying to load without addon.yml! Abort.");
        }
        InputStream input = jar.getInputStream(entry);
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(input);
        name = String.valueOf(data.get("name"));
        mainClass = String.valueOf(data.get("main"));
        version = String.valueOf(data.get("version"));

        Object api = data.get("api");
        if(api == null) apiVersion = null;
        else apiVersion = String.valueOf(api);

        authors = new ArrayList<>();

        if (data.get("author") != null) authors.add(data.get("author").toString());
        if (data.get("authors") != null) {
            for (Object o : (Iterable<?>) data.get("authors")) {
                authors.add(o.toString());
            }
        }

        if(data.get("softdepend") != null) {
            for (Object o : (Iterable<?>) data.get("softdepend")) {
                softDepend.add(o.toString());
            }
        }

        if(data.get("depend") != null) {
            for (Object o : (Iterable<?>) data.get("depend")) {
                depend.add(o.toString());
            }
        }

        jar.close();

    }

    /**
     * @return The version of the addon.
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return The fully qualified name of the main class of the addon.
     */
    public String getMainClass() {
        return mainClass;
    }

    /**
     * @return The name of the addon.
     */
    public String getName() {
        return name;
    }

    /**
     * @return A list of authors of the addon.
     */
    public List<String> getAuthors() {
        return authors;
    }

    /**
     * @return The API version the addon is compatible with, or null if not specified.
     */
    @Nullable
    public String getApiVersion() {
        return apiVersion;
    }

    /**
     * @since 2.0.2
     * @return List of addon dependencies
     */
    @NotNull
    public List<String> getDepend() {
        return depend;
    }

    /**
     * @since 2.0.2
     * @return List of addon soft dependencies
     */
    @NotNull
    public List<String> getSoftDepend() {
        return softDepend;
    }

    public File getFile() {
        return file;
    }

}