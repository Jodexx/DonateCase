package com.jodexindustries.donatecase.spigot.animations.firework;

import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class FireworkSettings {

    @Setting("StartPosition")
    public CaseLocation startPosition;

    @Setting("Pose")
    public ArmorStandEulerAngle pose;

    @Setting("SmallArmorStand")
    public boolean small = true;

    @Setting("Power")
    public int power;

    @Setting("FireworkColors")
    public List<String> fireworkColors = new ArrayList<>();

    @Setting("ItemSlot")
    public EquipmentSlot itemSlot = EquipmentSlot.HEAD;

    @Setting("Scroll")
    public Scroll scroll;

    @ConfigSerializable
    public static class Scroll {

        @Setting("Period")
        public int period;

        @Setting("Yaw")
        public float yaw;
    }
}
