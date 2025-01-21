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
public abstract class CaseDataMaterial implements MetaUpdatable, Cloneable {

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

    @Override
    public void updateMeta() {
        updateMeta(getDisplayName(), getLore(), getModelData(), isEnchanted(), getRgb());
    }

    @Override
    public CaseDataMaterial clone() {
        try {
            return (CaseDataMaterial) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
