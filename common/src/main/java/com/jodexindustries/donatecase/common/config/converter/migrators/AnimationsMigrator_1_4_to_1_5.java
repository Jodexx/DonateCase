package com.jodexindustries.donatecase.common.config.converter.migrators;

import com.jodexindustries.donatecase.common.config.ConfigImpl;
import com.jodexindustries.donatecase.common.config.converter.ConfigMigrator;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.InvocationTargetException;

public class AnimationsMigrator_1_4_to_1_5 implements ConfigMigrator {

    @Override
    public void migrate(ConfigImpl config) throws SerializationException {
        try {
            Class<?> popClazz = Class.forName("com.jodexindustries.donatecase.spigot.animations.pop.PopSettings");
            Object popObject = popClazz.getDeclaredConstructor().newInstance();
            config.node("POP").set(popObject);

            Class<?> selectClazz = Class.forName("com.jodexindustries.donatecase.spigot.animations.select.SelectSettings");
            Object selectObject = selectClazz.getDeclaredConstructor().newInstance();
            config.node("SELECT").set(selectObject);

            config.node("config", "version").set(15);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new SerializationException(e);
        }
    }

}