package com.jodexindustries.dceventmanager.data;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.List;

@Getter
@ConfigSerializable
public class EventData {

    @Setting
    private List<Executor> executors;

    @Getter
    @ConfigSerializable
    public static class Executor {

        @Setting
        private String name;

        // TODO Conditions

        @Setting
        private List<String> actions;
    }
}