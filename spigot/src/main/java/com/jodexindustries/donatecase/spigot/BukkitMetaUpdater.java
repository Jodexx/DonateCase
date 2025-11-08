package com.jodexindustries.donatecase.spigot;

import com.jodexindustries.donatecase.api.data.casedata.MetaUpdater;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.spigot.tools.DCToolsBukkit;
import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.List;

public class BukkitMetaUpdater implements MetaUpdater {

    @Override
    public void updateMeta(Object itemStack, String displayName, List<String> lore, int modelData, boolean enchanted, String[] rgb) {
        if (!(itemStack instanceof ItemStack)) {
            return;
        }

        ItemStack item = (ItemStack) itemStack;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (displayName != null) meta.setDisplayName(DCTools.rc(displayName));
            if (lore != null) meta.setLore(DCTools.rc(lore));
            if (modelData != -1) meta.setCustomModelData(modelData);
            if (enchanted) meta.addEnchant(Enchantment.LURE, 1, true);

            if (rgb != null && rgb.length >= 3) {
                Color color = DCToolsBukkit.fromRGBString(rgb, Color.WHITE);
                updateColor(meta, color);
            }

            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_DYE);
            try {
                meta.addItemFlags(ItemFlag.valueOf("HIDE_POTION_EFFECTS"));
            } catch (IllegalArgumentException ignored) {
            }

            item.setItemMeta(meta);
        }
    }

    private void updateColor(ItemMeta meta, Color color) {
        try {
            Method setColorMethod = meta.getClass().getDeclaredMethod("setColor", Color.class);
            setColorMethod.invoke(meta, color);
        } catch (Exception ignored) {
        }
    }
}
