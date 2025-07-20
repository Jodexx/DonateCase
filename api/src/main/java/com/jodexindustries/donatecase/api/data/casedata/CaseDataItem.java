package com.jodexindustries.donatecase.api.data.casedata;

import com.jodexindustries.donatecase.api.data.casedefinition.CaseItem;
import com.jodexindustries.donatecase.api.tools.ProbabilityCollection;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for the implementation of winning items from the case
 */
@Accessors(fluent = true)
@Getter
@Setter
@ConfigSerializable
public class CaseDataItem implements Cloneable {

    @Deprecated
    @Setting(nodeFromParent = true)
    private ConfigurationNode node;

    @Deprecated
    private transient String name;

    @Deprecated
    @Setting("Group")
    private String group;

    @Deprecated
    @Setting("Chance")
    private double chance;

    @Deprecated
    @Setting("Index")
    private int index;

    @Deprecated
    @Setting("Material")
    private CaseDataMaterial material;

    @Deprecated
    @Setting("GiveType")
    private String giveType = "ONE";

    @Deprecated
    @Setting("Actions")
    private List<String> actions;

    @Deprecated
    @Setting("AlternativeActions")
    private List<String> alternativeActions;

    @Deprecated
    @Setting("RandomActions")
    private Map<String, RandomAction> randomActions;

    @Deprecated
    public static CaseDataItem fromItem(CaseItem item) {
        CaseDataItem old = new CaseDataItem();

        old.name = item.name();
        old.group = item.group();
        old.chance = item.chance();
        old.index = item.index();
        old.material = CaseDataMaterial.fromMaterial(item.material());
        old.giveType = item.giveType().name();
        old.actions = item.actions();
        old.alternativeActions = item.alternativeActions();
        old.randomActions = fromItem(item.randomActions());

        return old;
    }

    @Deprecated
    public static CaseItem toItem(CaseDataItem old) {
        return new CaseItem(
                old.name,
                old.group,
                old.chance,
                old.index,
                CaseDataMaterial.toMaterial(old.material),
                GiveType.valueOf(old.giveType),
                old.actions,
                old.alternativeActions,
                toItem(old.randomActions)
        );
    }

    @Deprecated
    private static Map<String, RandomAction> fromItem(Map<String, CaseItem.RandomAction> randomActions) {
        Map<String, RandomAction> old = new HashMap<>();

        for (Map.Entry<String, CaseItem.RandomAction> entry : randomActions.entrySet()) {
            old.put(entry.getKey(), RandomAction.fromItem(entry.getValue()));
        }

        return old;
    }

    @Deprecated
    private static Map<String, CaseItem.RandomAction> toItem(Map<String, RandomAction> oldRandomActions) {
        Map<String, CaseItem.RandomAction> map = new HashMap<>();

        for (Map.Entry<String, RandomAction> entry : oldRandomActions.entrySet()) {
            map.put(entry.getKey(), RandomAction.toItem(entry.getValue()));
        }

        return map;
    }

    @Deprecated
    public String getName() {
        if (name != null) return name;
        return String.valueOf(node.key());
    }

    @Deprecated
    public List<String> getActionsBasedOnChoice(RandomAction randomAction, boolean alternative) {
        if (randomAction != null) return randomAction.actions();
        return alternative ? alternativeActions() : actions();
    }

    @Deprecated
    public RandomAction getRandomAction() {
        ProbabilityCollection<RandomAction> collection = new ProbabilityCollection<>();
        for (RandomAction randomAction : randomActions().values()) {
            double chance = randomAction.chance();
            if(chance > 0) collection.add(randomAction, chance);
        }
        return collection.get();
    }

    /**
     * Class to implement a random action
     */
    @Accessors(fluent = true)
    @Setter
    @Getter
    @ConfigSerializable
    public static class RandomAction implements Cloneable {

        @Deprecated
        @Setting(nodeFromParent = true)
        private ConfigurationNode node;

        @Deprecated
        private transient String name;

        @Deprecated
        @Setting("Chance")
        private double chance;

        @Deprecated
        @Setting("Actions")
        private List<String> actions;

        @Deprecated
        @Setting("DisplayName")
        private String displayName;

        @Deprecated
        public static RandomAction fromItem(CaseItem.RandomAction randomAction) {
            RandomAction old = new RandomAction();

            old.name = randomAction.name();
            old.chance = randomAction.chance();
            old.actions = randomAction.actions();
            old.displayName = randomAction.displayName();

            return old;
        }

        @Deprecated
        public static CaseItem.RandomAction toItem(RandomAction old) {
            return new CaseItem.RandomAction(
                    old.name,
                    old.chance,
                    old.actions,
                    old.displayName
            );
        }

        @Deprecated
        public String getName() {
            if (name != null) return name;
            return String.valueOf(node.key());
        }

        @Deprecated
        @Override
        public RandomAction clone() {
            try {
                return (RandomAction) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        @Deprecated
        @Override
        public String toString() {
            return "RandomAction{" +
                    "chance=" + chance +
                    ", actions=" + actions +
                    ", displayName='" + displayName + '\'' +
                    '}';
        }
    }

    @Deprecated
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

    @Deprecated
    @Override
    public String toString() {
        return "CaseDataItem{" +
                "node=" + node +
                ", group='" + group + '\'' +
                ", chance=" + chance +
                ", index=" + index +
                ", material=" + material +
                ", giveType='" + giveType + '\'' +
                ", actions=" + actions +
                ", alternativeActions=" + alternativeActions +
                ", randomActions=" + randomActions +
                '}';
    }

    /**
     * Clone method for CaseData deep clone
     */
    @Deprecated
    private static Map<String, RandomAction> cloneRandomActionsMap(Map<String, RandomAction> originalMap) {
        Map<String, RandomAction> clonedMap = new HashMap<>();
        for (Map.Entry<String, RandomAction> entry : originalMap.entrySet()) {
            clonedMap.put(entry.getKey(), entry.getValue().clone());
        }
        return clonedMap;
    }

}