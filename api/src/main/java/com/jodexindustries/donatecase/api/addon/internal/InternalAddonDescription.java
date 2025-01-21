package com.jodexindustries.donatecase.api.addon.internal;

import lombok.Getter;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Getter
public class InternalAddonDescription {

    private final File file;
    private final String name;
    private final String mainClass;
    private final String version;
    private final String apiVersion;
    private final List<String> authors;
    private final List<String> depend;
    private final List<String> softDepend;

    /**
     * Constructor to load addon description from a JAR file.
     *
     * @param file The addon JAR file.
     * @throws IOException If an I/O error occurs while reading the file.
     * @throws InvalidAddonException If the addon does not contain a valid addon.yml descriptor.
     */
    public InternalAddonDescription(File file) throws IOException, InvalidAddonException {
        this.file = file;
        JarFile jar = new JarFile(file);

        JarEntry entry = jar.getJarEntry("addon.yml");
        if (entry == null) {
            throw new InvalidAddonException("Addon " + file.getName() + " trying to load without addon.yml! Abort.");
        }

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder().source(() -> {
            InputStream input = jar.getInputStream(entry);
            InputStreamReader reader = new InputStreamReader(input);
            return new BufferedReader(reader);
        }).build();

        ConfigurationNode config = loader.load();

        name = config.node("name").getString();
        mainClass = config.node("main").getString();
        version = config.node("version").getString();
        apiVersion = config.node("api").getString();

        authors = config.node("authors").getList(String.class, new ArrayList<>());

        if (config.node("author") != null) authors.add(config.node("author").getString());

        softDepend = config.node("softdepend").getList(String.class, new ArrayList<>());
        depend = config.node("depend").getList(String.class, new ArrayList<>());

        jar.close();

    }

}