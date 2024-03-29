package com.jodexindustries.donatecase.api.armorstand;

import com.jodexindustries.donatecase.DonateCase;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;

public class BukkitArmorStandCreator implements ArmorStandCreator {
    private ArmorStand entity;
    @Override
    public void spawnArmorStand(Location location) {
        entity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        entity.setMetadata("case", new FixedMetadataValue(DonateCase.instance, "case"));
    }

    @Override
    public void setVisible(boolean isVisible) {
        entity.setVisible(isVisible);
    }

    @Override
    public void setSmall(boolean small) {
        entity.setSmall(small);
    }

    @Override
    public void setCustomName(String displayName) {
        entity.setCustomNameVisible(true);
        entity.setCustomName(displayName);
    }

    @Override
    public void teleport(Location location) {
        entity.teleport(location);
    }
    @Override
    public void setHelmet(ItemStack item) {
        EntityEquipment equipment = entity.getEquipment();
        if(equipment != null) equipment.setHelmet(item);
    }

    @Override
    public void setEquipment(EquipmentSlot equipmentSlot, ItemStack item) {
        EntityEquipment equipment = entity.getEquipment();
        if(equipment != null) {
            switch (equipmentSlot) {
                case HAND:
                    equipment.setItemInMainHand(item);
                case OFF_HAND:
                    equipment.setItemInOffHand(item);
                case CHEST:
                    equipment.setChestplate(item);
                case LEGS:
                    equipment.setLeggings(item);
                case HEAD:
                    equipment.setHelmet(item);
                case FEET:
                    equipment.setBoots(item);
            }
        }
    }

    @Override
    public void setPose(ArmorStandEulerAngle angle) {
        entity.setHeadPose(angle.getHead());
        entity.setBodyPose(angle.getBody());
        entity.setLeftArmPose(angle.getLeftArm());
        entity.setRightArmPose(angle.getRightArm());
        entity.setLeftLegPose(angle.getLeftLeg());
        entity.setRightLegPose(angle.getRightLeg());
    }

    @Override
    public void setRotation(float yaw, float pitch) {
        entity.setRotation(yaw, pitch);
    }

    @Override
    public void setHeadPose(EulerAngle eulerAngle) {
        entity.setHeadPose(eulerAngle);
    }

    @Override
    public Location getLocation() {
        return entity.getLocation();
    }

    @Override
    public void setGravity(boolean hasGravity) {
        entity.setGravity(hasGravity);
    }

    @Override
    public void remove() {
        entity.remove();
    }
}
