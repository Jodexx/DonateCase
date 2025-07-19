package com.jodexindustries.donatecase.common.config.serializer.casedefinition;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMaterial;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class CaseMaterialSerializer implements TypeSerializer<CaseMaterial> {

    @Override
    public CaseMaterial deserialize(Type type, ConfigurationNode node) throws SerializationException {

        String id = node.node("id").getString();

        String[] rgb = node.node("rgb").getList(String.class, new ArrayList<>())
                .toArray(new String[0]);

        Object itemStack = DCAPI.getInstance().getPlatform().getTools().loadCaseItem(id);

        return new CaseMaterial(
                id,
                node.node("display-name").getString(),
                node.node("enchanted").getBoolean(),
                node.node("lore").getList(String.class),
                node.node("model-data").getInt(),
                rgb,
                itemStack
        );
    }

    @Override
    public void serialize(Type type, @Nullable CaseMaterial obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) return;

        node.node("id").set(obj.id());
        node.node("display-name").set(obj.displayName());
        node.node("enchanted").set(obj.enchanted());
        node.node("lore").setList(String.class, obj.lore());
        node.node("model-data").set(obj.modelData());
        node.node("rgb").setList(String.class, Arrays.asList(obj.rgb()));
    }
}
