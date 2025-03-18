package com.jodexindustries.donatecase.common.config.converter.migrators;

import com.jodexindustries.donatecase.common.config.ConfigImpl;
import com.jodexindustries.donatecase.common.config.converter.ConfigMigrator;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.InvocationTargetException;

public class AnimationsMigrator_1_4_to_1_5 implements ConfigMigrator {

    @Override
    public void migrate(ConfigImpl config) throws SerializationException {
        Class<?> clazz;
        Object object;
        try {
            clazz = Class.forName("com.jodexindustries.donatecase.spigot.animations.pop.PopSettings");
            object = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new SerializationException(e);
        }
        config.node("POP").set(object);
        config.node("config", "version").set(15);
    }

}