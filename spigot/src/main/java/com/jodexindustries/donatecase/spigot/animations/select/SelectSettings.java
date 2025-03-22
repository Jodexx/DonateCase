package com.jodexindustries.donatecase.spigot.animations.select;

import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class SelectSettings {

    @Setting("Facing")
    public Facing facing = Facing.SOUTH;

    @Setting("Period")
    public int period;

    @Setting("ItemSlot")
    public EquipmentSlot itemSlot = EquipmentSlot.HEAD;

    public enum Facing {
        WEST(90),
        NORTH(180),
        EAST(270),
        SOUTH(0);

        public final float yaw;

        Facing(float yaw) {
            this.yaw = yaw;
        }
    }
}
