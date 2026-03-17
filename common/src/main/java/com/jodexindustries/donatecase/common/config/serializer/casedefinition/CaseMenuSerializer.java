package com.jodexindustries.donatecase.common.config.serializer.casedefinition;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItem;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMaterial;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMenu;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.common.config.serializer.SerializerUtil;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.*;

public class CaseMenuSerializer implements TypeSerializer<CaseMenu> {

    @Override
    public CaseMenu deserialize(Type type, ConfigurationNode node) throws SerializationException {
        Map<String, CaseMenu.Item> items = new HashMap<>();

        ConfigurationNode itemsNode = node.node("items");

        if (itemsNode.isMap()) {
            Set<Integer> slots = new HashSet<>();

            for (Map.Entry<Object, ? extends ConfigurationNode> entry : itemsNode.childrenMap().entrySet()) {
                String key = String.valueOf(entry.getKey());
                CaseMenu.Item item = loadItem(key, entry.getValue(), slots);
                if (item == null) continue;

                items.put(key, item);
            }
        }

        int size = node.node("size").getInt();

        if (!DCTools.isValidGuiSize(size)) {
            size = 54;
            DCAPI.getInstance().getPlatform().getLogger().warning("Wrong GUI size: " + size + ". Using 54");
        }

        return new CaseMenu(
                node.node("id").getString(),
                node.node("title").getString(),
                size,
                node.node("update-rate").getInt(),
                items
        );
    }

    @Override
    public void serialize(Type type, @Nullable CaseMenu obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) return;

        node.node("id").set(obj.id());
        node.node("title").set(obj.title());
        node.node("size").set(obj.size());
        node.node("update-rate").set(obj.updateRate());

        ConfigurationNode itemsNode = node.node("items");
        Map<String, CaseMenu.Item> items = obj.items();

        if (items != null) {
            for (Map.Entry<String, CaseMenu.Item> entry : items.entrySet()) {
                itemsNode.node(entry.getKey()).set(CaseMenu.Item.class, entry.getValue());
            }
        }
    }

    private CaseMenu.Item loadItem(String i, @NotNull ConfigurationNode itemSection, Set<Integer> currentSlots) throws SerializationException {
        CaseMenu.Item item = itemSection.get(CaseMenu.Item.class);
        if (item == null) return null;

        if (item.slots().isEmpty()) {
            DCAPI.getInstance().getPlatform().getLogger().warning("Item " + i + " has no specified slots");
            return null;
        }

        if (item.slots().removeIf(currentSlots::contains))
            DCAPI.getInstance().getPlatform().getLogger().warning("Item " + i + " contains duplicated slots, removing..");

        currentSlots.addAll(item.slots());

        if (!item.type().equalsIgnoreCase("DEFAULT")) {
            Optional<TypedItem> typedItem = DCAPI.getInstance().getGuiTypedItemManager().getFromString(item.type());
            if (typedItem.isPresent()) {
                if (typedItem.get().loadOnCase()) {
                    item.material().itemStack(DCAPI.getInstance().getPlatform().getTools().loadCaseItem(item.material().id()));
                }
            }
        } else {
            item.material().itemStack(null);
        }

        return item;
    }

    public static class Item implements TypeSerializer<CaseMenu.Item> {

        @Override
        public CaseMenu.Item deserialize(Type type, ConfigurationNode node) throws SerializationException {
            return new CaseMenu.Item(
                    node,
                    String.valueOf(node.key()),
                    node.node("type").getString(),
                    node.node("material").get(CaseMaterial.class),
                    SerializerUtil.intNode(node.node("slots"))
            );
        }

        @Override
        public void serialize(Type type, CaseMenu.@Nullable Item obj, ConfigurationNode node) throws SerializationException {
            if (obj == null) return;

            node.node("type").set(obj.type());
            node.node("material").set(CaseMaterial.class, obj.material());
            node.node("slots").setList(Integer.class, obj.slots());
        }

    }
}
