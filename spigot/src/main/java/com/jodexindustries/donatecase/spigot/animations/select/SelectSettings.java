package com.jodexindustries.donatecase.spigot.animations.select;

import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.spigot.animations.Facing;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class SelectSettings {

    @Setting("Facing")
    public Facing facing = Facing.SOUTH;

    @Setting("Period")
    public int period;

    @Setting("Timeout")
    public int timeout = 600; // 30 seconds

    @Setting("Radius")
    public double radius = 1;

    @Setting("ItemSlot")
    public EquipmentSlot itemSlot = EquipmentSlot.HEAD;

}
