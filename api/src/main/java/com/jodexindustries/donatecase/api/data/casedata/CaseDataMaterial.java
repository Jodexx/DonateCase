package com.jodexindustries.donatecase.api.data.casedata;

import com.jodexindustries.donatecase.api.DCAPI;
import lombok.Getter;
import lombok.Setter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.List;

/**
 * Represents material data for a case, including properties like display name,
 * enchantment status, lore, custom model data, and RGB values.
 * This class provides methods to get and set each attribute, allowing flexible
 * modification and retrieval of material details.
 */
@Setter
@Getter
@ConfigSerializable
public class CaseDataMaterial implements MetaUpdater, Cloneable {

    @Setting("ID")
    private String id;
    @Setting("DisplayName")
    private String displayName;
    @Setting("Enchanted")
    private boolean enchanted;
    @Setting("Lore")
    private List<String> lore;
    @Setting("ModelData")
    private int modelData;
    @Setting("Rgb")
    private String[] rgb;
    private transient Object itemStack = DCAPI.getInstance().getPlatform().getTools().loadCaseItem(id);

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
}
