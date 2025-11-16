package com.jodexindustries.donatecase.common.config.converter.migrators;

import com.jodexindustries.donatecase.api.config.Config;
import com.jodexindustries.donatecase.api.config.converter.ConfigMigrator;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.InvocationTargetException;

public class AnimationsMigrator_1_5_to_1_6 implements ConfigMigrator {

    @Override
    public void migrate(Config config) throws SerializationException {
        try {
            Class<?> futureWheelClazz = Class.forName("com.jodexindustries.donatecase.spigot.animations.futurewheel.FutureWheelSettings");
            Object futureWheelObject = futureWheelClazz.getDeclaredConstructor().newInstance();

            config.node("FUTURE_WHEEL").set(futureWheelObject);

            config.node("config", "version").set(16);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new SerializationException(e);
        }
    }
}
