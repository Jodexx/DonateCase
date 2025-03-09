package com.jodexindustries.dceventmanager.utils;

import com.jodexindustries.dceventmanager.bootstrap.MainAddon;
import com.jodexindustries.dceventmanager.config.ConfigManager;
import com.jodexindustries.dceventmanager.config.PlaceholderConfig;
import com.jodexindustries.dceventmanager.event.DCEventExecutor;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.InternalJavaAddon;
import com.jodexindustries.donatecase.api.event.DCEvent;
import lombok.Getter;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;


public class Tools {

    public static List<DCEventExecutor> executors = new ArrayList<>();

    public final DCAPI api = DCAPI.getInstance();

    public boolean debug = false;

    public final MainAddon main;

    @Getter
    private final ConfigManager configManager;

    public Tools(MainAddon main) {
        this.main = main;
        this.configManager = new ConfigManager(main);
    }

    public void load() {
        loadConfig();
        debug = configManager.getConfig().node("debug").getBoolean();
        registerExecutors();
        registerEvents();
    }

    public void unload() {
        unregisterEvents();
    }

    public void registerExecutors() {
        executors.clear();
        Set<Class<? extends DCEvent>> classes = getClasses();

        generatePlaceholders(classes);

        for (Class<? extends DCEvent> clazz : classes) {
            DCEventExecutor executor = new DCEventExecutor(clazz, this);
            executors.add(executor);

            if (debug) main.getLogger().info("Executor for " + clazz.getSimpleName() + " registered");
        }
    }

    public void registerEvents() {
        unregisterEvents();

        for (DCEventExecutor executor : executors) {
            api.getEventBus().register(executor.clazz, executor);
        }

        main.getLogger().info("Registered " + executors.size() + " executors");
    }

    public void unregisterEvents() {
        executors.forEach(executor -> api.getEventBus().unregister(executor));
    }

    public void loadConfig() {
        try {
            configManager.load();
            main.getLogger().info("Registered " + configManager.getEventConfig().getEvents().size() + " events");
        } catch (ConfigurateException e) {
            main.getLogger().log(Level.WARNING, "Error with loading configuration", e);
        }
    }

    private void generatePlaceholders(Set<Class<? extends DCEvent>> classes) {
        PlaceholderConfig config = configManager.getPlaceholderConfig();
        if(!config.getEventPlaceholders().isEmpty()) return;

        main.getLogger().info("Generating player's placeholders for all available events");

        PlaceholderGenerator generator = new PlaceholderGenerator(config, classes);
        try {
            generator.generate();
            config.update();
        } catch (ConfigurateException e) {
            main.getLogger().log(Level.WARNING, "Error with updating placeholders", e);
        }
    }

    private Set<Class<? extends DCEvent>> getClasses() {
        Set<Class<? extends DCEvent>> classes = new HashSet<>();

        try {
            List<String> packages = configManager.getConfig().node("packages").getList(String.class);

            if (packages != null) {
                for (String pkg : packages) {
                    // load classes from DonateCase
                    classes.addAll(Reflection.getClassesForPackage(getClass().getClassLoader().getParent(), pkg));

                    // load classes from addons
                    for (InternalJavaAddon addon : api.getAddonManager().getMap().values()) {
                        classes.addAll(Reflection.getClassesForPackage(addon.getUrlClassLoader(), pkg));
                    }
                }
            }

        } catch (ClassNotFoundException | SerializationException e) {
            throw new RuntimeException(e);
        }

        return classes;
    }

}