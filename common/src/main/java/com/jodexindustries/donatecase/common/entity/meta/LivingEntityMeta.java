package com.jodexindustries.donatecase.common.entity.meta;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.HumanoidArm;
import com.github.retrooper.packetevents.util.Vector3i;

import java.util.Optional;

public class LivingEntityMeta extends EntityMeta {
    public static final byte OFFSET = EntityMeta.MAX_OFFSET;
    public static final byte MAX_OFFSET = OFFSET + 7;

    private final static byte IS_HAND_ACTIVE_BIT = 0x01;
    private final static byte ACTIVE_HAND_BIT = 0x02;
    private final static byte IS_IN_SPIN_ATTACK_BIT = 0x04;

    public LivingEntityMeta(int entityId) {
        super(entityId);
    }

    public LivingEntityMeta(EntityMeta metadata) {
        super(metadata);
    }

    public float getHealth() {
        return super.metadata.getIndex(offset(OFFSET,1), 1F);
    }


    public int getPotionEffectColor() {
        return super.metadata.getIndex(offset(OFFSET,2), 0);
    }

    public void setPotionEffectColor(int value) {
        super.metadata.setIndex(offset(OFFSET,2), EntityDataTypes.INT, value);
    }

    public void setPotionEffectColor(int red, int green, int blue) {
        setPotionEffectColor(red << 16 + green << 8 + blue);
    }

    public boolean isPotionEffectAmbient() {
        return super.metadata.getIndex(offset(OFFSET,3), false);
    }

    public void setPotionEffectAmbient(boolean value) {
        super.metadata.setIndex(offset(OFFSET,3), EntityDataTypes.BOOLEAN, value);
    }

    public int getArrowCount() {
        return super.metadata.getIndex(offset(OFFSET,4), 0);
    }

    public void setArrowCount(int value) {
        super.metadata.setIndex(offset(OFFSET,4), EntityDataTypes.INT, value);
    }

    public void setHealth(float value) {
        super.metadata.setIndex(offset(OFFSET,1), EntityDataTypes.FLOAT, value);
    }

    public HumanoidArm getActiveHand() {
        return getMaskBit(OFFSET, ACTIVE_HAND_BIT) ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
    }

    public void setActiveHand(HumanoidArm value) {
        setMaskBit(OFFSET, ACTIVE_HAND_BIT, value == HumanoidArm.LEFT);
    }

    public boolean isInRiptideSpinAttack() {
        return getMaskBit(OFFSET, IS_IN_SPIN_ATTACK_BIT);
    }

    public void setInRiptideSpinAttack(boolean value) {
        setMaskBit(OFFSET, IS_IN_SPIN_ATTACK_BIT, value);
    }

    public Optional<Vector3i> getBedPosition() {
        return super.metadata.getIndex(offset(OFFSET,6), Optional.empty());
    }

    public void setBedPosition(Vector3i value) {
        super.metadata.setIndex(offset(OFFSET,6), EntityDataTypes.OPTIONAL_BLOCK_POSITION, value == null ? Optional.empty() : Optional.of(value));
    }

    public int getBeeStingerCount() {
        return super.metadata.getIndex(offset(OFFSET,5), 0);
    }

    public void setBeeStingerCount(int value) {
        super.metadata.setIndex(offset(OFFSET,5), EntityDataTypes.INT, value);
    }

    public boolean isHandActive() {
        return getMaskBit(OFFSET, IS_HAND_ACTIVE_BIT);
    }

    public void setHandActive(boolean value) {
        setMaskBit(OFFSET, IS_HAND_ACTIVE_BIT, value);
    }

}