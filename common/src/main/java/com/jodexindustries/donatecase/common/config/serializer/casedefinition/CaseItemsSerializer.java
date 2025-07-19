package com.jodexindustries.donatecase.common.config.serializer.casedefinition;

import com.jodexindustries.donatecase.api.data.casedefinition.CaseItems;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseItem;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CaseItemsSerializer implements TypeSerializer<CaseItems> {

    @Override
    public CaseItems deserialize(Type type, ConfigurationNode node) throws SerializationException {
        Map<String, CaseItem> items = new HashMap<>();

        if (node.isMap()) {
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()) {
                items.put(String.valueOf(entry.getKey()), entry.getValue().get(CaseItem.class));
            }
        }

        return new CaseItems(items);
    }

    @Override
    public void serialize(Type type, @Nullable CaseItems obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) return;

        for (Map.Entry<String, CaseItem> entry : obj.items().entrySet()) {
            node.node(entry.getKey()).set(entry.getValue());
        }
    }
}
