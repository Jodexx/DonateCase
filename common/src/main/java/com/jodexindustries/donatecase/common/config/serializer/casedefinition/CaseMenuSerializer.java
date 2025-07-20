package com.jodexindustries.donatecase.common.config.serializer.casedefinition;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMaterial;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMenu;
import com.jodexindustries.donatecase.api.tools.DCTools;
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

//        if (!item.type().equalsIgnoreCase("DEFAULT")) {
//            Optional<TypedItem> typedItem = DCAPI.getInstance().getGuiTypedItemManager().getFromString(item.toString());
//            if (typedItem.isPresent()) {
//                if (typedItem.get().loadOnCase()) {
//                    material.itemStack(DCAPI.getInstance().getPlatform().getTools().loadCaseItem(item.material().id()));
//                }
//            }
//        }

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
                    getItemSlots(node.node("slots"))
            );
        }

        @Override
        public void serialize(Type type, CaseMenu.@Nullable Item obj, ConfigurationNode node) throws SerializationException {
            if (obj == null) return;

            node.node("type").set(obj.type());
            node.node("material").set(CaseMaterial.class, obj.material());
            node.node("slots").setList(Integer.class, obj.slots());
        }

        private List<Integer> getItemSlots(ConfigurationNode node) throws SerializationException {
            if (node.isList()) {
                return getItemSlotsListed(node.getList(String.class));
            } else {
                return getItemSlotsRanged(node.getString());
            }
        }

        private List<Integer> getItemSlotsListed(List<String> temp) {
            if (temp == null || temp.isEmpty()) return Collections.emptyList();

            List<Integer> slots = new ArrayList<>();
            for (String slot : temp) {
                String[] values = slot.split("-", 2);
                try {
                    int start = Integer.parseInt(values[0]);
                    int end = (values.length == 2) ? Integer.parseInt(values[1]) : start;
                    addRange(slots, start, end);
                } catch (NumberFormatException ignored) {
                }
            }
            return slots;
        }

        private List<Integer> getItemSlotsRanged(String slots) {
            if (slots == null || slots.isEmpty()) return Collections.emptyList();

            String[] slotArgs = slots.split("-");
            try {
                int start = Integer.parseInt(slotArgs[0]);
                int end = (slotArgs.length >= 2) ? Integer.parseInt(slotArgs[1]) : start;
                List<Integer> result = new ArrayList<>();
                addRange(result, start, end);
                return result;
            } catch (NumberFormatException e) {
                return Collections.emptyList();
            }
        }

        private void addRange(List<Integer> slots, int start, int end) {
            for (int i = start; i <= end; i++) {
                slots.add(i);
            }
        }

    }
}
