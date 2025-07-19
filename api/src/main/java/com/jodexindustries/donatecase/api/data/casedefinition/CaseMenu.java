package com.jodexindustries.donatecase.api.data.casedefinition;

import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGui;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;
import java.util.Map;

@Accessors(fluent = true)
@Getter
@Setter
public class CaseMenu {

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

    @Accessors(fluent = true)
    @Getter
    @Setter
    public static class Item {

        private ConfigurationNode node;

        private String type;

        private CaseMaterial material;

        private List<Integer> slots;

        public Item(ConfigurationNode node, String type, CaseMaterial material, List<Integer> slots) {
            this.node = node;
            this.type = type;
            this.material = material;
            this.slots = slots;
        }

        @Override
        public Item clone() {
            try {
                Item cloned = (Item) super.clone();
                cloned.material = material.clone();
                return cloned;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }
}
