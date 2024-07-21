package com.jodexindustries.donatecase.api.armorstand;

import org.bukkit.Location;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.Metadatable;
import org.bukkit.util.EulerAngle;

public interface ArmorStandCreator extends Metadatable {
    void setVisible(boolean isVisible);
    void setCustomName(String displayName);
    void teleport(Location location);
    @Deprecated
    void setHelmet(ItemStack item);
    void setEquipment(EquipmentSlot equipmentSlot, ItemStack item);
    void setAngle(ArmorStandEulerAngle angle);
    void setRotation(float yaw, float pitch);
    void setHeadPose(EulerAngle eulerAngle);
    void setGravity(boolean hasGravity);
    void setSmall(boolean small);
    void setMarker(boolean marker);
    void setCollidable(boolean collidable);
    void setCustomNameVisible(boolean flag);
    Location getLocation();

    /**
     * Used for PacketArmorStandCreator
     */
    default void spawn() {}
    default void updateMeta() {}

    void remove();
}
