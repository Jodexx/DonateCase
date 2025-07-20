package com.jodexindustries.donatecase.api.data.casedata.gui;

import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMenu;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Accessors(fluent = true)
@Setter
@Getter
@Deprecated
public class CaseGui implements Cloneable {

    private String title;
    private int size;
    private int updateRate;
    private transient Map<String, Item> items;

    @Nullable
    public String getItemTypeBySlot(int slot) {
        for (Item item : items.values()) {
            if (item.slots.contains(slot)) return item.type;
        }
        return null;
    }

    public static CaseGui fromMenu(CaseMenu menu) {
        CaseGui caseGui = new CaseGui();

        caseGui.items = fromMenu(menu.items());
        caseGui.size = menu.size();
        caseGui.title = menu.title();
        caseGui.updateRate = menu.updateRate();

        return caseGui;
    }

    public static CaseMenu toMenu(CaseGui caseGui) {
        return new CaseMenu(
                "default_menu",
                caseGui.title,
                caseGui.size,
                caseGui.updateRate,
                toMenu(caseGui.items)
        );
    }

    private static Map<String, Item> fromMenu(Map<String, CaseMenu.Item> itemMap) {
        return itemMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> Item.fromMenu(entry.getValue()), (a, b) -> b));
    }

    private static Map<String, CaseMenu.Item> toMenu(Map<String, Item> itemMap) {
        return itemMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> Item.toMenu(entry.getValue()), (a, b) -> b));
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

    @Accessors(fluent = true)
    @Getter
    @Setter
    public static class Item implements Cloneable {

        private ConfigurationNode node;
        private String type;
        private CaseDataMaterial material;
        private transient List<Integer> slots;

        public static Item fromMenu(CaseMenu.Item item) {
            Item oldItem = new Item();

            oldItem.node = item.node();
            oldItem.slots = item.slots();
            oldItem.type = item.type();
            oldItem.material = CaseDataMaterial.fromMaterial(item.material());

            return oldItem;
        }

        public static CaseMenu.Item toMenu(Item old) {

            return new CaseMenu.Item(
                    old.node,
                    String.valueOf(old.node.key()),
                    old.type,
                    CaseDataMaterial.toMaterial(old.material),
                    old.slots
                    );
        }

        @Override
        public Item clone() {
            try {
                Item cloned = (Item) super.clone();
                cloned.material(material.clone());
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