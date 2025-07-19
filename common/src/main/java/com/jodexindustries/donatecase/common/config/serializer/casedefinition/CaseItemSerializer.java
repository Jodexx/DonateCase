package com.jodexindustries.donatecase.common.config.serializer.casedefinition;

import com.jodexindustries.donatecase.api.data.casedata.GiveType;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseItem;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMaterial;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CaseItemSerializer implements TypeSerializer<CaseItem> {

    @Override
    public CaseItem deserialize(Type type, ConfigurationNode node) throws SerializationException {
        Map<String, CaseItem.RandomAction> randomActions = new HashMap<>();

        ConfigurationNode randomActionsNode = node.node("random-actions");

        if (randomActionsNode.isMap()) {
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : randomActionsNode.childrenMap().entrySet()) {
                randomActions.put(String.valueOf(entry.getKey()), entry.getValue().get(CaseItem.RandomAction.class));
            }
        }

        return new CaseItem(
                String.valueOf(node.key()),
                node.node("group").getString(),
                node.node("chance").getDouble(),
                node.node("index").getInt(),
                node.node("material").get(CaseMaterial.class),
                node.node("give-type").get(GiveType.class, GiveType.ONE),
                node.node("actions").getList(String.class),
                node.node("alternative-actions").getList(String.class),
                randomActions
        );
    }

    @Override
    public void serialize(Type type, @Nullable CaseItem obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) return;

        // TODO finish serialization of CaseItem
    }

    public static class RandomAction implements TypeSerializer<CaseItem.RandomAction> {

        @Override
        public CaseItem.RandomAction deserialize(Type type, ConfigurationNode node) throws SerializationException {
            return new CaseItem.RandomAction(
                    String.valueOf(node.key()),
                    node.node("chance").getDouble(),
                    node.node("actions").getList(String.class),
                    node.node("display-name").getString()
            );
        }

        @Override
        public void serialize(Type type, CaseItem.@Nullable RandomAction obj, ConfigurationNode node) throws SerializationException {
            if (obj == null) return;

            // TODO finish serialization of RandomAction
        }
    }
}
