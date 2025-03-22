package com.jodexindustries.donatecase.spigot.animations.shape;

import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.spigot.animations.SoundSettings;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class ShapeSettings {

    @Setting("StartPosition")
    public CaseLocation startPosition;

    @Setting("Pose")
    public ArmorStandEulerAngle pose;

    @Setting("SmallArmorStand")
    public boolean small = true;

    @Setting("Firework")
    public boolean firework = true;

    @Setting("Scroll")
    public Scroll scroll;

    @Setting("Particle")
    public Particle particle;

    @Setting("ItemSlot")
    public EquipmentSlot itemSlot = EquipmentSlot.HEAD;

    @ConfigSerializable
    public static class Scroll extends SoundSettings {

        @Setting("Period")
        public int period;

        @Setting("Height")
        public double height = 1.5;

        @Setting("Tail")
        public Tail tail;

        @Setting("Time")
        public int time = 40;

        @Setting("Interval")
        public int interval = 1;

        @Setting("Yaw")
        public float yaw = 20;

        public static class Tail {

            @Setting("Radius")
            public double radius = 0.5;
        }
    }

    @ConfigSerializable
    public static class Particle {

        @Setting("Orange")
        public Color orange;

        @Setting("White")
        public Color white;

        @ConfigSerializable
        public static class Color {

            @Setting("Rgb")
            public String rgb;

            @Setting("Size")
            public float size = 1;
        }
    }
}
