package com.jodexindustries.donatecase.spigot.api.armorstand;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3f;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import me.tofaa.entitylib.meta.other.ArmorStandMeta;
import me.tofaa.entitylib.wrapper.WrapperLivingEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class PacketArmorStandCreator implements ArmorStandCreator {

    private CaseLocation location;
    private final UUID animationId;
    private final WrapperLivingEntity entity;
    private final ArmorStandMeta meta;

    public PacketArmorStandCreator(UUID animationId, CaseLocation location) {
        this.animationId = animationId;
        entity = new WrapperLivingEntity(EntityTypes.ARMOR_STAND);
        entity.getEquipment().setNotifyChanges(true);
        for (Player p : Bukkit.getOnlinePlayers()) {
            entity.addViewer(p.getUniqueId());
        }
        meta = (ArmorStandMeta) entity.getEntityMeta();

        this.location = location;

        ArmorStandCreator.armorStands.put(entity.getEntityId(), this);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        PacketArmorStandCreator that = (PacketArmorStandCreator) object;
        return getEntityId() == that.getEntityId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getEntityId());
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
        if(displayName != null)
            meta.setIndex((byte) 2, EntityDataTypes.OPTIONAL_ADV_COMPONENT,
                    Optional.of(LegacyComponentSerializer.legacyAmpersand().deserialize(DCTools.rc(displayName))));
    }

    @Override
    public void setGravity(boolean isGravity) {
        meta.setIndex((byte) 5, EntityDataTypes.BOOLEAN, isGravity);
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
        meta.setIndex((byte) 3, EntityDataTypes.BOOLEAN, flag);
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
    public UUID getAnimationId() {
        return animationId;
    }

    @Override
    public int getEntityId() {
        return entity.getEntityId();
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
        ArmorStandCreator.armorStands.remove(entity.getEntityId());
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
