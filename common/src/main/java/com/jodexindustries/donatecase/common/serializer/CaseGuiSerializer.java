package com.jodexindustries.donatecase.common.serializer;

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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CaseGuiSerializer implements TypeSerializer<CaseGui> {

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

        if (itemsNode != null) {
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : itemsNode.childrenMap().entrySet()) {
                CaseGui.Item item = loadGUIItem(String.valueOf(entry.getKey()), entry.getValue(), slots);
                if (item != null) itemMap.put((String) item.node().key(), item);
            }
        }

        caseGui.items(itemMap);
        return caseGui;
    }

    @Override
    public void serialize(Type type, @Nullable CaseGui obj, ConfigurationNode target) {

    }

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

        @Override
        public CaseGui.Item deserialize(Type type, ConfigurationNode source) throws SerializationException {
            CaseGui.Item item = new CaseGui.Item();

            String itemType = source.node("Type").getString();

            CaseDataMaterial material = source.node("Material").get(CaseDataMaterial.class);
            item.type(itemType);
            item.material(material);
            item.node(source);

            item.slots(getItemSlots(source));
            return item;
        }

        @Override
        public void serialize(Type type, CaseGui.@Nullable Item obj, ConfigurationNode target) {

        }

        private List<Integer> getItemSlots(ConfigurationNode itemSection) throws SerializationException {
            if (itemSection.node("Slots").isList()) {
                return getItemSlotsListed(itemSection);
            } else {
                return getItemSlotsRanged(itemSection);
            }
        }

        private List<Integer> getItemSlotsListed(ConfigurationNode itemSection) throws SerializationException {
            List<Integer> slots = new ArrayList<>();
            List<String> temp = itemSection.node("Slots").getList(String.class);
            if (temp != null) {
                for (String slot : temp) {
                    String[] values = slot.split("-", 2);
                    if (values.length == 2) {
                        for (int i = Integer.parseInt(values[0]); i <= Integer.parseInt(values[1]); i++) {
                            slots.add(i);
                        }
                    } else {
                        slots.add(Integer.parseInt(slot));
                    }
                }
            }
            return slots;
        }

        private List<Integer> getItemSlotsRanged(ConfigurationNode itemSection) {
            String slots = itemSection.node("Slots").getString();

            if (slots == null || slots.isEmpty()) return new ArrayList<>();

            String[] slotArgs = slots.split("-");
            int range1 = Integer.parseInt(slotArgs[0]);
            int range2 = slotArgs.length >= 2 ? Integer.parseInt(slotArgs[1]) : range1;
            return IntStream.rangeClosed(range1, range2).boxed().collect(Collectors.toList());
        }
    }
}