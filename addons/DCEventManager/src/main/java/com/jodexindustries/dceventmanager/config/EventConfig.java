package com.jodexindustries.dceventmanager.config;

import com.jodexindustries.dceventmanager.data.EventData;
import com.jodexindustries.donatecase.common.config.ConfigImpl;
import io.leangen.geantyref.TypeToken;
import lombok.Getter;
import org.spongepowered.configurate.ConfigurateException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
public class EventConfig extends ConfigImpl {

    private Map<String, EventData> events;

    public EventConfig(File file) {
        super(file);
    }

    @Override
    public void load() throws ConfigurateException {
        node(loader().load());

        TypeToken<Map<String, EventData>> token = new TypeToken<Map<String, EventData>>() {
        };
        this.events = node("events").get(token);
        if(events == null) events = new HashMap<>();
    }

}
