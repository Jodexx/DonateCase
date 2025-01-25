package com.jodexindustries.donatecase.api.data.casedata;

import lombok.Getter;
import lombok.Setter;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for the implementation of winning items from the case
 */
@Getter
@Setter
@ConfigSerializable
public class CaseDataItem implements Cloneable {
    @Setting(nodeFromParent = true)
    private ConfigurationNode node;
    @Setting("Group")
    private String group;
    @Setting("Chance")
    private double chance;
    @Setting("Index")
    private int index;
    @Setting("Item")
    private CaseDataMaterial material;
    @Setting("GiveType")
    private String giveType = "ONE";
    @Setting("Actions")
    private List<String> actions;
    @Setting("AlternativeActions")
    private List<String> alternativeActions;
    @Setting("RandomActions")
    private Map<String, RandomAction> randomActions;

    /**
     * Class to implement a random action
     */
    @Setter
    @Getter
    @ConfigSerializable
    public static class RandomAction implements Cloneable {
        @Setting("Chance")
        private double chance;
        @Setting("Actions")
        private List<String> actions;
        @Setting("DisplayName")
        private String displayName;

        @Override
        public RandomAction clone() {
            try {
                return (RandomAction) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

    }

    @Override
    public CaseDataItem clone() {
        try {
            CaseDataItem clonedItem = (CaseDataItem) super.clone();

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