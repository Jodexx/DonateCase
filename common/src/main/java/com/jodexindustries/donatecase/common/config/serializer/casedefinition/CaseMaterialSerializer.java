package com.jodexindustries.donatecase.common.config.serializer.casedefinition;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMaterial;
import com.jodexindustries.donatecase.api.tools.DCTools;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;

public class CaseMaterialSerializer implements TypeSerializer<CaseMaterial> {

    @Override
    public CaseMaterial deserialize(Type type, ConfigurationNode node) throws SerializationException {

        String id = node.node("id").getString();

        ConfigurationNode rgbNode = node.node("rgb");

        Integer[] rgb = rgbNode.isList() ? rgbNode.get(Integer[].class, new Integer[0]) : DCTools.parseRGB(rgbNode.getString());

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
        node.node("rgb").setList(Integer.class, Arrays.asList(obj.rgb()));
    }
}
