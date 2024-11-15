package com.jodexindustries.donatecase.api.data.casedata;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for the implementation of winning items from the case
 */
public class CaseDataItem<M extends CaseDataMaterial<I>, I> implements Cloneable {
    private final String itemName;
    private String group;
    private double chance;
    private int index;
    private M material;
    private String giveType;
    private List<String> actions;
    private List<String> alternativeActions;
    private Map<String, RandomAction> randomActions;

    /**
     * Default constructor
     *
     * @param itemName           Item name
     * @param group              Item group
     * @param chance             Item chance
     * @param index              Item index
     * @param material           Item material
     * @param giveType           Item give type
     * @param actions            Item actions
     * @param randomActions      Item random actions
     * @param alternativeActions Item alternative actions
     */
    public CaseDataItem(String itemName, String group, double chance, int index, M material,
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
     *
     * @return random actions
     */
    public Map<String, RandomAction> getRandomActions() {
        return randomActions;
    }

    /**
     * Get random action
     *
     * @param name random action name
     * @return CaseData.RandomAction
     */
    @Nullable
    public RandomAction getRandomAction(String name) {
        return randomActions.getOrDefault(name, null);
    }

    /**
     * Set random actions
     *
     * @param randomActions map of random actions
     */
    public void setRandomActions(Map<String, RandomAction> randomActions) {
        this.randomActions = randomActions;
    }

    /**
     * Get item actions
     *
     * @return actions
     */
    public List<String> getActions() {
        return actions;
    }

    /**
     * Set item actions
     *
     * @param actions actions
     */
    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    /**
     * Get item give type
     *
     * @return give type
     */
    public String getGiveType() {
        return giveType;
    }

    /**
     * Set item give type
     *
     * @param giveType give type
     */
    public void setGiveType(String giveType) {
        this.giveType = giveType;
    }

    /**
     * Get item material (CaseData.Material)
     *
     * @return CaseData.Material
     */
    @NotNull
    public M getMaterial() {
        return material;
    }

    /**
     * Set item material (CaseData.Material)
     *
     * @param material CaseData.Material
     */
    public void setMaterial(M material) {
        this.material = material;
    }

    /**
     * Get item chance
     *
     * @return chance
     */
    public double getChance() {
        return chance;
    }

    /**
     * Set item chance
     *
     * @param chance chance
     */
    public void setChance(double chance) {
        this.chance = chance;
    }

    /**
     * Get item group
     *
     * @return grouo
     */
    public String getGroup() {
        return group;
    }

    /**
     * Set item group
     *
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
     *
     * @return item name
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * Get alternative actions
     * These actions are performed when LevelGroups is enabled and the player's group has a higher level than the one they won from the case
     *
     * @return list of actions
     */
    public List<String> getAlternativeActions() {
        return alternativeActions;
    }

    /**
     * Set alternative actions
     * These actions are performed when LevelGroups is enabled and the player's group has a higher level than the one they won from the case
     *
     * @param alternativeActions list of actions
     */
    public void setAlternativeActions(List<String> alternativeActions) {
        this.alternativeActions = alternativeActions;
    }

    /**
     * Gets item index. <br/>
     * Used for items sorting
     *
     * @return index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set item index <br/>
     * Used for items sorting
     *
     * @param index item index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Class to implement a random action
     */
    public static class RandomAction implements Cloneable {
        private double chance;
        private List<String> actions;
        private String displayName;

        /**
         * Default constructor
         *
         * @param chance      action chance
         * @param actions     list of actions
         * @param displayName action display name
         */
        public RandomAction(double chance, List<String> actions, String displayName) {
            this.chance = chance;
            this.actions = actions;
            this.displayName = displayName;
        }

        /**
         * Get random actions
         *
         * @return random actions
         */
        public List<String> getActions() {
            return actions;
        }


        /**
         * Set random actions
         *
         * @param actions random actions
         */
        public void setActions(List<String> actions) {
            this.actions = actions;
        }

        /**
         * Get random action chance
         *
         * @return chance
         */
        public double getChance() {
            return chance;
        }

        /**
         * Set random action chance
         *
         * @param chance chance
         */
        public void setChance(double chance) {
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
         *
         * @return display name of random action
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * Set display name of random action
         * Path in case config: RandomActions.(action).DisplayName
         *
         * @param displayName display name of random action
         */
        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public CaseDataItem<M, I> clone() {
        try {
            CaseDataItem<M, I> clonedItem = (CaseDataItem<M, I>) super.clone();

            clonedItem.randomActions = cloneRandomActionsMap(this.randomActions);

            return clonedItem;
        } catch (Throwable e) {
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