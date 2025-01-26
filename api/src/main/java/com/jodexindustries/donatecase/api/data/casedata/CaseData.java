package com.jodexindustries.donatecase.api.data.casedata;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGui;
import com.jodexindustries.donatecase.api.tools.ProbabilityCollection;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.*;

/**
 * Class for implementing cases that are loaded into the plugin's memory.
 */
@Getter
@Setter
@ConfigSerializable
public class CaseData implements Cloneable {

    private transient String caseType;

    @Setting("DisplayName")
    private String caseDisplayName;

    @Setting("Animation")
    @Required
    private String animation;

    @Setting("Items")
    private Map<String, CaseDataItem> items;

    @Setting("Hologram")
    private Hologram hologram;

    @Setting("LevelGroups")
    private Map<String, Integer> levelGroups;

    @Setting("Gui")
    private CaseGui caseGui;

    @Setting("NoKeyActions")
    private List<String> noKeyActions;

    @Setting("OpenType")
    private OpenType openType = OpenType.GUI;

    @Setting("AnimationSettings")
    private ConfigurationNode animationSettings;

    /**
     * Get case item
     *
     * @param name item name
     * @return item
     */
    @Nullable
    public CaseDataItem getItem(String name) {
        return items.getOrDefault(name, null);
    }

    /**
     * Get random item from case
     *
     * @return Random item
     */
    public CaseDataItem getRandomItem() {
        ProbabilityCollection<CaseDataItem> collection = new ProbabilityCollection<>();
        for (CaseDataItem item : items.values()) {
            double chance = item.getChance();
            if(chance > 0) collection.add(item, chance);
        }
        return collection.get();
    }

    /**
     * Checks if the current collection of items contains any "real" items.
     * A "real" item is defined as an item with a chance greater than 0.
     *
     * @return {@code true} if all items in the collection have a chance greater than 0, {@code false} otherwise.
     */
    public boolean hasRealItems() {
        return items.values().stream().anyMatch(item -> item.getChance() > 0);
    }

    @Override
    public CaseData clone() {
        try {
            CaseData clonedCaseData = (CaseData) super.clone();

            if(this.items != null) clonedCaseData.items = cloneItemsMap(this.items);
            if(this.caseGui != null) clonedCaseData.caseGui = this.caseGui.clone();

            return clonedCaseData;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public String toString() {
        return "CaseData{" +
                "caseType='" + caseType + '\'' +
                ", caseDisplayName='" + caseDisplayName + '\'' +
                ", animation='" + animation + '\'' +
                ", items=" + items +
                ", hologram=" + hologram +
                ", levelGroups=" + levelGroups +
                ", caseGui=" + caseGui +
                ", noKeyActions=" + noKeyActions +
                ", openType=" + openType +
                ", animationSettings=" + animationSettings +
                '}';
    }

    /**
     * Clone method for CaseData deep clone
     */
    protected static Map<String, CaseDataItem> cloneItemsMap(Map<String, CaseDataItem> originalMap) {
        Map<String, CaseDataItem> clonedMap = new HashMap<>();
        for (Map.Entry<String, CaseDataItem> entry : originalMap.entrySet()) {
            clonedMap.put(entry.getKey(), entry.getValue().clone());
        }
        return clonedMap;
    }

    /**
     * Class to implement information about case opening histories
     */
    @Setter
    @Getter
    @DatabaseTable(tableName = "history_data")
    public static class History {
        @DatabaseField(columnName = "id") private int id;
        @DatabaseField(columnName = "item") private String item;
        @DatabaseField(columnName = "player_name") private String playerName;
        @DatabaseField(columnName = "time") private long time;
        @DatabaseField(columnName = "group") private String group;
        @DatabaseField(columnName = "case_type") private String caseType;
        @DatabaseField(columnName = "action") private String action;

        /**
         * Default constructor
         *
         * @param item       Item name
         * @param caseType   Case type
         * @param playerName Player name
         * @param time       Timestamp
         * @param group      Group name
         * @param action     Action name
         */
        public History(String item, String caseType, String playerName, long time, String group, String action) {
            this.item = item;
            this.playerName = playerName;
            this.time = time;
            this.group = group;
            this.caseType = caseType;
            this.action = action;
        }

        public History() {}

        @Override
        public History clone() {
            try {
                return (History) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

    }

    /**
     * Class for the implementation of holograms of the case.
     */
    @Getter
    @ConfigSerializable
    public static class Hologram {
        @Setting("Toggle")
        private boolean enabled;
        @Setting("Height")
        private double height;
        @Setting("Range")
        private int range;
        @Setting("Message")
        private List<String> messages;

    }
}
