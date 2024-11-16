package com.jodexindustries.donatecase.api.data.casedata;

import com.jodexindustries.donatecase.api.data.casedata.gui.GUI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Class for implementing cases that are loaded into the plugin's memory.
 */
public class CaseDataBukkit extends CaseData<CaseDataMaterialBukkit, ItemStack> {
    private ConfigurationSection animationSettings;

    /**
     * Default constructor
     *
     * @param caseType        Case type
     * @param caseDisplayName Case display name
     * @param animation       Animation name
     * @param items           Items list
     * @param historyData     History data array
     * @param hologram        Hologram object
     * @param levelGroups     Map with level groups
     * @param gui             GUI object
     * @param noKeyActions    NoKeyActions
     * @param openType        Open type
     * @param animationSettings Animation settings section
     */
    public CaseDataBukkit(String caseType, String caseDisplayName, String animation, Map<String, CaseDataItem<CaseDataMaterialBukkit, ItemStack>> items,
                          CaseDataHistory[] historyData, CaseDataHologram hologram, Map<String, Integer> levelGroups, GUI<CaseDataMaterialBukkit> gui,
                          List<String> noKeyActions, @NotNull OpenType openType, ConfigurationSection animationSettings) {
        super(caseType, caseDisplayName, animation, items, historyData, hologram, levelGroups, gui, noKeyActions, openType);
        this.animationSettings = animationSettings;
    }

    /**
     * Gets animation settings section
     *
     * @return settings
     * @since 2.2.5.9
     */
    @Nullable
    public ConfigurationSection getAnimationSettings() {
        return animationSettings;
    }

    /**
     * Sets animation settings section
     *
     * @param animationSettings animation settings section
     * @since 2.2.5.9
     */
    public void setAnimationSettings(ConfigurationSection animationSettings) {
        this.animationSettings = animationSettings;
    }

    @Override
    public CaseDataBukkit clone() {
        return (CaseDataBukkit) super.clone();
    }

}
