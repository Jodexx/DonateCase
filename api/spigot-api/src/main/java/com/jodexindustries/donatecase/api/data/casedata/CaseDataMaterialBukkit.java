package com.jodexindustries.donatecase.api.data.casedata;

import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;

public class CaseDataMaterialBukkit extends CaseDataMaterial {

    /**
     * Update {@link #getItemStack()} metadata
     *
     * @param displayName Item display name
     * @param lore        Item lore
     * @param modelData   Item custom model data
     * @param enchanted   Item enchantment
     * @param rgb         Item rgb
     */
    public void updateMeta(String displayName, List<String> lore, int modelData,
                           boolean enchanted, String[] rgb) {
        if (getItemStack() != null) {
            ItemStack itemStack = getItemStack();
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                if(displayName != null) meta.setDisplayName(displayName);
                if(lore != null) meta.setLore(lore);
                if(modelData != -1) meta.setCustomModelData(modelData);
                if (enchanted) meta.addEnchant(Enchantment.LURE, 1, true);

                if (rgb != null && rgb.length >= 3 && meta instanceof LeatherArmorMeta) {
                    LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) meta;
                    leatherArmorMeta.setColor(DCToolsBukkit.fromRGBString(rgb, Color.WHITE));
                }

                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                meta.addItemFlags(ItemFlag.HIDE_DYE);
                try {
                    meta.addItemFlags(ItemFlag.valueOf("HIDE_POTION_EFFECTS"));
                } catch (IllegalArgumentException ignored) {
                }

                itemStack.setItemMeta(meta);
            }
        }
    }

    @Override
    public ItemStack getItemStack() {
        return (ItemStack) super.getItemStack();
    }

    @Override
    public CaseDataMaterialBukkit clone() {
        CaseDataMaterialBukkit cloned = (CaseDataMaterialBukkit) super.clone();
        if(getItemStack() != null) cloned.setItemStack(getItemStack().clone());
        return cloned;
    }

}
