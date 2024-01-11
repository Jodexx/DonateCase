package com.jodexindustries.donatecase.api.armorstand;

import com.jodexindustries.donatecase.api.Case;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class BukkitArmorStandCreator implements ArmorStandCreator {
    private ArmorStand entity;
    @Override
    public void spawnArmorStand(Location location) {
        entity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        Case.armorStandList.add(entity);
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
        entity.setHelmet(item);
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
        Case.armorStandList.remove(entity);
        entity.remove();
    }
}
