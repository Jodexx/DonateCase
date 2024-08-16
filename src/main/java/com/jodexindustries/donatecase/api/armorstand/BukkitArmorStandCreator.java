package com.jodexindustries.donatecase.api.armorstand;

import com.jodexindustries.donatecase.api.Case;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class BukkitArmorStandCreator implements ArmorStandCreator {
    private final ArmorStand entity;

    public BukkitArmorStandCreator(Location location) {
        World world = location.getWorld();
        if (world == null) {
            entity = null;
            return;
        }
        entity = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
        entity.setMetadata("case", new FixedMetadataValue(Case.getInstance(), "case"));
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
    public void setMarker(boolean marker) {
        entity.setMarker(marker);
    }

    @Override
    public void setGlowing(boolean glowing) {
        entity.setGlowing(glowing);
    }

    @Override
    public void setCollidable(boolean collidable) {
        entity.setCollidable(collidable);
    }

    @Override
    public void setCustomNameVisible(boolean flag) {
        entity.setCustomNameVisible(flag);
    }

    @Override
    public void setMetadata(@NotNull String metadata, @NotNull MetadataValue value) {
        entity.setMetadata(metadata, value);
    }

    @NotNull
    @Override
    public List<MetadataValue> getMetadata(@NotNull String metadataKey) {
        return entity.getMetadata(metadataKey);
    }

    @Override
    public boolean hasMetadata(@NotNull String metadataKey) {
        return entity.hasMetadata(metadataKey);
    }

    @Override
    public void removeMetadata(@NotNull String metadataKey, @NotNull Plugin owningPlugin) {
        entity.removeMetadata(metadataKey, owningPlugin);
    }

    @Override
    public void setCustomName(String displayName) {
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
                    break;
                case OFF_HAND:
                    equipment.setItemInOffHand(item);
                    break;
                case CHEST:
                    equipment.setChestplate(item);
                    break;
                case LEGS:
                    equipment.setLeggings(item);
                    break;
                case HEAD:
                    equipment.setHelmet(item);
                    break;
                case FEET:
                    equipment.setBoots(item);
                    break;
            }
        }
    }

    @Override
    public void setAngle(ArmorStandEulerAngle angle) {
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
    public @NotNull UUID getUniqueId() {
        return entity.getUniqueId();
    }

    @Override
    public boolean isPacket() {
        return false;
    }

    @Override
    public ArmorStand getArmorStand() {
        return entity;
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
