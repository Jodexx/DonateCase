package com.jodexindustries.donatecase.api.data.casedata;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGui;
import com.jodexindustries.donatecase.api.data.casedefinition.*;
import com.jodexindustries.donatecase.api.tools.ProbabilityCollection;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.*;

/**
 * Class for implementing cases that are loaded into the plugin's memory.
 */
@Accessors(fluent = true)
@Getter
@Setter
@ConfigSerializable
public class CaseData implements Cloneable {

    private transient String caseType;

    @Deprecated
    @Setting("DisplayName")
    private String caseDisplayName;

    @Deprecated
    @Setting("Animation")
    @Required
    private String animation;

    @Deprecated
    @Setting("Items")
    private Map<String, CaseDataItem> items;

    @Deprecated
    @Setting("Hologram")
    private Hologram hologram;

    @Deprecated
    @Setting("LevelGroups")
    private Map<String, Integer> levelGroups;

    @Deprecated
    @Setting("Gui")
    private CaseGui caseGui;

    @Deprecated
    @Setting("NoKeyActions")
    private List<String> noKeyActions;

    @Deprecated
    @Setting("OpenType")
    private OpenType openType = OpenType.GUI;

    @Deprecated
    @Setting("AnimationSettings")
    private ConfigurationNode animationSettings;

    @Deprecated
    @Setting("CooldownBeforeAnimation")
    private int cooldownBeforeStart;

    @Deprecated
    @Setting("HistoryDataSize")
    private int historyDataSize;

    /**
     * Get case item
     *
     * @param name item name
     * @return item
     */
    @Deprecated
    @Nullable
    public CaseDataItem getItem(String name) {
        return items.getOrDefault(name, null);
    }

    /**
     * Get random item from case
     *
     * @return Random item
     */
    @Deprecated
    public CaseDataItem getRandomItem() {
        ProbabilityCollection<CaseDataItem> collection = new ProbabilityCollection<>();
        for (CaseDataItem item : items.values()) {
            double chance = item.chance();
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
    @Deprecated
    public boolean hasRealItems() {
        return items.values().stream().anyMatch(item -> item.chance() > 0);
    }

    @Deprecated
    public static CaseData fromDefinition(CaseDefinition definition) {
        CaseSettings settings = definition.settings();

        CaseData caseData = new CaseData();

        caseData.caseType = settings.type();
        caseData.openType = settings.openType();
        caseData.animation = settings.animation();
        caseData.animationSettings = settings.animationSettings();
        caseData.levelGroups = settings.levelGroups().map();
        caseData.noKeyActions = settings.noKeyActions();
        caseData.caseDisplayName = settings.displayName();
        caseData.cooldownBeforeStart = settings.cooldownBeforeAnimation();
        caseData.historyDataSize = settings.historyDataSize();

        caseData.hologram = Hologram.fromDefinition(settings.hologram());

        caseData.caseGui = CaseGui.fromMenu(definition.defaultMenu());

        caseData.items = fromDefinition(definition.items());

        return caseData;
    }

    @Deprecated
    public static CaseDefinition toDefinition(CaseData data) {
        CaseSettings.Hologram hologram = new CaseSettings.Hologram(
                data.hologram.node,
                data.hologram.enabled,
                data.hologram.height,
                data.hologram.range,
                data.hologram.messages
        );

        CaseSettings settings = new CaseSettings(
                data.caseType,
                "default_menu",
                data.animation,
                hologram,
                new CaseSettings.LevelGroups(data.levelGroups),
                data.noKeyActions,
                data.openType,
                data.animationSettings,
                data.cooldownBeforeStart,
                data.historyDataSize,
                data.caseDisplayName
        );

        // Items
        CaseItems items = toDefinition(data.items);

        // Menu
        List<CaseMenu> menus = new ArrayList<>();
        if (data.caseGui != null) {
            menus.add(CaseGui.toMenu(data.caseGui));
        }

        return new CaseDefinition(settings, items, menus);
    }


    @Deprecated
    private static Map<String, CaseDataItem> fromDefinition(CaseItems items) {
        Map<String, CaseDataItem> old = new HashMap<>();

        for (Map.Entry<String, CaseItem> entry : items.items().entrySet()) {
            old.put(entry.getKey(), CaseDataItem.fromItem(entry.getValue()));
        }

        return old;
    }

    @Deprecated
    private static CaseItems toDefinition(Map<String, CaseDataItem> oldItems) {
        Map<String, CaseItem> items = new HashMap<>();

        for (Map.Entry<String, CaseDataItem> entry : oldItems.entrySet()) {
            items.put(entry.getKey(), CaseDataItem.toItem(entry.getValue()));
        }

        return new CaseItems(items);
    }


    @Deprecated
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

    @Deprecated
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
    @Deprecated
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
    @Accessors(fluent = true, chain = false)
    @Getter
    @Setter
    @DatabaseTable(tableName = "history_data")
    public static class History {

        @DatabaseField(columnName = "id")
        @Deprecated
        private int id;

        @DatabaseField(columnName = "item")
        private String item;

        @DatabaseField(columnName = "player_name")
        private String playerName;

        @DatabaseField(columnName = "time")
        private long time;

        @DatabaseField(columnName = "group")
        private String group;

        @DatabaseField(columnName = "case_type")
        private String caseType;

        @DatabaseField(columnName = "action")
        private String action;

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
        public String toString() {
            return "History{" +
                    "item='" + item + '\'' +
                    ", playerName='" + playerName + '\'' +
                    ", time=" + time +
                    ", group='" + group + '\'' +
                    ", caseType='" + caseType + '\'' +
                    ", action='" + action + '\'' +
                    '}';
        }

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
    @Accessors(fluent = true)
    @Getter
    @ConfigSerializable
    @Deprecated
    public static class Hologram {

        @Deprecated
        @Setting(nodeFromParent = true)
        private ConfigurationNode node;

        @Deprecated
        @Setting("Toggle")
        private boolean enabled;

        @Deprecated
        @Setting("Height")
        private double height;

        @Deprecated
        @Setting("Range")
        private int range;

        @Deprecated
        @Setting("Message")
        private List<String> messages;

        @Deprecated
        public static CaseSettings.Hologram toDefinition(Hologram hologram) {
            return new CaseSettings.Hologram(
                    hologram.node,
                    hologram.enabled,
                    hologram.height,
                    hologram.range,
                    hologram.messages
            );
        }

        @Deprecated
        public static Hologram fromDefinition(CaseSettings.Hologram hologram) {
            CaseData.Hologram old = new Hologram();
            old.enabled = hologram.enabled();
            old.range = hologram.range();
            old.messages = hologram.message();
            old.height = hologram.height();
            old.node = hologram.node();
            return old;
        }
    }
}
