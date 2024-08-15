package com.jodexindustries.donatecase.api.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jodexindustries.donatecase.tools.ProbabilityCollection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Class for implementing cases that are loaded into the plugin's memory.
 */
public class CaseData implements Cloneable {
    private final String caseType;
    private String caseDisplayName;
    private String caseTitle;
    private String animation;
    private Map<String, Item> items;
    private HistoryData[] historyData;
    private Hologram hologram;
    private Map<String, Integer> levelGroups;
    private GUI gui;
    private List<String> noKeyActions;

    /**
     * Default constructor
     * @param caseType Case type
     * @param caseDisplayName Case display name
     * @param caseTitle Case title (GUI)
     * @param animation Animation name
     * @param items Items list
     * @param historyData History data array
     * @param hologram Hologram object
     * @param levelGroups Map with level groups
     * @param gui GUI object
     * @param noKeyActions NoKeyActions
     */
    public CaseData(String caseType, String caseDisplayName, String caseTitle,
                    String animation, Map<String,
            Item> items, HistoryData[] historyData, Hologram hologram, Map<String, Integer> levelGroups, GUI gui, List<String> noKeyActions) {
        this.caseType = caseType;
        this.caseDisplayName = caseDisplayName;
        this.caseTitle = caseTitle;
        this.animation = animation;
        this.items = items;
        this.historyData = historyData;
        this.hologram = hologram;
        this.levelGroups = levelGroups;
        this.gui = gui;
        this.noKeyActions = noKeyActions;
    }

    @Override
    public String toString() {
        return "CaseData{" +
                "caseType='" + caseType + '\'' +
                ", caseDisplayName='" + caseDisplayName + '\'' +
                ", caseTitle='" + caseTitle + '\'' +
                ", animation='" + animation + '\'' +
                ", items=" + items +
                ", historyData=" + Arrays.toString(historyData) +
                ", hologram=" + hologram +
                ", levelGroups=" + levelGroups +
                ", gui=" + gui +
                '}';
    }

    /**
     * Get case history data
     * @return history data
     */
    public HistoryData[] getHistoryData() {
        return historyData;
    }

    /**
     * Get case items
     * @return items
     */
    public Map<String, Item> getItems() {
        return items;
    }

    /**
     * Get case item
     * @param name item name
     * @return item
     */
    @Nullable
    public Item getItem(String name) {
        return items.getOrDefault(name, null);
    }

    /**
     * Get random item from case
     * @return Random item
     */
    public Item getRandomItem() {
        ProbabilityCollection<Item> collection = new ProbabilityCollection<>();
        for (Item item : items.values()) {
            collection.add(item, item.chance);
        }
        return collection.get();
    }

    /**
     * Set case history data
     * @param historyData history data
     */
    public void setHistoryData(HistoryData[] historyData) {
        this.historyData = historyData;
    }

    /**
     * Set case items
     * @param items map of CaseData.Item items
     */
    public void setItems(Map<String, Item> items) {
        this.items = items;
    }

    /**
     * Get animation
     * @return animation
     */
    @NotNull
    public String getAnimation() {
        return animation;
    }

    /**
     * Set animation
     * @param animation animation
     */
    public void setAnimation(String animation) {
        this.animation = animation;
    }

    /**
     * Get case title
     * @return title
     */
    public String getCaseTitle() {
        return caseTitle;
    }

    /**
     * Set case title
     * @param caseTitle title
     */
    public void setCaseTitle(String caseTitle) {
        this.caseTitle = caseTitle;
    }

    /**
     * Get case type
     * @deprecated
     * This method does not match the field name
     * <p> Use {@link CaseData#getCaseType()} instead</p>
     * @return case type
     *
     */
    @Deprecated
    public String getCaseName() {
        return caseType;
    }

    /**
     * Get case type
     * @return case type
     * @since 2.2.1.8
     */
    @NotNull
    public String getCaseType() {
        return caseType;
    }

