package com.jodexindustries.donatecase.api.data.casedata.gui;

import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@ConfigSerializable
public class CaseGui {
    private String title;
    private int size;
    private int updateRate;
    private transient Map<String, Item> items;

    public CaseGui(String title, int size, Map<String, Item> items, int updateRate) {
        this.title = title;
        this.size = size;
        this.updateRate = updateRate;
        this.items = items;
    }

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
            clone.items = cloneItemsMap(this.items);

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
    }


    @Getter
    @Setter
    @ConfigSerializable
    public static class Item {
        @Setting(nodeFromParent = true)
        private String itemName;
        private String type;
        private CaseDataMaterial material;
        private transient List<Integer> slots;


        public Item(String itemName, String type, CaseDataMaterial material, List<Integer> slots) {
            this.itemName = itemName;
            this.type = type;
            this.material = material;
            this.slots = slots;
        }

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

    }
}