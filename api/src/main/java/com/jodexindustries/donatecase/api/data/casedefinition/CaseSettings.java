package com.jodexindustries.donatecase.api.data.casedefinition;

import com.jodexindustries.donatecase.api.data.casedata.OpenType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Accessors(fluent = true, chain = false)
@Getter
@Setter
public class CaseSettings implements Cloneable {

    private String type;

    private String defaultMenu;

    private String animation;

    private Hologram hologram;

    private LevelGroups levelGroups;

    private List<String> noKeyActions;

    private OpenType openType;

    private ConfigurationNode animationSettings;

    private int cooldownBeforeAnimation;

    private int historyDataSize;

    private String displayName;

    public CaseSettings(String type, String defaultMenu, String animation, Hologram hologram, LevelGroups levelGroups, List<String> noKeyActions, OpenType openType, ConfigurationNode animationSettings, int cooldownBeforeAnimation, int historyDataSize, String displayName) {
        this.type = type;
        this.defaultMenu = defaultMenu;
        this.animation = animation;
        this.hologram = hologram;
        this.levelGroups = levelGroups;
        this.noKeyActions = noKeyActions;
        this.openType = openType;
        this.animationSettings = animationSettings;
        this.cooldownBeforeAnimation = cooldownBeforeAnimation;
        this.historyDataSize = historyDataSize;
        this.displayName = displayName;
    }

    @Override
    public CaseSettings clone() {
        try {
            CaseSettings clone = (CaseSettings) super.clone();
            clone.hologram = hologram.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static class LevelGroups {

        @Getter
        private final Map<String, Integer> map;

        public LevelGroups(Map<String, Integer> map) {
            this.map = map;
        }

        public LevelGroups() {
            this.map = new HashMap<>();
        }

        public boolean isBetterOrEqual(String playerGroup, String rewardGroup) {
            Integer playerLevel = map.get(playerGroup);
            Integer rewardLevel = map.get(rewardGroup);

            return playerLevel != null && rewardLevel != null && playerLevel >= rewardLevel;
        }

    }

    @Accessors(fluent = true)
    @Getter
    @Setter
    public static class Hologram implements Cloneable {

        private ConfigurationNode node;

        private boolean enabled;

        private double height;

        private int range;

        private List<String> message;

        public Hologram(ConfigurationNode node, boolean enabled, double height, int range, List<String> message) {
            this.node = node;
            this.enabled = enabled;
            this.height = height;
            this.range = range;
            this.message = message;
        }

        @Override
        public Hologram clone() {
            try {
                Hologram clone = (Hologram) super.clone();
                clone.node = this.node.copy();
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }
}
