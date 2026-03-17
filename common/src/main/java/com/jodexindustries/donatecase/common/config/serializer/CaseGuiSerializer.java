package com.jodexindustries.donatecase.common.config.serializer;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGui;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItem;
import com.jodexindustries.donatecase.api.tools.DCTools;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.*;

public class CaseGuiSerializer implements TypeSerializer<CaseGui> {

    @Deprecated
    @Override
    public CaseGui deserialize(Type type, ConfigurationNode source) throws SerializationException {
        CaseGui caseGui = new CaseGui();

        String title = source.node("Title").getString();
        int updateRate = source.node("UpdateRate").getInt();
        int size = source.node("Size").getInt();

        caseGui.title(title);
        caseGui.updateRate(updateRate);
        caseGui.size(size);

        if (!DCTools.isValidGuiSize(size)) {
            caseGui.size(54);
            DCAPI.getInstance().getPlatform().getLogger().warning("Wrong GUI size: " + size);
        }

        Map<String, CaseGui.Item> itemMap = new HashMap<>();

        ConfigurationNode itemsNode = source.node("Items");

        Set<Integer> slots = new HashSet<>();

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : itemsNode.childrenMap().entrySet()) {
            CaseGui.Item item = loadGUIItem(String.valueOf(entry.getKey()), entry.getValue(), slots);
            if (item != null) itemMap.put((String) item.node().key(), item);
        }

        caseGui.items(itemMap);
        return caseGui;
    }

    @Override
    public void serialize(Type type, @Nullable CaseGui obj, ConfigurationNode target) {

    }

    @Deprecated
    private CaseGui.Item loadGUIItem(String i, @NotNull ConfigurationNode itemSection, Set<Integer> currentSlots) throws SerializationException {
        CaseGui.Item item = itemSection.get(CaseGui.Item.class);
        if (item == null) return null;

        if (item.slots().isEmpty()) {
            DCAPI.getInstance().getPlatform().getLogger().warning("Item " + i + " has no specified slots");
            return null;
        }

        if (item.slots().removeIf(currentSlots::contains))
            DCAPI.getInstance().getPlatform().getLogger().warning("Item " + i + " contains duplicated slots, removing..");

        currentSlots.addAll(item.slots());

        CaseDataMaterial material = itemSection.get(CaseDataMaterial.class);
        if (material == null) return null;

        if (!item.type().equalsIgnoreCase("DEFAULT")) {
            Optional<TypedItem> typedItem = DCAPI.getInstance().getGuiTypedItemManager().getFromString(item.toString());
            if (typedItem.isPresent()) {
                if (typedItem.get().loadOnCase()) {
                    material.itemStack(DCAPI.getInstance().getPlatform().getTools().loadCaseItem(item.material().id()));
                }
            }
        }

        return item;
    }

    public static class Item implements TypeSerializer<CaseGui.Item> {

        @Deprecated
        @Override
        public CaseGui.Item deserialize(Type type, ConfigurationNode source) throws SerializationException {
            CaseGui.Item item = new CaseGui.Item();

            String itemType = source.node("Type").getString();

            CaseDataMaterial material = source.node("Material").get(CaseDataMaterial.class);
            item.type(itemType);
            item.material(material);
            item.node(source);

            item.slots(SerializerUtil.intNode((source.node("Slots"))));
            return item;
        }

        @Deprecated
        @Override
        public void serialize(Type type, CaseGui.@Nullable Item obj, ConfigurationNode target) {

        }

    }
}