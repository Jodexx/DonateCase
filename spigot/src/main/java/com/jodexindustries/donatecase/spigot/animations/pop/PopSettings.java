package com.jodexindustries.donatecase.spigot.animations.pop;

import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import org.bukkit.Sound;
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

    @ConfigSerializable
    public static class Scroll {

        @Setting("Sound")
        private String sound = "ENTITY_ITEM_PICKUP";

        @Setting("Volume")
        public float volume = 10;

        @Setting("Pitch")
        public float pitch = 1;

        // parse sound without Configurate TypeSerializer due Bukkit has interface instead of enum
        public Sound sound() {
            if (sound == null || sound.isEmpty()) return null;
            try {
                return Sound.valueOf(sound);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
