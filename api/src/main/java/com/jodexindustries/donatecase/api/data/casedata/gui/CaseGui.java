package com.jodexindustries.donatecase.api.data.casedata.gui;

import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class CaseGui implements Cloneable {

    private String title;
    private int size;
    private int updateRate;
    private transient Map<String, Item> items;

    @Nullable
    public String getItemTypeBySlot(int slot) {
        for (Item item : items.values()) {
            if (item.getSlots().contains(slot)) return item.getType();
        }
        return null;
    }

    /**
     * Clone method for GUI deep clone
     */
    private Map<String, Item> cloneItemsMap(Map<String, Item> originalMap) {
        Map<String, Item> clonedMap = new HashMap<>();
        for (Map.Entry<String, Item> entry : originalMap.entrySet()) {
            clonedMap.put(entry.getKey(), entry.getValue().clone());
        }
        return clonedMap;
    }

    @Override
    public CaseGui clone() {
        try {
            CaseGui clone = (CaseGui) super.clone();
            if (this.items != null) clone.items = cloneItemsMap(this.items);

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return "CaseGui{" +
                "title='" + title + '\'' +
                ", size=" + size +
                ", updateRate=" + updateRate +
                ", items=" + items +
                '}';
    }

    @Getter
    @Setter
    public static class Item implements Cloneable {

        private ConfigurationNode node;
        private String type;
        private CaseDataMaterial material;
        private transient List<Integer> slots;

        @Override
        public Item clone() {
            try {
                Item cloned = (Item) super.clone();
                cloned.setMaterial(material.clone());
                return cloned;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }

        @Override
        public String toString() {
            return "Item{" +
                    "node=" + node +
                    ", type='" + type + '\'' +
                    ", material=" + material +
                    ", slots=" + slots +
                    '}';
        }
    }
}