package com.jodexindustries.donatecase.api.armorstand;

import org.bukkit.Location;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class PacketArmorStandCreator implements ArmorStandCreator {
    private final int id = (int) (Math.random() * Integer.MAX_VALUE);
    private Location location;

    public void spawnArmorStand(Location location) {
        byte yRot = (byte) ((location.getPitch() / 360.0) * 256);
        byte xRot = (byte) ((location.getYaw()/ 360.0) * 256);
        this.location = location;
    }
    public void setHelmet(ItemStack item) {

    }

    @Override
    public void setEquipment(EquipmentSlot equipmentSlot, ItemStack item) {

    }

    @Override
    public void setPose(ArmorStandEulerAngle angle) {

    }

    public void setVisible(boolean isVisible) {
    }



    public void setCustomName(String displayName) {
    }
    public void setGravity(boolean isGravity) {}

    @Override
    public void setSmall(boolean small) {
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public void teleport(Location location) {
        byte yRot = (byte) ((location.getPitch() / 360.0) * 256);
        byte xRot = (byte) ((location.getYaw()/ 360.0) * 256);
        this.location = location;
    }

    public void remove() {
    }
}
