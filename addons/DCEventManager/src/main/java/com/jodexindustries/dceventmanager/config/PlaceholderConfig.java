package com.jodexindustries.dceventmanager.config;

import com.jodexindustries.dceventmanager.data.EventPlaceholder;
import com.jodexindustries.donatecase.common.config.ConfigImpl;
import io.leangen.geantyref.TypeToken;
import lombok.Getter;
import org.spongepowered.configurate.ConfigurateException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
public class PlaceholderConfig extends ConfigImpl {

    private static final TypeToken<Map<String, EventPlaceholder>> MAP_TYPE_TOKEN = new TypeToken<Map<String, EventPlaceholder>>() {};

    private Map<String, EventPlaceholder> eventPlaceholders;

    public PlaceholderConfig(File file) {
        super(file);
    }

    @Override
    public void load() throws ConfigurateException {
        node(loader().load());
        this.eventPlaceholders = node("events").get(MAP_TYPE_TOKEN, new HashMap<>());
    }
}
