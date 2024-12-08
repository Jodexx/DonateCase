package com.jodexindustries.donatecase.api.data.casedata;

import com.jodexindustries.donatecase.api.data.casedata.gui.GUI;
import com.jodexindustries.donatecase.api.tools.ProbabilityCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for implementing cases that are loaded into the plugin's memory.
 */
public class CaseData<M extends CaseDataMaterial<I>, I> implements CCloneable {
    private final String caseType;
    private String caseDisplayName;
    private String animation;
    private Map<String, CaseDataItem<M, I>> items;
    private CaseDataHistory[] historyData;
    private CaseDataHologram hologram;
    private Map<String, Integer> levelGroups;
    private GUI<M> gui;
    private List<String> noKeyActions;
    private OpenType openType;

    /**
     * Default constructor
     *
     * @param caseType          Case type
     * @param caseDisplayName   Case display name
     * @param animation         Animation name
     * @param items             Items list
     * @param historyData       History data array
     * @param hologram          Hologram object
     * @param levelGroups       Map with level groups
     * @param gui               GUI object
     * @param noKeyActions      NoKeyActions
     * @param openType          Open type
     */
    public CaseData(String caseType, String caseDisplayName, String animation, Map<String,
            CaseDataItem<M, I>> items, CaseDataHistory[] historyData, CaseDataHologram hologram, Map<String, Integer> levelGroups, GUI<M> gui,
                    List<String> noKeyActions, @NotNull OpenType openType) {
        this.caseType = caseType;
        this.caseDisplayName = caseDisplayName;
        this.animation = animation;
        this.items = items;
        this.historyData = historyData;
        this.hologram = hologram;
        this.levelGroups = levelGroups;
        this.gui = gui;
        this.noKeyActions = noKeyActions;
        this.openType = openType;
    }

    @Override
    public String toString() {
        return "CaseData{" +
                "caseType='" + caseType + '\'' +
                ", caseDisplayName='" + caseDisplayName + '\'' +
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
     *
     * @return history data
     */
    public CaseDataHistory[] getHistoryData() {
        return historyData;
    }

    /**
     * Get case items
     *
     * @return items
     */
    public Map<String, CaseDataItem<M, I>> getItems() {
        return items;
    }

    /**
     * Get case item
     *
     * @param name item name
     * @return item
     */
    @Nullable
    public CaseDataItem<M, I> getItem(String name) {
        return items.getOrDefault(name, null);
    }

    /**
     * Get random item from case
     *
     * @return Random item
     */
    public CaseDataItem<M, I> getRandomItem() {
        ProbabilityCollection<CaseDataItem<M, I>> collection = new ProbabilityCollection<>();
        for (CaseDataItem<M, I> item : items.values()) {
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
        return items.values().stream().noneMatch(item -> item.getChance() <= 0);
    }

    /**
     * Set case history data
     *
     * @param historyData history data
     */
    public void setHistoryData(CaseDataHistory[] historyData) {
        this.historyData = historyData;
    }

    /**
     * Set case items
     *
     * @param items map of CaseData.Item items
     */
    public void setItems(Map<String, CaseDataItem<M, I>> items) {
        this.items = items;
    }

    /**
     * Get animation
     *
     * @return animation
     */
    @NotNull
    public String getAnimation() {
        return animation;
    }

    /**
     * Set animation
     *
     * @param animation animation
     */
    public void setAnimation(String animation) {
        this.animation = animation;
    }

    /**
     * Get case title
     *
     * @return title
     */
    @NotNull
    public String getCaseTitle() {
        if (gui == null) return "";
        return gui.getTitle();
    }

    /**
     * Set case title
     *
     * @param caseTitle title
     */
    public void setCaseTitle(@NotNull String caseTitle) {
        if (this.gui != null) this.gui.setTitle(caseTitle);
    }

    /**
     * Get case type
     *
     * @return case type
     */
    @NotNull
    public String getCaseType() {
        return caseType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CaseData<M, I> clone() {
        try {
            CaseData<M, I> clonedCaseData = (CaseData<M, I>) super.clone();

            // Deep clone the map of items
            clonedCaseData.items = cloneItemsMap(this.items);

            clonedCaseData.gui = this.gui.clone();

            // Deep clone the array of historyData
            clonedCaseData.historyData = cloneCaseDataHistoryArray(this.historyData);

            return clonedCaseData;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Clone method for CaseData deep clone
     */
    protected static CaseDataHistory[] cloneCaseDataHistoryArray(CaseDataHistory[] originalArray) {
        CaseDataHistory[] newArray = new CaseDataHistory[originalArray.length];
        for (int i = 0; i < originalArray.length; i++) {
            if (originalArray[i] != null) {
                newArray[i] = originalArray[i].clone();
            }
        }
        return newArray;
    }

    /**
     * Clone method for CaseData deep clone
     */
    protected Map<String, CaseDataItem<M, I>> cloneItemsMap(Map<String, CaseDataItem<M, I>> originalMap) {
        Map<String, CaseDataItem<M, I>> clonedMap = new HashMap<>();
        for (Map.Entry<String, CaseDataItem<M, I>> entry : originalMap.entrySet()) {
            clonedMap.put(entry.getKey(), entry.getValue().clone());
        }
        return clonedMap;
    }

    /**
     * Get case display name (case.DisplayName path in case config)
     *
     * @return case display name
     */

    public String getCaseDisplayName() {
        return caseDisplayName;
    }

    /**
     * Set case display name (case.DisplayName path in case config)
     *
     * @param caseDisplayName new display name
     */

    public void setCaseDisplayName(String caseDisplayName) {
        this.caseDisplayName = caseDisplayName;
    }

    /**
     * Get case hologram
     *
     * @return case hologram class
     */
    public CaseDataHologram getHologram() {
        return hologram;
    }

    /**
     * Set case hologram
     *
     * @param hologram case hologram class
     */
    public void setHologram(CaseDataHologram hologram) {
        this.hologram = hologram;
    }

    /**
     * Get case LevelGroups (optional setting for each case)
     *
     * @return map of LevelGroups
     */
    public Map<String, Integer> getLevelGroups() {
        return levelGroups;
    }

    /**
     * Set case LevelGroups (optional setting for each case)
     *
     * @param levelGroups map of LevelGroups
     */
    public void setLevelGroups(Map<String, Integer> levelGroups) {
        this.levelGroups = levelGroups;
    }

    /**
     * Gets GUI storage object
     *
     * @return GUI object
     */
    public GUI<M> getGui() {
        return gui;
    }

    /**
     * Set GUI storage object
     *
     * @param gui object
     */
    public void setGui(GUI<M> gui) {
        this.gui = gui;
    }

    /**
     * Gets actions to be performed if a player tries to open a case without keys
     *
     * @return List of actions
     */
    public List<String> getNoKeyActions() {
        return noKeyActions;
    }

    /**
     * Set actions to be performed if a player tries to open a case without keys
     *
     * @param noKeyActions List of actions
     */
    public void setNoKeyActions(List<String> noKeyActions) {
        this.noKeyActions = noKeyActions;
    }

    /**
     * Gets case open type
     *
     * @return open type
     */
    @NotNull
    public OpenType getOpenType() {
        return openType;
    }

    /**
     * Set case open type
     *
     * @param openType open type
     */
    public void setOpenType(OpenType openType) {
        this.openType = openType;
    }

}
