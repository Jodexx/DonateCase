package com.jodexindustries.donatecase.api.data;

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
            return getMaterial().getModelData();
        }

        /**
         * @deprecated Use {@link CaseData.Item.Material#setModelData(int)}
         */
        @Deprecated
        public void setModelData(int modelData) {
            getMaterial().setModelData(modelData);
        }

        /**
         * @deprecated Use {@link CaseData.Item.Material#getRgb()}
         */
        @Deprecated
        public String[] getRgb() {
            return getMaterial().getRgb();
        }

        /**
         * @deprecated Use {@link CaseData.Item.Material#setRgb(String[])}
         */
        @Deprecated
        public void setRgb(String[] rgb) {
            getMaterial().setRgb(rgb);
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
                    '}';
        }
    }
}