    @Override
    public CaseData clone() {
        try {
            CaseData clonedCaseData = (CaseData) super.clone();

            // Deep clone the map of items
            clonedCaseData.items = cloneItemsMap(this.items);

            clonedCaseData.gui = this.gui.clone();

            // Deep clone the array of historyData
            clonedCaseData.historyData = cloneHistoryDataArray(this.historyData);

            return clonedCaseData;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Clone method for CaseData deep clone
     */
    private static HistoryData[] cloneHistoryDataArray(HistoryData[] originalArray) {
        HistoryData[] newArray = new HistoryData[originalArray.length];
        for (int i = 0; i < originalArray.length; i++) {
            if(originalArray[i] != null) {
                newArray[i] = originalArray[i].clone();
            }
        }
        return newArray;
    }

    /**
     * Clone method for CaseData deep clone
     */
    private static Map<String, Item> cloneItemsMap(Map<String, Item> originalMap) {
        Map<String, Item> clonedMap = new HashMap<>();
        for (Map.Entry<String, Item> entry : originalMap.entrySet()) {
            clonedMap.put(entry.getKey(), entry.getValue().clone());
        }
        return clonedMap;
    }

    /**
     * Get case display name (case.DisplayName path in case config)
     * @return case display name
     */

    public String getCaseDisplayName() {
        return caseDisplayName;
    }

    /**
     * Set case display name (case.DisplayName path in case config)
     * @param caseDisplayName new display name
     */

    public void setCaseDisplayName(String caseDisplayName) {
        this.caseDisplayName = caseDisplayName;
    }

    /**
     * Get case hologram
     * @return case hologram class
     */
    public Hologram getHologram() {
        return hologram;
    }

    /**
     * Set case hologram
     * @param hologram case hologram class
     */
    public void setHologram(Hologram hologram) {
        this.hologram = hologram;
    }

    /**
     * Get case LevelGroups (optional setting for each case)
     * @return map of LevelGroups
     */
    public Map<String, Integer> getLevelGroups() {
        return levelGroups;
    }

    /**
     * Set case LevelGroups (optional setting for each case)
     * @param levelGroups map of LevelGroups
     */
    public void setLevelGroups(Map<String, Integer> levelGroups) {
        this.levelGroups = levelGroups;
    }

    /**
     * Gets GUI storage object
     * @return GUI object
     */
    public GUI getGui() {
        return gui;
    }

    /**
     * Set GUI storage object
     * @param gui object
     */
    public void setGui(GUI gui) {
        this.gui = gui;
    }

    /**
     * Gets actions to be performed if a player tries to open a case without keys
     * @return List of actions
     * @since 2.2.4.3
     */
    public List<String> getNoKeyActions() {
        return noKeyActions;
    }

    /**
     * Set actions to be performed if a player tries to open a case without keys
     * @param noKeyActions List of actions
     * @since 2.2.4.3
     */
    public void setNoKeyActions(List<String> noKeyActions) {
        this.noKeyActions = noKeyActions;
    }

    /**
     * Class for the implementation of winning items from the case
     */
    public static class Item implements Cloneable {
        private final String itemName;
        private String group;
        private int chance;
        private int index;
        private Material material;
        private String giveType;
        private List<String> actions;
        private List<String> alternativeActions;
        private Map<String,RandomAction> randomActions;

        public Item(String itemName, String group, int chance, int index, Material material,
                    String giveType, List<String> actions, Map<String, RandomAction> randomActions, List<String> alternativeActions) {
            this.itemName = itemName;
            this.group = group;
            this.chance = chance;
            this.index = index;
            this.material = material;
            this.giveType = giveType;
            this.actions = actions;
            this.randomActions = randomActions;
            this.alternativeActions = alternativeActions;
        }

        /**
         * Get map of random actions
         * @return random actions
         */
        public Map<String, RandomAction> getRandomActions() {
            return randomActions;
        }

        /**
         * Get random action
         * @param name random action name
         * @return CaseData.RandomAction
         */
        @Nullable
        public RandomAction getRandomAction(String name) {
            return randomActions.getOrDefault(name, null);
        }

        /**
         * Set random actions
         * @param randomActions map of random actions
         */
        public void setRandomActions(Map<String, RandomAction> randomActions) {
            this.randomActions = randomActions;
        }

        /**
         * Get item actions
         * @return actions
         */
        public List<String> getActions() {
            return actions;
        }

        /**
         * Set item actions
         * @param actions actions
         */
        public void setActions(List<String> actions) {
            this.actions = actions;
        }

        /**
         * Get item give type
         * @return give type
         */
        public String getGiveType() {
            return giveType;
        }

        /**
         * Set item give type
         * @param giveType give type
         */
        public void setGiveType(String giveType) {
            this.giveType = giveType;
        }

        /**
         * Get item material (CaseData.Material)
         * @return CaseData.Material
         */
        @NotNull
        public Material getMaterial() {
            return material;
        }

        /**
         * Set item material (CaseData.Material)
         * @param material CaseData.Material
         */
        public void setMaterial(Material material) {
            this.material = material;
        }

        /**
         * Get item chance
         * @return chance
         */
        public int getChance() {
            return chance;
        }

        /**
         * Set item chance
         * @param chance chance
         */
        public void setChance(int chance) {
            this.chance = chance;
        }

        /**
         * Get item group
         * @return grouo
         */
        public String getGroup() {
            return group;
        }

        /**
         * Set item group
         * @param group group
         */
        public void setGroup(String group) {
            this.group = group;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "group='" + group + '\'' +
                    ", chance=" + chance +
                    ", material=" + material +
                    ", giveType='" + giveType + '\'' +
                    ", actions=" + actions +
                    ", randomActions=" + randomActions +
                    '}';
        }

        /**
         * Get item name (like path of item in case config)
         * @return item name
         */
        public String getItemName() {
            return itemName;
        }

        /**
         * Get alternative actions
         * These actions are performed when LevelGroups is enabled and the player's group has a higher level than the one they won from the case
         * @return list of actions
         */
        public List<String> getAlternativeActions() {
            return alternativeActions;
        }

        /**
         * Set alternative actions
         * These actions are performed when LevelGroups is enabled and the player's group has a higher level than the one they won from the case
         * @param alternativeActions list of actions
         */
        public void setAlternativeActions(List<String> alternativeActions) {
            this.alternativeActions = alternativeActions;
        }

        /**
         * Gets item index. <br/>
         * Used for items sorting
         * @return index
         */
        public int getIndex() {
            return index;
        }

        /**
         * Set item index <br/>
         * Used for items sorting
         * @param index item index
         */
        public void setIndex(int index) {
            this.index = index;
        }

        /**
         * Class to implement a random action
         */
        public static class RandomAction implements Cloneable {
            private int chance;
            private List<String> actions;
            private String displayName;

            public RandomAction(int chance, List<String> actions, String displayName) {
                this.chance = chance;
                this.actions = actions;
                this.displayName = displayName;
            }

            /**
             * Get random actions
             * @return random actions
             */
            public List<String> getActions() {
                return actions;
            }


            /**
             * Set random actions
             * @param actions random actions
             */
            public void setActions(List<String> actions) {
                this.actions = actions;
            }

            /**
             * Get random action chance
             * @return chance
             */
            public int getChance() {
                return chance;
            }

            /**
             * Set random action chance
             * @param chance chance
             */
            public void setChance(int chance) {
                this.chance = chance;
            }

            @Override
            public String toString() {
                return "RandomAction{" +
                        "chance=" + chance +
                        ", actions=" + actions +
                        '}';
            }
            @Override
            public RandomAction clone() {
                try {
                    return (RandomAction) super.clone();
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /**
             * Get display name of random action
             * Path in case config: RandomActions.(action).DisplayName
             * @return display name of random action
             */
            public String getDisplayName() {
                return displayName;
            }

            /**
             * Set display name of random action
             * Path in case config: RandomActions.(action).DisplayName
             * @param displayName display name of random action
             */
            public void setDisplayName(String displayName) {
                this.displayName = displayName;
            }
        }

        /**
         * Class for the implementation of the winning item material
         */
        public static class Material implements Cloneable {
            private String id;
            private ItemStack itemStack;
            private String displayName;
            private boolean enchanted;
            private List<String> lore;
            private int modelData;
            private String[] rgb;

            public Material(String id, ItemStack itemStack, String displayName, boolean enchanted,
                            List<String> lore, int modelData, String[] rgb) {
                this.itemStack = itemStack;
                this.displayName = displayName;
                this.enchanted = enchanted;
                this.id = id;
                this.lore = lore == null ? new ArrayList<>() : lore;
                this.modelData = modelData;
                this.rgb = rgb;
            }

            /**
             * Get win item itemStack
             * @return itemStack
             */
            @Nullable
            public ItemStack getItemStack() {
                return itemStack;
            }

            /**
             * Set itemStack for win item
             * @param itemStack itemStack
             */
            public void setItemStack(ItemStack itemStack) {
                this.itemStack = itemStack;
            }



            /**
             * Get item display name
             * @return display name
             */
            public String getDisplayName() {
                return displayName;
            }

            /**
             * Set item displayName
             * @param displayName display name
             */
            public void setDisplayName(String displayName) {
                this.displayName = displayName;
            }

            /**
             * Check if item enchanted
             * @return boolean
             */
            public boolean isEnchanted() {
                return enchanted;
            }

            /**
             * Set item enchanted
             * @param enchanted boolean
             */
            public void setEnchanted(boolean enchanted) {
                this.enchanted = enchanted;
            }

            /**
             * Material id like HDB:1234, HEAD:name, RED_WOOL etc.
             * @return id
             */
            @Nullable
            public String getId() {
                return id;
            }

            /**
             * Set material id
             * @param id material id
             */
            public void setId(String id) {
                this.id = id;
            }

            @NotNull
            public List<String> getLore() {
                return lore;
            }

            public void setLore(List<String> lore) {
                this.lore = lore == null ? new ArrayList<>() : lore;
            }

            @Override
            public Material clone() {
                try {
                    return (Material) super.clone();
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError();
                }
            }

            @Override
            public String toString() {
                return "Material{" +
                        "id='" + id + '\'' +
                        ", itemStack=" + itemStack +
                        ", displayName='" + displayName + '\'' +
                        ", enchanted=" + enchanted +
                        ", modelData=" + modelData +
                        ", rgb=" + Arrays.toString(rgb) +
                        ", lore=" + lore +
                        '}';
            }

            /**
             * Gets custom model data
             * @return custom model data
             */
            public int getModelData() {
                return modelData;
            }

            /**
             * Set custom model data
             * @param modelData custom model data
             */
            public void setModelData(int modelData) {
                this.modelData = modelData;
            }

            /**
             * Gets array of rgb
             * @return rgb array
             */
            public String[] getRgb() {
                return rgb;
            }

            /**
             * Set array of rgb
             * @param rgb array
             */
            public void setRgb(String[] rgb) {
                this.rgb = rgb;
            }
        }

        @Override
        public Item clone() {
            try {
                Item clonedItem = (Item) super.clone();

                clonedItem.randomActions = cloneRandomActionsMap(this.randomActions);

                return clonedItem;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        /**
         * Clone method for CaseData deep clone
         */
        private static Map<String, RandomAction> cloneRandomActionsMap(Map<String, RandomAction> originalMap) {
            Map<String, RandomAction> clonedMap = new HashMap<>();
            for (Map.Entry<String, RandomAction> entry : originalMap.entrySet()) {
                clonedMap.put(entry.getKey(), entry.getValue().clone());
            }
            return clonedMap;
        }

    }

    /**
     * Class to implement information about case opening histories
     */
    @DatabaseTable(tableName = "history_data")

    public static class HistoryData implements Cloneable {
        @DatabaseField(columnName = "id")
        private int id;

        public void setItem(String item) {
            this.item = item;
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public void setCaseType(String caseType) {
            this.caseType = caseType;
        }

        public void setAction(String action) {
            this.action = action;
        }

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
         * @param item Item name
         * @param caseType Case type
         * @param playerName Player name
         * @param time Timestamp
         * @param group Group name
         * @param action Action name
         */
        public HistoryData(String item, String caseType, String playerName, long time, String group, String action) {
            this.item = item;
            this.playerName = playerName;
            this.time = time;
            this.group = group;
            this.caseType = caseType;
            this.action = action;
        }

        @Override
        public String toString() {
            return "HistoryData{" +
                    "playerName='" + playerName + '\'' +
                    ", time=" + time +
                    ", group='" + group + '\'' +
                    ", caseType='" + caseType + '\'' +
                    ", action='" + action + '\'' +
                    '}';
        }

        @Override
        public HistoryData clone() {
            try {
                return (HistoryData) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        /**
         * Get material id like HDB:1234, HEAD:name, RED_WOOL etc.
         * @return material id
         */
        public int getId() {
            return id;
        }

        /**
         *  Set material id like HDB:1234, HEAD:name, RED_WOOL etc.
         * @param id material id
         */
        public void setId(int id) {
            this.id = id;
        }

        /**
         * Get player name, who opened case
         * @return player name
         */
        public String getPlayerName() {
            return playerName;
        }

        /**
         * Get timestamp, when case successful opened
         * @return timestamp
         */
        public long getTime() {
            return time;
        }

        /**
         * Get win group
         * @return win group
         */
        public String getGroup() {
            return group;
        }

        /**
         * Get case type
         * @return case type
         */
        public String getCaseType() {
            return caseType;
        }

        /**
         * Get action (like group, but from RandomActions section)
         * @return action
         */
        public String getAction() {
            return action;
        }

        /**
         * Get win item name (like path of item in case config)
         * @return win item name
         */
        public String getItem() {
            return item;
        }
    }


    /**
     * Class for the implementation of holograms of the case.
     */
    public static class Hologram {

        private final boolean enabled;
        private final double height;
        private final int range;
        private final List<String> messages;

        /**
         * Empty constructor
         */
        public Hologram() {
            this.enabled = false;
            this.height = 0.0;
            this.range = 8;
            this.messages = new ArrayList<>();
        }

        /**
         * A secondary constructor to build a hologram.
         *
         * @param enabled if the hologram enabled or not
         * @param height of the hologram from the ground
         * @param range the range, when player will see hologram
         * @param messages the hologram will display
         */
        public Hologram(boolean enabled, double height, int range, List<String> messages) {
            this.enabled = enabled;
            this.height = height;
            this.range = range;
            this.messages = messages;
        }

        /**
         * Check if the hologram is enabled or not.
         *
         * @return true if yes otherwise false.
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Gets the range at which a hologram can be seen.
         *
         * @return the range
         */
        public int getRange() {
            return range;
        }

        /**
         * Get the height of the hologram from the ground.
         *
         * @return the height
         */
        public double getHeight() {
            return height;
        }

        /**
         * Get the messages the hologram will display.
         *
         * @return the list of messages
         */
        public List<String> getMessages() {
            return messages;
        }
    }

}
