package com.jodexindustries.donatecase.common.config.serializer.casedefinition;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMaterial;
import com.jodexindustries.donatecase.api.tools.DCTools;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CaseMaterialSerializer implements TypeSerializer<CaseMaterial> {

    @Override
    public CaseMaterial deserialize(Type type, ConfigurationNode node) throws SerializationException {

        String id = node.node("id").getString();

        String[] rgb = node.node("rgb").getList(String.class, new ArrayList<>())
                .toArray(new String[0]);

        Object itemStack = DCAPI.getInstance().getPlatform().getTools().loadCaseItem(id);

        return new CaseMaterial(
                id,
                DCTools.rc(node.node("display-name").getString()),
                node.node("enchanted").getBoolean(),
                DCTools.rc(node.node("lore").getList(String.class)),
                node.node("model-data").getInt(),
                rgb,
                itemStack
        );
    }

    @Override
    public void serialize(Type type, @Nullable CaseMaterial obj, ConfigurationNode node) throws SerializationException {
        // TODO finish serialization of CaseMaterial
    }
}
