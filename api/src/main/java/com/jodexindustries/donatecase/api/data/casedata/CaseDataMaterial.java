package com.jodexindustries.donatecase.api.data.casedata;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents material data for a case, including properties like display name,
 * enchantment status, lore, custom model data, and RGB values.
 * This class provides methods to get and set each attribute, allowing flexible
 * modification and retrieval of material details.
 */
public abstract class CaseDataMaterial<I> implements Cloneable, MetaUpdatable {

    protected String id;
    protected String displayName;
    protected boolean enchanted;
    protected List<String> lore;
    protected int modelData;
    protected String[] rgb;
    protected I itemStack;

    /**
     * Constructs a new CaseDataMaterial with specified attributes.
     *
     * @param id          the unique identifier for the material, such as HDB:1234 or RED_WOOL
     * @param displayName the display name of the material
     * @param enchanted   whether the material is enchanted
     * @param lore        the lore associated with the material; if null, an empty list is assigned
     * @param modelData   the custom model data for the material
     * @param rgb         the RGB color values for the material
     */
    public CaseDataMaterial(String id, I itemStack, String displayName, boolean enchanted,
                            List<String> lore, int modelData, String[] rgb) {
        this.id = id;
        this.displayName = displayName;
        this.itemStack = itemStack;
        this.enchanted = enchanted;
        this.lore = lore == null ? new ArrayList<>() : lore;
        this.modelData = modelData;
        this.rgb = rgb;
    }

    /**
     * Gets the display name of the material.
     *
     * @return the display name, or null if not set
     */
    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name of the material.
     *
     * @param displayName the new display name for the material
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Checks if the material is enchanted.
     *
     * @return true if the material is enchanted, false otherwise
     */
    public boolean isEnchanted() {
        return enchanted;
    }

    /**
     * Sets the enchanted status of the material.
     *
     * @param enchanted true to enchant the material, false to remove enchantment
     */
    public void setEnchanted(boolean enchanted) {
        this.enchanted = enchanted;
    }

    /**
     * Gets the unique identifier for the material.
     *
     * @return the material id, or null if not set
     */
    @Nullable
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the material.
     *
     * @param id the new material id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the lore associated with the material.
     *
     * @return a list of lore strings; an empty list if lore is not set
     */
    @NotNull
    public List<String> getLore() {
        return lore;
    }

    /**
     * Sets the lore associated with the material.
     *
     * @param lore a list of lore strings; if null, an empty list is assigned
     */
    public void setLore(List<String> lore) {
        this.lore = lore == null ? new ArrayList<>() : lore;
    }

    /**
     * Gets the custom model data for the material.
     *
     * @return the custom model data as an integer
     */
    public int getModelData() {
        return modelData;
    }

    /**
     * Sets the custom model data for the material.
     *
     * @param modelData the new custom model data
     */
    public void setModelData(int modelData) {
        this.modelData = modelData;
    }

    /**
     * Gets the RGB color values for the material.
     *
     * @return an array of RGB strings representing the color values
     */
    public String[] getRgb() {
        return rgb;
    }

    /**
     * Sets the RGB color values for the material.
     *
     * @param rgb an array of RGB color values
     */
    public void setRgb(String[] rgb) {
        this.rgb = rgb;
    }

    /**
     * Get win item itemStack
     *
     * @return itemStack
     */
    public I getItemStack() {
        return itemStack;
    }

    /**
     * Set itemStack for win item
     *
     * @param itemStack itemStack
     */
    public void setItemStack(I itemStack) {
        this.itemStack = itemStack;
        updateMeta();
    }

    /**
     * Update {@link #itemStack} metadata
     */
    @Override
    public void updateMeta() {
        updateMeta(getDisplayName(), getLore(), getModelData(), isEnchanted(), getRgb());
    }

    /**
     * Creates a clone of the CaseDataMaterial instance.
     *
     * @return a cloned copy of this CaseDataMaterial
     * @throws AssertionError if cloning is not supported
     */
    @SuppressWarnings("unchecked")
    @Override
    public CaseDataMaterial<I> clone() {
        try {
            return (CaseDataMaterial<I>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * Provides a string representation of the CaseDataMaterial instance, including
     * all properties such as id, display name, enchanted status, model data, RGB values,
     * and lore.
     *
     * @return a string representation of the material's attributes
     */
    @Override
    public String toString() {
        return "Material{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", enchanted=" + enchanted +
                ", modelData=" + modelData +
                ", rgb=" + Arrays.toString(rgb) +
                ", lore=" + lore +
                '}';
    }
}
