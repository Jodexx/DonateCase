package com.jodexindustries.donatecase.api.data.casedefinition;

import com.jodexindustries.donatecase.api.data.casedata.GiveType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Accessors(fluent = true, chain = false)
@Getter
@Setter
public class CaseItem implements Cloneable {

    private String name;

    private String group;

    private double chance;

    private int index;

    private CaseMaterial material;

    private GiveType giveType;

    private List<String> actions;

    private List<String> alternativeActions;

    private Map<String, RandomAction> randomActions;

    public CaseItem(String name, String group, double chance, int index, CaseMaterial material, GiveType giveType, List<String> actions, List<String> alternativeActions, Map<String, RandomAction> randomActions) {
        this.name = name;
        this.group = group;
        this.chance = chance;
        this.index = index;
        this.material = material;
        this.giveType = giveType;
        this.actions = actions;
        this.alternativeActions = alternativeActions;
        this.randomActions = randomActions;
    }

    @Override
    public CaseItem clone() {
        try {
            CaseItem clone = (CaseItem) super.clone();
            clone.randomActions = this.randomActions.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().clone(), (a, b) -> b));
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Accessors(fluent = true)
    @Getter
    @Setter
    public static class RandomAction implements Cloneable {

        private String name;

        private double chance;

        private List<String> actions;

        private String displayName;

        public RandomAction(String name, double chance, List<String> actions, String displayName) {
            this.name = name;
            this.chance = chance;
            this.actions = actions;
            this.displayName = displayName;
        }

        @Override
        public RandomAction clone() {
            try {
                return (RandomAction) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }
}
