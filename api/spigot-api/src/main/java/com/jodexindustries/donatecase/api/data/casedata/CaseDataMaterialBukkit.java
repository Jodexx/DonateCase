package com.jodexindustries.donatecase.api.data.casedata;

import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;

public class CaseDataMaterialBukkit extends CaseDataMaterial<ItemStack> {

    /**
     * Default constructor
     *
     * @param id          Material id
     * @param itemStack   Material ItemStack
     * @param displayName Material display name
     * @param enchanted   Is material enchanted
     * @param lore        Material lore
     * @param modelData   Material custom model data
     * @param rgb         Material rgb
     */
    public CaseDataMaterialBukkit(String id, ItemStack itemStack, String displayName, boolean enchanted, List<String> lore, int modelData, String[] rgb) {
        super(id, itemStack, displayName, enchanted, lore, modelData, rgb);
    }

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
        if (this.itemStack != null) {
            ItemMeta meta = this.itemStack.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(displayName);
                meta.setLore(lore);
                meta.setCustomModelData(modelData);
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
    public CaseDataMaterialBukkit clone() {
        CaseDataMaterialBukkit cloned = (CaseDataMaterialBukkit) super.clone();
        if (cloned.itemStack != null) cloned.setItemStack(cloned.itemStack.clone());
        return cloned;
    }

}
