package com.jodexindustries.donatecase.api.data.casedefinition;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Accessors(fluent = true, chain = false)
@Getter
@Setter
public class CaseMenu implements Cloneable {

    private String id;

    private String title;

    private int size;

    private int updateRate;

    private Map<String, Item> items;

    public CaseMenu(String id, String title, int size, int updateRate, Map<String, Item> items) {
        this.id = id;
        this.title = title;
        this.size = size;
        this.updateRate = updateRate;
        this.items = items;
    }

    public String getItemTypeBySlot(int slot) {
        for (Item item : items.values()) {
            if (item.slots.contains(slot)) return item.type;
        }
        return null;
    }

    @Override
    public CaseMenu clone() {
        try {
            CaseMenu clone = (CaseMenu) super.clone();
            clone.items = this.items.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().clone(), (a, b) -> b));
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Accessors(fluent = true)
    @Getter
    @Setter
    public static class Item {

        private ConfigurationNode node;

        private String name;

        private String type;

        private CaseMaterial material;

        private List<Integer> slots;

        public Item(ConfigurationNode node, String name, String type, CaseMaterial material, List<Integer> slots) {
            this.node = node;
            this.name = name;
            this.type = type;
            this.material = material;
            this.slots = slots;
        }

        @Override
        public Item clone() {
            try {
                Item cloned = (Item) super.clone();
                cloned.node = node.copy();
                cloned.material = material.clone();
                return cloned;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }
}
