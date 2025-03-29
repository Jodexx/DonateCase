package com.jodexindustries.donatecase.common.config.converter.migrators;

import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.common.config.ConfigImpl;
import com.jodexindustries.donatecase.common.config.converter.ConfigMigrator;
import org.spongepowered.configurate.ConfigurationNode;
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

            updatePosition(config.node("SHAPE", "StartPosition"));
            updatePosition(config.node("FIREWORK", "StartPosition"));

            ConfigurationNode wheelNode = config.node("WHEEL");
            wheelNode.node("StartPosition").set(new CaseLocation(.5, 1, .5));
            wheelNode.removeChild("LiftingAlongX");
            wheelNode.removeChild("LiftingAlongY");
            wheelNode.removeChild("LiftingAlongZ");

            config.node("config", "version").set(15);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new SerializationException(e);
        }
    }

    private void updatePosition(ConfigurationNode node) throws SerializationException {
        CaseLocation startPosition = parseLocation(node);
        node.set(startPosition);
    }

    private CaseLocation parseLocation(ConfigurationNode node) {
        CaseLocation location = new CaseLocation(node.node("X").getDouble(), node.node("Y").getDouble(), node.node("Z").getDouble());
        node.removeChild("X");
        node.removeChild("Y");
        node.removeChild("Z");
        return location;
    }

}