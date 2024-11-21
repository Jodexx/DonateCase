package com.jodexindustries.donatecase.api.data.casedata.gui;

import com.jodexindustries.donatecase.api.data.casedata.CCloneable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUI<M extends CCloneable> implements CCloneable {
    private String title;
    private int size;
    private Map<String, Item<M>> items;
    private int updateRate;

    public GUI(String title, int size, Map<String, Item<M>> items, int updateRate) {
        this.title = title;
        this.size = size;
        this.items = items;
        this.updateRate = updateRate;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Map<String, Item<M>> getItems() {
        return items;
    }

    public void setItems(Map<String, Item<M>> items) {
        this.items = items;
    }

    @Nullable
    public String getItemTypeBySlot(int slot) {
        for (GUI.Item<M> item : items.values()) {
            if (item.getSlots().contains(slot)) return item.getType();
        }
        return null;
    }

    /**
     * Clone method for GUI deep clone
     */
    private Map<String, Item<M>> cloneItemsMap(Map<String, Item<M>> originalMap) {
        Map<String, Item<M>> clonedMap = new HashMap<>();
        for (Map.Entry<String, Item<M>> entry : originalMap.entrySet()) {
            clonedMap.put(entry.getKey(), entry.getValue().clone());
        }
        return clonedMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public GUI<M> clone() {
        try {
            GUI<M> clone = (GUI<M>) super.clone();
            clone.items = cloneItemsMap(this.items);

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public int getUpdateRate() {
        return updateRate;
    }

    public void setUpdateRate(int updateRate) {
        this.updateRate = updateRate;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
    }


    public static class Item<M extends CCloneable> implements CCloneable {
        private String itemName;
        private String type;
        private M material;
        private List<Integer> slots;


        public Item(String itemName, String type, M material, List<Integer> slots) {
            this.itemName = itemName;
            this.type = type;
            this.material = material;
            this.slots = slots;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        @NotNull
        public String getType() {
            return type;
        }

        public void setType(@NotNull String type) {
            this.type = type;
        }

        public M getMaterial() {
            return material;
        }

        public void setMaterial(M material) {
            this.material = material;
        }

        public List<Integer> getSlots() {
            return slots;
        }

        public void setSlots(List<Integer> slots) {
            this.slots = slots;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Item<M> clone() {
            try {
                Item<M> cloned = (Item<M>) super.clone();
                cloned.setMaterial((M) material.clone());
                return cloned;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }

        @Override
        public String toString() {
            return "Item{" +
                    "itemName='" + itemName + '\'' +
                    ", type='" + type + '\'' +
                    ", material=" + material +
                    ", slots=" + slots +
                    '}';
        }
    }
}