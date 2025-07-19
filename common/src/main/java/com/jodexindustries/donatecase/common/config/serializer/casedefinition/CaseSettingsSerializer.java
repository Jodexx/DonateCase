package com.jodexindustries.donatecase.common.config.serializer.casedefinition;

import com.jodexindustries.donatecase.api.data.casedata.OpenType;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseSettings;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CaseSettingsSerializer implements TypeSerializer<CaseSettings> {

    @Override
    public CaseSettings deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String caseType = node.node("type").getString();
        if (caseType == null) throw new SerializationException("Case type cannot be null!");

        String defaultMenu = node.node("default-menu").getString();
        if (defaultMenu == null) throw new SerializationException("Case default menu cannot be null!");

        return new CaseSettings(
                caseType,
                defaultMenu,
                node.node("animation").getString(),
                node.node("hologram").get(CaseSettings.Hologram.class),
                node.node("level-groups").get(CaseSettings.LevelGroups.class),
                node.node("no-key-actions").getList(String.class),
                node.node("open-type").get(OpenType.class),
                node.node("animation-settings"),
                node.node("cooldown-before-animation").getInt(),
                node.node("history-data-size").getInt(),
                node.node("display-name").getString()
        );
    }

    @Override
    public void serialize(Type type, @Nullable CaseSettings obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) return;

        node.node("type").set(obj.type());
        node.node("default-menu").set(obj.defaultMenu());
        node.node("animation").set(obj.animation());
        node.node("hologram").set(CaseSettings.Hologram.class, obj.hologram());
        node.node("level-groups").set(CaseSettings.LevelGroups.class, obj.levelGroups());
        node.node("no-key-actions").setList(String.class, obj.noKeyActions());
        node.node("open-type").set(OpenType.class, obj.openType());
        node.node("animation-settings").set(obj.animationSettings());
        node.node("cooldown-before-animation").set(obj.cooldownBeforeAnimation());
        node.node("history-data-size").set(obj.historyDataSize());
        node.node("display-name").set(obj.displayName());
    }

    public static class LevelGroups implements TypeSerializer<CaseSettings.LevelGroups> {

        @Override
        public CaseSettings.LevelGroups deserialize(Type type, ConfigurationNode node) {
            Map<String, Integer> map = new HashMap<>();

            if (node.isMap()) {
                for (Map.Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()) {
                    map.put(String.valueOf(entry.getKey()), entry.getValue().getInt());
                }
            }

            return new CaseSettings.LevelGroups(map);
        }

        @Override
        public void serialize(Type type, CaseSettings.@Nullable LevelGroups obj, ConfigurationNode node) throws SerializationException {
            if (obj == null) return;

            for (Map.Entry<String, Integer> entry : obj.map().entrySet()) {
                node.node(entry.getKey()).set(entry.getValue());
            }
        }
    }

    public static class Hologram implements TypeSerializer<CaseSettings.Hologram> {

        @Override
        public CaseSettings.Hologram deserialize(Type type, ConfigurationNode node) throws SerializationException {
            return new CaseSettings.Hologram(
                    node,
                    node.node("enabled").getBoolean(),
                    node.node("height").getDouble(),
                    node.node("range").getInt(),
                    node.node("message").getList(String.class)
            );
        }

        @Override
        public void serialize(Type type, CaseSettings.@Nullable Hologram obj, ConfigurationNode node) throws SerializationException {
            if (obj == null) return;

            node.node("enabled").set(obj.enabled());
            node.node("height").set(obj.height());
            node.node("range").set(obj.range());
            node.node("message").setList(String.class, obj.message());
        }
    }
}
