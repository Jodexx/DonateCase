package com.jodexindustries.donatecase.api.data;

import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaseData implements Cloneable {
    private final String caseName;
    private String caseTitle;
    private String animation;
    private AnimationSound animationSound;
    private Map<String, Item> items;
    private HistoryData[] historyData;

    public CaseData(String caseName, String caseTitle, String animation, AnimationSound animationSound, Map<String, Item> items, HistoryData[] historyData) {
        this.caseName = caseName;
        this.caseTitle = caseTitle;
        this.animation = animation;
        this.animationSound = animationSound;
        this.items = items;
        this.historyData = historyData;
    }

    @Override
    public String toString() {
        return "CaseData{" +
                "caseName='" + caseName + '\'' +
                ", caseTitle='" + caseTitle + '\'' +
                ", animation='" + animation + '\'' +
                ", animationSound=" + animationSound +
                ", items=" + items +
                ", historyData=" + Arrays.toString(historyData) +
                '}';
    }

    public HistoryData[] getHistoryData() {
        return historyData;
    }

    public Map<String, Item> getItems() {
        return items;
    }
    public Item getItem(String name) {
        return items.getOrDefault(name, null);
    }

    public void setHistoryData(HistoryData[] historyData) {
        this.historyData = historyData;
    }

    public void setItems(Map<String, Item> items) {
        this.items = items;
    }

    public AnimationSound getAnimationSound() {
        return animationSound;
    }

    public void setAnimationSound(AnimationSound animationSound) {
        this.animationSound = animationSound;
    }

    public String getAnimation() {
        return animation;
    }

    public void setAnimation(String animation) {
        this.animation = animation;
    }

    public String getCaseTitle() {
        return caseTitle;
    }

    public void setCaseTitle(String caseTitle) {
        this.caseTitle = caseTitle;
    }

    public String getCaseName() {
        return caseName;
    }

    @Override
    public CaseData clone() {
        try {
            CaseData clonedCaseData = (CaseData) super.clone();

            // Clone the nested classes
            clonedCaseData.animationSound = this.animationSound.clone();

            // Deep clone the map of items
            clonedCaseData.items = cloneItemsMap(this.items);

            // Deep clone the array of historyData
            clonedCaseData.historyData = cloneHistoryDataArray(this.historyData);

            return clonedCaseData;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
    private static HistoryData[] cloneHistoryDataArray(HistoryData[] originalArray) {
        HistoryData[] newArray = new HistoryData[originalArray.length];
        for (int i = 0; i < originalArray.length; i++) {
            newArray[i] = originalArray[i].clone();
        }
        return newArray;
    }

    private static Map<String, Item> cloneItemsMap(Map<String, Item> originalMap) {
        Map<String, Item> clonedMap = new HashMap<>();
        for (Map.Entry<String, Item> entry : originalMap.entrySet()) {
            clonedMap.put(entry.getKey(), entry.getValue().clone());
        }
        return clonedMap;
    }
    public static class Item implements Cloneable {
        private String group;
        private int chance;
        private Material material;
        private String giveType;
        private List<String> actions;
        private Map<String,RandomAction> randomActions;
        private String[] rgb;

        public Item(String group, int chance, Material material, String giveType, List<String> actions, Map<String, RandomAction> randomActions, String[] rgb) {
            this.group = group;
            this.chance = chance;
            this.material = material;
            this.giveType = giveType;
            this.actions = actions;
            this.randomActions = randomActions;
            this.rgb = rgb;
        }

        public Map<String, RandomAction> getRandomActions() {
            return randomActions;
        }
        public RandomAction getRandomAction(String name) {
            return randomActions.getOrDefault(name, null);
        }

        public void setRandomActions(Map<String, RandomAction> randomActions) {
            this.randomActions = randomActions;
        }

        public List<String> getActions() {
            return actions;
        }

        public void setActions(List<String> actions) {
            this.actions = actions;
        }

        public String getGiveType() {
            return giveType;
        }

        public void setGiveType(String giveType) {
            this.giveType = giveType;
        }

        public Material getMaterial() {
            return material;
        }

        public void setMaterial(Material material) {
            this.material = material;
        }

        public int getChance() {
            return chance;
        }

        public void setChance(int chance) {
            this.chance = chance;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String[] getRgb() {
            return rgb;
        }

        public void setRgb(String[] rgb) {
            this.rgb = rgb;
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
                    ", rgb=" + Arrays.toString(rgb) +
                    '}';
        }

        public static class RandomAction implements Cloneable {
            private int chance;
            private List<String> actions;

            public RandomAction(int chance, List<String> actions) {
                this.chance = chance;
                this.actions = actions;
            }

            public List<String> getActions() {
                return actions;
            }

            public void setActions(List<String> actions) {
                this.actions = actions;
            }

            public int getChance() {
                return chance;
            }

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
        }
        public static class Material {
            private ItemStack itemStack;
            private String displayName;
            private boolean enchanted;

            public Material(ItemStack itemStack, String displayName, boolean enchanted) {
                this.itemStack = itemStack;
                this.displayName = displayName;
                this.enchanted = enchanted;
            }

            public ItemStack getItemStack() {
                return itemStack;
            }
            public void setItemStack(ItemStack itemStack) {
                this.itemStack = itemStack;
            }

            @Override
            public String toString() {
                return "Material{" +
                        "itemStack=" + itemStack +
                        ", displayName='" + displayName + '\'' +
                        ", enchanted=" + enchanted +
                        '}';
            }

            public String getDisplayName() {
                return displayName;
            }
            public void setDisplayName(String displayName) {
                this.displayName = displayName;
                if(this.itemStack.getItemMeta() != null) {
                    this.itemStack.getItemMeta().setDisplayName(displayName);
                }
            }

            public boolean isEnchanted() {
                return enchanted;
            }

            public void setEnchanted(boolean enchanted) {
                    if (enchanted) {
                        this.itemStack.addUnsafeEnchantment(Enchantment.LURE, 1);
                    } else {
                        this.itemStack.removeEnchantment(Enchantment.LURE);
                    }
                    this.enchanted = enchanted;
            }
        }

        @Override
        public Item clone() {
            try {
                Item clonedItem = (Item) super.clone();

                // Deep clone the map of randomActions
                clonedItem.randomActions = cloneRandomActionsMap(this.randomActions);

                return clonedItem;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        private static Map<String, RandomAction> cloneRandomActionsMap(Map<String, RandomAction> originalMap) {
            Map<String, RandomAction> clonedMap = new HashMap<>();
            for (Map.Entry<String, RandomAction> entry : originalMap.entrySet()) {
                clonedMap.put(entry.getKey(), entry.getValue().clone());
            }
            return clonedMap;
        }

    }

    public static class AnimationSound implements Cloneable {
        private Sound sound;
        private float volume;
        private float pitch;

        public AnimationSound(Sound sound, float volume, float pitch) {
            this.sound = sound;
            this.volume = volume;
            this.pitch = pitch;
        }

        public Sound getSound() {
            return sound;
        }

        public void setSound(Sound sound) {
            this.sound = sound;
        }

        public float getVolume() {
            return volume;
        }

        public void setVolume(float volume) {
            this.volume = volume;
        }

        public float getPitch() {
            return pitch;
        }

        public void setPitch(float pitch) {
            this.pitch = pitch;
        }

        @Override
        public String toString() {
            return "AnimationSound{" +
                    "sound=" + sound +
                    ", volume=" + volume +
                    ", pitch=" + pitch +
                    '}';
        }

        @Override
        public AnimationSound clone() {
            try {
                return (AnimationSound) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }
    }
    /**
     * Class to implement information about case opening histories
     */
    public static class HistoryData implements Cloneable {
        private final String playerName;
        private final long time;
        private final String group;
        private final String caseType;
        private final String action;

        public HistoryData(String caseType, String playerName, long time, String group, String action) {
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

        public String getAction() {
            return action;
        }

    }

}
