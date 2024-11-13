package com.jodexindustries.donatecase.api.addon.internal;

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
    private final String name;
    private final String mainClass;
    private final String version;
    private final String apiVersion;
    private final List<String> libraries;
    private final List<String> authors;

    public InternalAddonDescription(File file) throws IOException, InvalidAddonException {
        JarFile jarFile = new JarFile(file);
        JarEntry entry = jarFile.getJarEntry("addon.yml");
        if (entry == null) {
            throw new InvalidAddonException("Addon " + file.getName() + " trying to load without addon.yml! Abort.");
        }
        InputStream input = jarFile.getInputStream(entry);
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

        libraries = new ArrayList<>();

        if (data.get("libraries") != null) {
            for (Object o : (Iterable<?>) data.get("libraries")) {
                libraries.add(o.toString());
            }
        }
        jarFile.close();
    }

    public String getVersion() {
        return version;
    }

    public String getMainClass() {
        return mainClass;
    }

    public String getName() {
        return name;
    }

    public List<String> getLibraries() {
        return libraries;
    }

    public List<String> getAuthors() {
        return authors;
    }

    @Nullable
    public String getApiVersion() {
        return apiVersion;
    }
}