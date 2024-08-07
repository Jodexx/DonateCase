package com.jodexindustries.donatecase.api.data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GUI {
    private int size;
    private Map<String, Item> items;

    public GUI(int size, Map<String, Item> items) {
        this.size = size;
        this.items = items;
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


    public static class Item implements Cloneable {
        private String itemName;
        private String type;
        private CaseData.Item.Material material;
        private List<Integer> slots;
        private int modelData;
        private String[] rgb;


        public Item(String itemName, String type, CaseData.Item.Material material, List<Integer> slots, int modelData, String[] rgb) {
            this.itemName = itemName;
            this.type = type;
            this.material = material;
            this.slots = slots;
            this.modelData = modelData;
            this.rgb = rgb;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
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

        /**
         * @deprecated Use {@link CaseData.Item.Material#getModelData()}
         * @return item model data
         */
        @Deprecated
        public int getModelData() {
            return modelData;
        }

        public void setModelData(int modelData) {
            this.modelData = modelData;
        }

        public String[] getRgb() {
            return rgb;
        }

        public void setRgb(String[] rgb) {
            this.rgb = rgb;
        }


        @Override
        public Item clone() {
            try {
                Item item = (Item) super.clone();
                item.setMaterial(material.clone());
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
                    ", modelData=" + modelData +
                    ", rgb=" + Arrays.toString(rgb) +
                    '}';
        }
    }
}