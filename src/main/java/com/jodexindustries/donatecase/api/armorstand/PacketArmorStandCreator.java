package com.jodexindustries.donatecase.api.armorstand;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.jodexindustries.donatecase.api.armorstand.packets.*;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

import static com.jodexindustries.donatecase.dc.Main.instance;
import static com.jodexindustries.donatecase.dc.Main.t;

public class PacketArmorStandCreator implements ArmorStandCreator {
    private int entityId;
    private Location location;

    public void spawnArmorStand(Location location) {
        entityId = (int) (Math.random() * Integer.MAX_VALUE);
        WrapperPlayServerSpawnEntityLiving entity = new WrapperPlayServerSpawnEntityLiving();
        entity.setEntityID(entityId);
        entity.setType(EntityType.ARMOR_STAND);
        entity.setX(location.getX());
        entity.setY(location.getY());
        entity.setZ(location.getZ());
        this.location = location;


        entity.broadcastPacket();

        instance.getEntityIds().add(entityId);
    }
    public void setHelmet(ItemStack item) {
        WrapperPlayServerEntityEquipment entityEquipment = new WrapperPlayServerEntityEquipment();
        entityEquipment.setEntityID(entityId);
        entityEquipment.setSlot(EnumWrappers.ItemSlot.HEAD);
        entityEquipment.setItem(item);
        entityEquipment.broadcastPacket();

    }

    public void setVisible(boolean isVisible) {
        int i = t.parseIntFromBoolean(isVisible);
        WrapperPlayServerEntityMetadata entityMetadata = new WrapperPlayServerEntityMetadata();
        entityMetadata.setEntityID(entityId);
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.WrappedDataWatcherObject visible = new WrappedDataWatcher.WrappedDataWatcherObject(
                i, WrappedDataWatcher.Registry.get(Byte.class));
        watcher.setObject(visible, (byte) 0x20);
        entityMetadata.setMetadata(watcher.getWatchableObjects());
        entityMetadata.broadcastPacket();
    }



    public void setCustomName(String displayName) {
        WrapperPlayServerEntityMetadata entityMetadata = new WrapperPlayServerEntityMetadata();
        entityMetadata.setEntityID(entityId);
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        Optional<?> optional = Optional.of(WrappedChatComponent.fromChatMessage(t.rc(displayName))[0]
                .getHandle());
        WrappedDataWatcher.WrappedDataWatcherObject nameq = new WrappedDataWatcher.WrappedDataWatcherObject(
                2, WrappedDataWatcher.Registry.getChatComponentSerializer(true));
        watcher.setObject(nameq, optional);

        WrappedDataWatcher.WrappedDataWatcherObject nameVisible = new WrappedDataWatcher.WrappedDataWatcherObject(
                3, WrappedDataWatcher.Registry.get(Boolean.class));
        watcher.setObject(nameVisible, true);
        entityMetadata.setMetadata(watcher.getWatchableObjects());
        entityMetadata.broadcastPacket();
    }
    public void setGravity(boolean isGravity) {
        WrapperPlayServerEntityMetadata entityMetadata = new WrapperPlayServerEntityMetadata();
        entityMetadata.setEntityID(entityId);
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.WrappedDataWatcherObject gravity = new WrappedDataWatcher.WrappedDataWatcherObject(
                5, WrappedDataWatcher.Registry.get(Boolean.class));
        watcher.setObject(gravity, isGravity);
        entityMetadata.setMetadata(watcher.getWatchableObjects());
        entityMetadata.broadcastPacket();
    }

    @Override
    public void setSmall(boolean small) {
        WrapperPlayServerEntityMetadata entityMetadata = new WrapperPlayServerEntityMetadata();
        entityMetadata.setEntityID(entityId);
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.WrappedDataWatcherObject smallW = new WrappedDataWatcher.WrappedDataWatcherObject(
                t.parseIntFromBoolean(small), WrappedDataWatcher.Registry.get(Byte.class));
        watcher.setObject(smallW, (byte) 0x01);
        entityMetadata.setMetadata(watcher.getWatchableObjects());
        entityMetadata.broadcastPacket();
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public void teleport(Location location) {
        WrapperPlayServerEntityTeleport entityTeleport = new WrapperPlayServerEntityTeleport();
        entityTeleport.setEntityID(entityId);
        entityTeleport.setX(location.getX());
        entityTeleport.setY(location.getY());
        entityTeleport.setZ(location.getZ());
        entityTeleport.setYaw(location.getYaw());
        entityTeleport.setPitch(location.getPitch());
        this.location = location;
        entityTeleport.broadcastPacket();
    }

    public void remove() {
        WrapperPlayServerEntityDestroy entityDestroy = new WrapperPlayServerEntityDestroy();
        entityDestroy.setEntityIds(new int[]{entityId});
        entityDestroy.broadcastPacket();
    }
}
