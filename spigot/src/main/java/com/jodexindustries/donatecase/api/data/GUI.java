package com.jodexindustries.donatecase.api.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUI implements Cloneable {
    private String title;
    private int size;
    private Map<String, Item> items;
    private int updateRate;

    public GUI(String title, int size, Map<String, Item> items, int updateRate) {
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

    public Map<String, Item> getItems() {
        return items;
    }

    public void setItems(Map<String, Item> items) {
        this.items = items;
    }

    @Nullable
    public String getItemTypeBySlot(int slot) {
        for (GUI.Item item : items.values()) {
            if (item.getSlots().contains(slot)) return item.getType();
        }
        return null;
    }

    /**
     * Clone method for GUI deep clone
     */
    private static Map<String, Item> cloneItemsMap(Map<String, Item> originalMap) {
        Map<String, Item> clonedMap = new HashMap<>();
        for (Map.Entry<String, Item> entry : originalMap.entrySet()) {
            clonedMap.put(entry.getKey(), entry.getValue().clone());
        }
        return clonedMap;
    }

    @Override
    public GUI clone() {
        try {
            GUI clone = (GUI) super.clone();
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


    public static class Item implements Cloneable {
        private String itemName;
        private String type;
        private CaseData.Item.Material material;
        private List<Integer> slots;


        public Item(String itemName, String type, CaseData.Item.Material material, List<Integer> slots) {
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

        public CaseData.Item.Material getMaterial() {
            return material;
        }

        public void setMaterial(CaseData.Item.Material material) {
            this.material = material;
        }

        public List<Integer> getSlots() {
            return slots;
        }

        public void setSlots(List<Integer> slots) {
            this.slots = slots;
        }

        @Override
        public Item clone() {
            try {
                Item item = (Item) super.clone();
                item.material = this.material.clone();
                return item;
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