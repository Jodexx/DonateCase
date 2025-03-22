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
    public String itemSlot;

    @Setting("Pose")
    public ArmorStandEulerAngle armorStandEulerAngle;

    @Setting("Type")
    public String wheelType;

    public EquipmentSlot getItemSlot() {
        if (itemSlot == null) return EquipmentSlot.HEAD;
        try {
            return EquipmentSlot.valueOf(itemSlot);
        } catch (IllegalArgumentException e) {
            return EquipmentSlot.HEAD;
        }
    }

    /**
     * Safely parse the wheel type.
     */
    public WheelType getWheelType() {
        if (wheelType == null) return WheelType.RANDOM;
        try {
            return WheelType.valueOf(wheelType);
        } catch (IllegalArgumentException e) {
            return WheelType.RANDOM;
        }
    }

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
