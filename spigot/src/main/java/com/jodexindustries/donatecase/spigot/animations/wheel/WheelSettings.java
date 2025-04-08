package com.jodexindustries.donatecase.spigot.animations.wheel;

import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.spigot.animations.SoundSettings;
import org.bukkit.Particle;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class WheelSettings {

    @Setting("StartPosition")
    public CaseLocation startPosition;

    @Setting("CircleRadius")
    public double radius;

    @Setting("Scroll")
    public Scroll scroll;

    @Setting("Flame")
    public Flame flame;

    @Setting("ItemsCount")
    public int itemsCount;

    @Setting("ItemSlot")
    public EquipmentSlot itemSlot = EquipmentSlot.HEAD;

    @Setting("Pose")
    public ArmorStandEulerAngle armorStandEulerAngle;

    @Setting("Type")
    public WheelType wheelType = WheelType.RANDOM;

    @Setting("Shuffle")
    public boolean shuffle = true;

    @Setting("SmallArmorStand")
    public boolean smallArmorStand = true;

    public enum WheelType {
        FULL,  // No duplicates, all unique items
        RANDOM // Can have duplicates, random items
    }

    @ConfigSerializable
    public static class Scroll extends SoundSettings {

        @Setting("Time")
        public int time = 100;

        @Setting("Count")
        public int count = 1;

        @Setting("EaseAmount")
        public double easeAmount = 2.5;

    }

    @ConfigSerializable
    public static class Flame {

        @Setting("Enabled")
        public boolean enabled;

        @Setting("Particle")
        public Particle particle = Particle.FLAME;
    }
}
