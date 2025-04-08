package com.jodexindustries.donatecase.spigot.animations.pop;

import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.api.data.animation.Facing;
import com.jodexindustries.donatecase.spigot.animations.SoundSettings;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class PopSettings {

    @Setting("Scroll")
    public Scroll scroll = new Scroll();

    @Setting("Facing")
    public Facing facing = Facing.SOUTH;

    @Setting("Rounded")
    public boolean rounded = true;

    @Setting("Radius")
    public double radius = 1.5;

    @Setting("Period")
    public int period;

    @Setting("ItemSlot")
    public EquipmentSlot itemSlot = EquipmentSlot.HEAD;

    @ConfigSerializable
    public static class Scroll extends SoundSettings {}

}
