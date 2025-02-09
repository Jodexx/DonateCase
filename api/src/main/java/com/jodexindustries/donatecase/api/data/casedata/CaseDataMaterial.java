package com.jodexindustries.donatecase.api.data.casedata;

import com.jodexindustries.donatecase.api.DCAPI;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

/**
 * Represents material data for a case, including properties like display name,
 * enchantment status, lore, custom model data, and RGB values.
 * This class provides methods to get and set each attribute, allowing flexible
 * modification and retrieval of material details.
 */
@Setter
@Getter
public class CaseDataMaterial implements MetaUpdater, Cloneable {

    private String id;
    private String displayName;
    private boolean enchanted;
    private List<String> lore;
    private int modelData;
    private String[] rgb;
    private Object itemStack;

    public void updateMeta() {
        updateMeta(getItemStack(), getDisplayName(), getLore(), getModelData(), isEnchanted(), getRgb());
    }

    @Override
    public void updateMeta(Object itemStack, String displayName, List<String> lore, int modelData, boolean enchanted, String[] rgb) {
        DCAPI.getInstance().getPlatform().getMetaUpdater().updateMeta(itemStack, displayName, lore, modelData, enchanted, rgb);
    }

    @Override
    public CaseDataMaterial clone() {
        try {
            CaseDataMaterial cloned = (CaseDataMaterial) super.clone();

            if (itemStack instanceof Cloneable) {
                cloned.itemStack = cloneItemStack(itemStack);
            } else {
                cloned.itemStack = itemStack;
            }

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    private Object cloneItemStack(Object itemStack) {
        try {
            return itemStack.getClass().getMethod("clone").invoke(itemStack);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to clone itemStack", e);
        }
    }

    @Override
    public String toString() {
        return "CaseDataMaterial{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", enchanted=" + enchanted +
                ", lore=" + lore +
                ", modelData=" + modelData +
                ", rgb=" + Arrays.toString(rgb) +
                ", itemStack=" + itemStack +
                '}';
    }
}
