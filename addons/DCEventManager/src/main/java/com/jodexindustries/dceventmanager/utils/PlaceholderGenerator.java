package com.jodexindustries.dceventmanager.utils;

import com.jodexindustries.dceventmanager.config.PlaceholderConfig;
import com.jodexindustries.dceventmanager.data.EventPlaceholder;
import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.event.DCEvent;
import com.jodexindustries.donatecase.api.platform.DCPlayer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlaceholderGenerator {

    private final PlaceholderConfig config;
    private final Set<Class<? extends DCEvent>> classes;

    public PlaceholderGenerator(PlaceholderConfig config, Set<Class<? extends DCEvent>> classes) {
        this.config = config;
        this.classes = classes;
    }

    public void generate() {
        Map<String, EventPlaceholder> eventPlaceholders = config.getEventPlaceholders();

        for (Class<? extends DCEvent> clazz : classes) {
            String className = clazz.getSimpleName();
            EventPlaceholder eventPlaceholder = eventPlaceholders.computeIfAbsent(className, k -> new EventPlaceholder());

            Set<EventPlaceholder.Placeholder> placeholders = eventPlaceholder.getPlaceholders() != null ?
                    new HashSet<>(eventPlaceholder.getPlaceholders()) :
                    new HashSet<>();

            for (Method method : clazz.getDeclaredMethods()) {
                placeholders.addAll(generate("", method));
            }

            eventPlaceholder.setPlaceholders(new ArrayList<>(placeholders));
        }
    }

    private Set<EventPlaceholder.Placeholder> generate(String prefix, Method method) {
        Set<EventPlaceholder.Placeholder> placeholders = new HashSet<>();

        Class<?> returnType = method.getReturnType();
        String name = method.getName();

        if (ActiveCase.class.isAssignableFrom(returnType)) {
            placeholders.addAll(generateActiveCase());
        } else if (DCPlayer.class.isAssignableFrom(returnType)) {
            placeholders.add(generatePlayer(prefix, name));
        } else if ((returnType.isPrimitive() || String.class.isAssignableFrom(returnType))
                && !name.equals("toString")
                && !name.equals("hashCode")
                && !name.equals("canEqual")
                && !name.equals("equals")) {
            placeholders.add(build(name, prefix + name));
        }

        return placeholders;
    }

    private Set<EventPlaceholder.Placeholder> generateActiveCase() {
        Set<EventPlaceholder.Placeholder> placeholders = new HashSet<>();

        for (Method method : ActiveCase.class.getDeclaredMethods()) {
            if (method.getReturnType() != ActiveCase.class) {
                placeholders.addAll(generate("activeCase#", method));
            }
        }

        return placeholders;
    }

    private EventPlaceholder.Placeholder generatePlayer(String prefix, String name) {
        return build(name, prefix + name + "#getName");
    }

    private EventPlaceholder.Placeholder build(String name, String path) {
        return new EventPlaceholder.Placeholder(name.toLowerCase(), "%" + name.toLowerCase() + "%", path);
    }
}
