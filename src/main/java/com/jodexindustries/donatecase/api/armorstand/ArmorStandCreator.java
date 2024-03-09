package com.jodexindustries.donatecase.api.armorstand;

import org.bukkit.Location;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public interface ArmorStandCreator {
    void spawnArmorStand(Location location);
    void setVisible(boolean isVisible);
    void setCustomName(String displayName);
    void teleport(Location location);
    @Deprecated
    void setHelmet(ItemStack item);
    void setEquipment(EquipmentSlot equipmentSlot, ItemStack item);
    void setPose(ArmorStandEulerAngle angle);
    void setGravity(boolean hasGravity);
    void setSmall(boolean small);
    Location getLocation();
    void remove();
}
