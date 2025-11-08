package com.jodexindustries.donatecase.common.config.serializer;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import com.jodexindustries.donatecase.api.tools.DCTools;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class CaseDataMaterialSerializer implements TypeSerializer<CaseDataMaterial> {

    @Deprecated
    @Override
    public CaseDataMaterial deserialize(Type type, ConfigurationNode node) throws SerializationException {
        CaseDataMaterial material = new CaseDataMaterial();

        material.id(node.node("ID").getString());
        material.displayName(node.node("DisplayName").getString());
        material.enchanted(node.node("Enchanted").getBoolean());
        material.lore(node.node("Lore").getList(String.class));
        material.modelData(node.node("ModelData").getInt(-1));

        ConfigurationNode rgbNode = node.node("Rgb");
        Integer[] rgb = rgbNode.isList() ? rgbNode.get(Integer[].class, new Integer[0]) : DCTools.parseRGB(rgbNode.getString());
        material.rgb(rgb);

        material.itemStack(DCAPI.getInstance().getPlatform().getTools().loadCaseItem(material.id()));

        return material;
    }

    @Deprecated
    @Override
    public void serialize(Type type, CaseDataMaterial obj, ConfigurationNode node) {
    }

    @Deprecated
    @Override
    public @Nullable CaseDataMaterial emptyValue(Type specificType, ConfigurationOptions options) {
        return new CaseDataMaterial();
    }
}
