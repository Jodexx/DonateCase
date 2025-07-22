package com.jodexindustries.donatecase.common.entity.creator;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.common.entity.EntityEquipment;
import com.jodexindustries.donatecase.common.entity.meta.armorstand.ArmorStandMeta;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class APASC implements ArmorStandCreator {

    private final PacketEventsAPI<?> api = PacketEvents.getAPI();

    private final UUID uuid;
    private final Collection<?> viewers;
    private final UUID animationId;
    private final ArmorStandMeta meta;
    private final EntityEquipment equipment;

    private CaseLocation location;

    public APASC(Collection<?> viewers, UUID animationId, int entityId, CaseLocation location) {
        this.uuid = UUID.randomUUID();
        this.viewers = viewers;
        this.animationId = animationId;
        this.meta = new ArmorStandMeta(entityId);
        this.equipment = new EntityEquipment(entityId);
        this.location = location;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        APASC that = (APASC) object;
        return getEntityId() == that.getEntityId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getEntityId());
    }

    @Override
    public void setVisible(boolean isVisible) {
        meta.setInvisible(!isVisible);
    }

    @Override
    public void setCustomName(@Nullable String displayName) {
        this.meta.setCustomName(displayName == null ? null :
                LegacyComponentSerializer.legacySection().deserialize(DCTools.rc(displayName)));
    }

    @Override
    public void teleport(CaseLocation location) {
        this.location = location;

        this.sendPacketToViewers(
                new WrapperPlayServerEntityTeleport(
                        this.meta.getEntityId(),
                        getPosition(),
                        location.yaw(), location.pitch(), false
                )
        );
    }

    protected void setEquipment0(EquipmentSlot equipmentSlot, ItemStack itemStack) {
        switch (equipmentSlot) {
            case LEGS:
                equipment.setLeggings(itemStack);
                break;
            case FEET:
                equipment.setBoots(itemStack);
                break;
            case OFF_HAND:
                equipment.setOffhand(itemStack);
                break;
            case CHEST:
                equipment.setChestplate(itemStack);
                break;
            case HAND:
                equipment.setMainHand(itemStack);
                break;
            case HEAD:
                equipment.setHelmet(itemStack);
                break;
        }

        sendPacketToViewers(equipment.createPacket());
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
        this.location.yaw(yaw);
        this.location.pitch(pitch);
        this.sendPacketToViewers(new WrapperPlayServerEntityRotation(meta.getEntityId(), yaw, pitch, false),
                new WrapperPlayServerEntityHeadLook(meta.getEntityId(), yaw));
    }

    @Override
    public void setGravity(boolean hasGravity) {
        meta.setHasNoGravity(hasGravity);
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
    public boolean isGlowing() {
        return meta.isGlowing();
    }

    @Override
    public void setCollidable(boolean collidable) {
    }

    @Override
    public void setCustomNameVisible(boolean flag) {
        meta.setCustomNameVisible(flag);
    }

    @Override
    public boolean isCustomNameVisible() {
        return meta.isCustomNameVisible();
    }

    @Override
    public CaseLocation getLocation() {
        return location;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return uuid;
    }

    @Override
    public UUID getAnimationId() {
        return animationId;
    }

    @Override
    public int getEntityId() {
        return meta.getEntityId();
    }

    @Override
    public void remove() {
        ArmorStandCreator.armorStands.remove(meta.getEntityId());
        this.sendPacketToViewers(new WrapperPlayServerDestroyEntities(meta.getEntityId()));
    }

    @Override
    public void spawn() {
        this.sendPacketToViewers(
                new WrapperPlayServerSpawnEntity(
                        this.meta.getEntityId(), Optional.of(this.uuid), EntityTypes.ARMOR_STAND,
                        getPosition(),
                        location.pitch(), location.yaw(), location.yaw(), 0, Optional.empty()
                )
        );
        this.sendPacketToViewers(this.meta.createPacket());
    }

    @Override
    public void updateMeta() {
        this.sendPacketToViewers(meta.createPacket());
    }

    private void sendPacketToViewers(PacketWrapper<?>... wrappers) {
        for (Object viewer : viewers) {
            for (PacketWrapper<?> wrapper : wrappers) {
                api.getPlayerManager().sendPacket(viewer, wrapper);
            }
        }
    }

    private Vector3d getPosition() {
        return new Vector3d(location.x(), location.y(), location.z());
    }
}
