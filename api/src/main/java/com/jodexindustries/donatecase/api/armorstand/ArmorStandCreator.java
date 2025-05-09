package com.jodexindustries.donatecase.api.armorstand;

import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface ArmorStandCreator {

    Map<Integer, ArmorStandCreator> armorStands = new HashMap<>();

    void setVisible(boolean isVisible);

    void setCustomName(@Nullable String displayName);

    void teleport(CaseLocation location);

    void setEquipment(EquipmentSlot equipmentSlot, Object item);

    void setAngle(@NotNull ArmorStandEulerAngle angle);

    void setRotation(float yaw, float pitch);

    void setGravity(boolean hasGravity);

    void setSmall(boolean small);

    void setMarker(boolean marker);

    void setGlowing(boolean glowing);

    boolean isGlowing();

    /**
     * Only with LivingEntity (not packet)
     *
     * @param collidable Set collidable
     */
    void setCollidable(boolean collidable);

    void setCustomNameVisible(boolean flag);

    boolean isCustomNameVisible();

    CaseLocation getLocation();

    @NotNull
    UUID getUniqueId();

    UUID getAnimationId();

    int getEntityId();

    default void spawn() {
    }

    default void updateMeta() {
    }

    void remove();
}
