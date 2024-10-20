package com.jodexindustries.dceventmanager.utils;

import com.jodexindustries.dceventmanager.bootstrap.Main;
import com.jodexindustries.dceventmanager.config.Config;
import com.jodexindustries.dceventmanager.data.EventData;
import com.jodexindustries.dceventmanager.data.Placeholder;
import com.jodexindustries.dceventmanager.event.DCEventExecutor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Tools implements Listener {
    public static Map<String, List<EventData>> eventMap = new HashMap<>();
    public static Map<String, List<Placeholder>> placeholderMap = new HashMap<>();
    public boolean debug = false;
    private final Main main;
    private final Config config;


    public Tools(Main main) {
        this.main = main;
        this.config = new Config(main);
    }
    public void load() {
        debug = config.getConfig().getBoolean("Debug");
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
        if(section == null) return;
        int i = 0;
        for (String event : section.getKeys(false)) {
            ConfigurationSection eventSection = section.getConfigurationSection(event);
            if(eventSection == null) continue;

            List<Placeholder> placeholders = new ArrayList<>();

            for (String placeholder : eventSection.getKeys(false)) {
                ConfigurationSection placeholderSection = eventSection.getConfigurationSection(placeholder);
                if(placeholderSection == null) continue;

                String name = placeholderSection.getString("placeholder");
                String method = placeholderSection.getString("method");

                Placeholder p = new Placeholder(name, method);
                placeholders.add(p);
                i++;
                if(debug) main.getLogger().info("Placeholder " + placeholder + " for event " + event + " loaded");
            }

            placeholderMap.put(event.toUpperCase(), placeholders);
        }

        main.getLogger().info("Loaded " + i + " placeholders");
    }

    public void loadEvents() {
        eventMap.clear();

        ConfigurationSection section = config.getConfig().getConfigurationSection("Events");
        int i = 0;
        if(section != null) {
            for (String event : section.getKeys(false)) {
                String eventName = section.getString(event + ".Event");
                if (eventName == null || eventName.isEmpty()) {
                    main.getLogger().warning("Event management " + event + " does not have an Event parameter");
                    continue;
                }
                eventName = eventName.toUpperCase();

                List<String> actions = section.getStringList(event + ".Actions");
                String caseName = section.getString(event + ".Case");
                int slot = section.getInt(event + ".Slot", -1);
                EventData data = new EventData(actions, caseName, slot);

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

    public void registerEvents() {
        unregisterEvents();
        ArrayList<Class<? extends Event>> classes = getClasses();
        PluginManager pluginManager = Bukkit.getPluginManager();

        int i;
        for (i = 0; i < classes.size(); i++) {
            Class<? extends Event> clazz = classes.get(i);
            String event = clazz.getSimpleName();
            pluginManager.registerEvent(clazz, this, EventPriority.NORMAL,
                    new DCEventExecutor(event, this), main.getPlugin());
            if (debug) main.getLogger().info("Event " + event + " registered");
        }

        main.getLogger().info("Registered " + i + " events");
    }

    public void unregisterEvents() {
        HandlerList.unregisterAll(this);
    }

    private ArrayList<Class<? extends Event>> getClasses() {
        ArrayList<Class<? extends Event>> classes;

        try {
            classes = Reflection.getClassesForPackage(getClass().getClassLoader(),
                    config.getConfig().getString("Package",
                            "com.jodexindustries.donatecase.api.events"));
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
