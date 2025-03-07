package com.jodexindustries.dceventmanager.config;

import com.jodexindustries.dceventmanager.data.EventPlaceholder;
import com.jodexindustries.donatecase.common.config.ConfigImpl;
import io.leangen.geantyref.TypeToken;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;

import java.io.File;
import java.util.Map;

@Getter
public class PlaceholderConfig extends ConfigImpl {

    @Nullable
    private Map<String, EventPlaceholder> eventPlaceholders;

    public PlaceholderConfig(File file) {
        super(file);
    }

    @Override
    public void load() throws ConfigurateException {
        node(loader().load());

        TypeToken<Map<String, EventPlaceholder>> token = new TypeToken<Map<String, EventPlaceholder>>() {
        };
        this.eventPlaceholders = node("events").get(token);
    }
}
