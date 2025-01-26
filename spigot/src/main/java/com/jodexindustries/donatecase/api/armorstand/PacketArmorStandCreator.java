package com.jodexindustries.donatecase.api.armorstand;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3f;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.tools.BukkitUtils;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import me.tofaa.entitylib.meta.other.ArmorStandMeta;
import me.tofaa.entitylib.wrapper.WrapperLivingEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PacketArmorStandCreator implements ArmorStandCreator {
    private CaseLocation location;
    private final WrapperLivingEntity entity;
    private final ArmorStandMeta meta;

    public PacketArmorStandCreator(CaseLocation location) {
        entity = new WrapperLivingEntity(EntityTypes.ARMOR_STAND);
        entity.getEquipment().setNotifyChanges(true);
        for (Player p : Bukkit.getOnlinePlayers()) {
            entity.addViewer(p.getUniqueId());
        }
        meta = (ArmorStandMeta) entity.getEntityMeta();

        this.location = location;

    }

    @Override
    public void setEquipment(EquipmentSlot equipmentSlot, Object item) {
        com.github.retrooper.packetevents.protocol.item.ItemStack itemStack = SpigotReflectionUtil.decodeBukkitItemStack((ItemStack) item);
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
    public void setAngle(@NotNull ArmorStandEulerAngle angle) {
        meta.setHeadRotation(new Vector3f(
                (float) angle.getHead().getX(),
                (float) angle.getHead().getY(),
                (float) angle.getHead().getZ())
        );

        meta.setLeftArmRotation(new Vector3f(
                (float) angle.getLeftArm().getX(),
                (float) angle.getLeftArm().getY(),
                (float) angle.getLeftArm().getZ())
        );

        meta.setRightArmRotation(new Vector3f(
                (float) angle.getRightArm().getX(),
                (float) angle.getRightArm().getY(),
                (float) angle.getRightArm().getZ())
        );

        meta.setBodyRotation(new Vector3f(
                (float) angle.getBody().getX(),
                (float) angle.getBody().getY(),
                (float) angle.getBody().getZ())
        );

        meta.setLeftLegRotation(new Vector3f(
                (float) angle.getLeftLeg().getX(),
                (float) angle.getLeftLeg().getY(),
                (float) angle.getLeftLeg().getZ())
        );

        meta.setRightLegRotation(new Vector3f(
                (float) angle.getRightLeg().getX(),
                (float) angle.getRightLeg().getY(),
                (float) angle.getRightLeg().getZ())
        );
    }

    @Override
    public void setRotation(float yaw, float pitch) {
        entity.rotateHead(yaw, pitch);
    }

    @Override
    public void setVisible(boolean isVisible) {
        meta.setInvisible(!isVisible);
    }

    @Override
    public void setCustomName(String displayName) {
        if(displayName != null) meta.setCustomName(LegacyComponentSerializer.legacyAmpersand().deserialize(displayName));
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
    public void setGlowing(boolean glowing) {
        meta.setGlowing(glowing);
    }

    @Override
    public void setCollidable(boolean collidable) {
    }

    @Override
    public void setCustomNameVisible(boolean flag) {
        meta.setCustomNameVisible(flag);
    }

    @Override
    public CaseLocation getLocation() {
        return location;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return entity.getUuid();
    }

    @Override
    public void teleport(CaseLocation location) {
        entity.teleport(
                fromBukkitLocation(BukkitUtils.toBukkit(location))
        );
        this.location = location;
    }

    @Override
    public void remove() {
        entity.remove();
    }

    @Override
    public void spawn() {
        entity.spawn(
                fromBukkitLocation(BukkitUtils.toBukkit(location))
        );
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
