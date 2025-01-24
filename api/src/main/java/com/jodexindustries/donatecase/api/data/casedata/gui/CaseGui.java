package com.jodexindustries.donatecase.api.data.casedata.gui;

import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@ConfigSerializable
public class CaseGui {
    @Setting("Title")
    private String title;
    @Setting("Size")
    private int size;
    @Setting("UpdateRate")
    private int updateRate;
    @Setting("Items")
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
            clone.items = cloneItemsMap(this.items);

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Getter
    @Setter
    @ConfigSerializable
    public static class Item {
        @Setting(nodeFromParent = true)
        private ConfigurationNode node;
        @Setting("Type")
        private String type;
        @Setting("Material")
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

    }
}