package com.jodexindustries.dceventmanager.utils;

import com.jodexindustries.dceventmanager.bootstrap.MainAddon;
import com.jodexindustries.dceventmanager.config.Config;
import com.jodexindustries.dceventmanager.data.EventData;
import com.jodexindustries.dceventmanager.data.Placeholder;
import com.jodexindustries.dceventmanager.event.DCEventExecutor;
import com.jodexindustries.donatecase.api.addon.InternalJavaAddon;
import com.jodexindustries.donatecase.api.event.DCEvent;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Tools implements Listener {

    public static Map<String, List<EventData>> eventMap = new HashMap<>();
    public static Map<String, List<Placeholder>> placeholderMap = new HashMap<>();
    public static List<DCEventExecutor> executors = new ArrayList<>();

    public boolean debug = false;
    @Getter
    private final MainAddon main;
    private final Config config;

    public Tools(MainAddon main) {
        this.main = main;
        this.config = new Config(main);
    }

    public void load() {
        debug = config.getConfig().getBoolean("Debug");
        registerExecutors();
        registerEvents();
        loadPlaceholders();
        loadEvents();
    }

    public void unload() {
        unregisterEvents();
    }

    public void loadPlaceholders() {
        placeholderMap.clear();
        ConfigurationSection section = config.getPlaceholders().getConfigurationSection("Events");
        if (section == null) return;
        int i = 0;
        for (String event : section.getKeys(false)) {
            ConfigurationSection eventSection = section.getConfigurationSection(event);
            if (eventSection == null) continue;

            List<Placeholder> placeholders = new ArrayList<>();

            for (String placeholder : eventSection.getKeys(false)) {
                ConfigurationSection placeholderSection = eventSection.getConfigurationSection(placeholder);
                if (placeholderSection == null) continue;

                String name = placeholderSection.getString("placeholder");
                String method = placeholderSection.getString("method");

                Placeholder p = new Placeholder(name, method);
                placeholders.add(p);
                i++;
                if (debug) main.getLogger().info("Placeholder " + placeholder + " for event " + event + " loaded");
            }

            placeholderMap.put(event.toUpperCase(), placeholders);
        }

        main.getLogger().info("Loaded " + i + " placeholders");
    }

    public void loadEvents() {
        eventMap.clear();

        ConfigurationSection section = config.getConfig().getConfigurationSection("Events");
        int i = 0;
        if (section != null) {
            for (String event : section.getKeys(false)) {
                String eventName = section.getString(event + ".Event");
                if (eventName == null || eventName.isEmpty()) {
                    main.getLogger().warning("Event management " + event + " does not have an Event parameter");
                    continue;
                }
                eventName = eventName.toUpperCase();

                List<String> actions = section.getStringList(event + ".Actions");
                String caseType = section.getString(event + ".Case");
                int slot = section.getInt(event + ".Slot", -1);
                EventData data = new EventData(actions, caseType, slot);

                List<EventData> list = new ArrayList<>();
                if (eventMap.get(eventName) != null) {
                    list = eventMap.get(eventName);
                }
                list.add(data);
                eventMap.put(eventName, list);
                i++;
                if (debug) main.getLogger().info("Event management " + event + " loaded");
            }
        }
        main.getLogger().info("Loaded " + i + " event managements from " + eventMap.size() + " events");
    }

    public void registerExecutors() {
        executors.clear();
        ArrayList<Class<? extends DCEvent>> classes = getClasses();

        int i;
        for (i = 0; i < classes.size(); i++) {
            Class<? extends DCEvent> clazz = classes.get(i);

            DCEventExecutor executor = new DCEventExecutor(clazz, this);
            executors.add(executor);

            if (debug) main.getLogger().info("Executor for " + clazz.getSimpleName() + " registered");
        }
    }

    public void registerEvents() {
        unregisterEvents();

        for (DCEventExecutor executor : executors) {
            main.api.getEventBus().register(executor.clazz, executor);
        }

        main.getLogger().info("Registered " + executors.size() + " events");
    }

    public void unregisterEvents() {
        executors.forEach(executor -> main.api.getEventBus().unregister(executor));
    }

    private ArrayList<Class<? extends DCEvent>> getClasses() {
        ArrayList<Class<? extends DCEvent>> classes;

        try {
            String pkg = config.getConfig().getString("Package",
                    "com.jodexindustries.donatecase.api.events");

            // load classes from DonateCase
            classes = Reflection.getClassesForPackage(getClass().getClassLoader().getParent(), pkg);

            // load classes from addons
            for (InternalJavaAddon addon : main.api.getAddonManager().getMap().values()) {
                classes.addAll(Reflection.getClassesForPackage(addon.getUrlClassLoader(), pkg));
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return classes;
    }

    public void reloadConfig() {
        config.reloadConfig();
        config.reloadPlaceholders();
        load();
        main.getLogger().info("Config reloaded");
    }

}