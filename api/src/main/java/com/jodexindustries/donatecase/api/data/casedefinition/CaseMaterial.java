package com.jodexindustries.donatecase.api.data.casedefinition;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.MetaUpdater;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Accessors(fluent = true, chain = false)
@Setter
@Getter
public class CaseMaterial implements MetaUpdater, Cloneable {

    private String id;

    private String displayName;

    private boolean enchanted;

    private List<String> lore;

    private int modelData;

    private String[] rgb;

    private Object itemStack;

    public CaseMaterial(String id, String displayName, boolean enchanted, List<String> lore, int modelData, String[] rgb, Object itemStack) {
        this.id = id;
        this.displayName = displayName;
        this.enchanted = enchanted;
        this.lore = lore;
        this.modelData = modelData;
        this.rgb = rgb;
        this.itemStack = itemStack;
    }

    public void updateMeta() {
        updateMeta(itemStack, displayName, lore, modelData, enchanted, rgb);
    }

    public void updateMeta(@NotNull MetaUpdater metaUpdater) {
        metaUpdater.updateMeta(itemStack, displayName, lore, modelData, enchanted, rgb);
    }

    @Override
    public void updateMeta(Object itemStack, String displayName, List<String> lore, int modelData, boolean enchanted, String[] rgb) {
        DCAPI.getInstance().getPlatform().getMetaUpdater().updateMeta(itemStack, displayName, lore, modelData, enchanted, rgb);
    }

    @Override
    public CaseMaterial clone() {
        try {
            CaseMaterial cloned = (CaseMaterial) super.clone();
            if (itemStack instanceof Cloneable) cloned.itemStack = cloneItemStack(itemStack);
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
