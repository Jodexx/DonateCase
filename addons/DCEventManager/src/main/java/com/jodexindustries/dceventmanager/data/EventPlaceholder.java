package com.jodexindustries.dceventmanager.data;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.List;

@Getter
@ConfigSerializable
public class EventPlaceholder {

    @Setting
    private List<Placeholder> placeholders;

    @Getter
    @ConfigSerializable
    public static class Placeholder {

        @Setting
        private String name;

        @Setting
        private String replace;

        @Setting
        private String method;
    }
}
