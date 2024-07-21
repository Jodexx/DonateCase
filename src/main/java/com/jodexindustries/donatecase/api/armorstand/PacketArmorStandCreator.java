package com.jodexindustries.donatecase.api.armorstand;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import me.tofaa.entitylib.meta.other.ArmorStandMeta;
import me.tofaa.entitylib.wrapper.WrapperLivingEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PacketArmorStandCreator implements ArmorStandCreator {
    private Location location;
    private final WrapperLivingEntity entity;
    private final ArmorStandMeta meta;
    private final EntityMetadataStore metadataStore;

    public PacketArmorStandCreator(Location location) {
        metadataStore = new EntityMetadataStore();
        entity = new WrapperLivingEntity(EntityTypes.ARMOR_STAND);
        entity.getEquipment().setNotifyChanges(true);
        for (Player p : Bukkit.getOnlinePlayers()) {
            entity.addViewer(p.getUniqueId());
        }
        meta = (ArmorStandMeta) entity.getEntityMeta();

        this.location = location;

    }

    @Override
    public void setHelmet(ItemStack item) {
        setEquipment(EquipmentSlot.HEAD, item);
    }

    @Override
    public void setEquipment(EquipmentSlot equipmentSlot, ItemStack item) {
        com.github.retrooper.packetevents.protocol.item.ItemStack itemStack = SpigotReflectionUtil.decodeBukkitItemStack(item);
        switch (equipmentSlot) {
            case LEGS:
                entity.getEquipment().setLeggings(itemStack);
                break;
            case FEET:
                entity.getEquipment().setBoots(itemStack);
                break;
            case OFF_HAND:
                entity.getEquipment().setOffhand(itemStack);
                break;
            case CHEST:
                entity.getEquipment().setChestplate(itemStack);
                break;
            case HAND:
                entity.getEquipment().setMainHand(itemStack);
                break;
            case HEAD:
                entity.getEquipment().setHelmet(itemStack);
                break;
        }
    }

    @Override
    public void setAngle(ArmorStandEulerAngle angle) {
        meta.setHeadRotation(meta.getHeadRotation().add((float) angle.getHead().getX(),
                (float) angle.getHead().getY(),
                (float) angle.getHead().getZ()));

        meta.setLeftArmRotation(meta.getLeftArmRotation().add(
                (float) angle.getLeftArm().getX(),
                (float) angle.getLeftArm().getY(),
                (float) angle.getLeftArm().getZ()));

        meta.setRightArmRotation(meta.getRightArmRotation().add(
                (float) angle.getRightArm().getX(),
                (float) angle.getRightArm().getY(),
                (float) angle.getRightArm().getZ()));

        meta.setBodyRotation(meta.getBodyRotation().add((float) angle.getBody().getX(),
                (float) angle.getBody().getY(),
                (float) angle.getBody().getZ()));

        meta.setLeftLegRotation(meta.getLeftLegRotation().add(
                (float) angle.getLeftLeg().getX(),
                (float) angle.getLeftLeg().getY(),
                (float) angle.getLeftLeg().getZ()));

        meta.setRightLegRotation(meta.getRightLegRotation().add(
                (float) angle.getRightLeg().getX(),
                (float) angle.getRightLeg().getY(),
                (float) angle.getRightLeg().getZ()));
    }

    @Override
    public void setRotation(float yaw, float pitch) {
        entity.rotateHead(yaw, pitch);
    }

    @Override
    public void setHeadPose(EulerAngle eulerAngle) {
        meta.setHeadRotation(meta.getHeadRotation().add((float) eulerAngle.getX(),
                (float) eulerAngle.getY(),
                (float) eulerAngle.getZ()));
    }

    @Override
    public void setVisible(boolean isVisible) {
        meta.setInvisible(!isVisible);
    }

    @Override
    public void setCustomName(String displayName) {
        meta.setCustomName(LegacyComponentSerializer.legacyAmpersand().deserialize(displayName));
    }

    @Override
    public void setGravity(boolean isGravity) {
        meta.setHasNoGravity(isGravity);
    }

    @Override
    public void setSmall(boolean small) {
        meta.setSmall(small);
    }

    @Override
    public void setMarker(boolean marker) {
        meta.setMarker(marker);
    }

    @Override
    public void setCollidable(boolean collidable) {}

    @Override
    public void setCustomNameVisible(boolean flag) {
        meta.setCustomNameVisible(flag);
    }

    @Override
    public void setMetadata(@NotNull String metadata, @NotNull MetadataValue value) {
        metadataStore.setMetadata(entity.getUuid(), metadata, value);
    }

    @NotNull
    @Override
    public List<MetadataValue> getMetadata(@NotNull String metadataKey) {
        return metadataStore.getMetadata(entity.getUuid(), metadataKey);
    }

    @Override
    public boolean hasMetadata(@NotNull String metadataKey) {
        return metadataStore.hasMetadata(entity.getUuid(), metadataKey);
    }

    @Override
    public void removeMetadata(@NotNull String metadataKey, @NotNull Plugin owningPlugin) {
        metadataStore.removeMetadata(entity.getUuid(), metadataKey, owningPlugin);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void teleport(Location location) {
        entity.teleport(fromBukkitLocation(location));
        this.location = location;
    }

    @Override
    public void remove() {
        entity.remove();
    }

    @Override
    public void spawn() {
        entity.spawn(fromBukkitLocation(location));
    }

    @Override
    public void updateMeta() {
        entity.sendPacketToViewers(meta.createPacket());
    }

    public static com.github.retrooper.packetevents.protocol.world.Location fromBukkitLocation(Location location) {
        return new com.github.retrooper.packetevents.protocol.world.Location(
                location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
}
