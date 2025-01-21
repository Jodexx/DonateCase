package com.jodexindustries.donatecase.api.data.serializer;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGui;
import com.jodexindustries.donatecase.api.data.casedata.gui.GuiTypedItem;
import com.jodexindustries.donatecase.api.tools.DCTools;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CaseGuiSerializer extends CaseSerializer<CaseGui> {

    @Override
    public CaseGui deserialize(Type type, ConfigurationNode source) throws SerializationException {
        CaseGui caseGui = source.get(CaseGui.class);
        if(caseGui != null) {
            int size = caseGui.getSize();
            if (!DCTools.isValidGuiSize(size)) {
                caseGui.setSize(54);
                DCAPI.getInstance().getPlatform().getLogger().warning("Wrong GUI size: " + size);
            }

            Map<String, CaseGui.Item> itemMap = new HashMap<>();

            ConfigurationNode itemsNode = source.node("Items");

            Set<Integer> slots = new HashSet<>();

            if(itemsNode != null) {
                for (Map.Entry<Object, ? extends ConfigurationNode> entry : itemsNode.childrenMap().entrySet()) {
                    CaseGui.Item item = loadGUIItem(String.valueOf(entry.getKey()), entry.getValue(), slots);
                    if(item != null) itemMap.put(item.getItemName(), item);
                }
            }

            caseGui.setItems(itemMap);
        }
        return caseGui;
    }

    @Override
    public void serialize(Type type, @Nullable CaseGui obj, ConfigurationNode target) throws SerializationException {

    }

    private CaseGui.Item loadGUIItem(String i, @NotNull ConfigurationNode itemSection, Set<Integer> currentSlots) throws SerializationException {
        CaseGui.Item item = itemSection.get(CaseGui.Item.class);
        if(item == null) return null;

        if(item.getSlots().isEmpty()) {
            DCAPI.getInstance().getPlatform().getLogger().warning("Item " + i + " has no specified slots");
            return null;
        }

        if (item.getSlots().removeIf(currentSlots::contains))
            DCAPI.getInstance().getPlatform().getLogger().warning("Item " + i + " contains duplicated slots, removing..");

        currentSlots.addAll(item.getSlots());

        CaseDataMaterial material = itemSection.get(CaseDataMaterial.class);
        if(material == null) return null;

        if(!item.getType().equalsIgnoreCase("DEFAULT")) {
            GuiTypedItem typedItem = DCAPI.getInstance().getGuiTypedItemManager().getFromString(item.getType());
            if (typedItem != null) {
                if(typedItem.isLoadOnCase())  {
                    material.setItemStack(DCAPI.getInstance().getPlatform().getTools().loadCaseItem(item.getMaterial().getId()));
                }
            }
        }

        return item;
    }

    public static class Item extends CaseSerializer<CaseGui.Item> {

        @Override
        public CaseGui.Item deserialize(Type type, ConfigurationNode source) throws SerializationException {
            CaseGui.Item item = source.get(CaseGui.Item.class);
            if(item == null) return null;

            item.setSlots(getItemSlots(source));
            return item;
        }

        @Override
        public void serialize(Type type, CaseGui.@Nullable Item obj, ConfigurationNode target) throws SerializationException {

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

            if(slots == null || slots.isEmpty()) return new ArrayList<>();

            String[] slotArgs = slots.split("-");
            int range1 = Integer.parseInt(slotArgs[0]);
            int range2 = slotArgs.length >= 2 ? Integer.parseInt(slotArgs[1]) : range1;
            return IntStream.rangeClosed(range1, range2).boxed().collect(Collectors.toList());
        }
    }
}
