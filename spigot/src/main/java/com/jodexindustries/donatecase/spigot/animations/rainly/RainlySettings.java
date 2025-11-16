package com.jodexindustries.donatecase.spigot.animations.rainly;

import com.jodexindustries.donatecase.api.armorstand.ArmorStandEulerAngle;
import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.spigot.animations.SoundSettings;
import org.bukkit.Particle;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class RainlySettings {

    @Setting("ItemSlot")
    public EquipmentSlot itemSlot = EquipmentSlot.HEAD;

    @Setting("CloudParticle")
    public Particle cloudParticle = Particle.CLOUD;

    @Setting("FallingParticle")
    public Particle fallingParticle = Particle.FALLING_WATER;

    @Setting("Pose")
    public ArmorStandEulerAngle armorStandEulerAngle;

    @Setting("SmallArmorStand")
    public boolean isSmall = true;

    @Setting("End")
    public ParticleSoundSettings end = new ParticleSoundSettings();

    @Setting("Scroll")
    public ParticleSoundSettings scroll = new ParticleSoundSettings();

    @ConfigSerializable
    public static class ParticleSoundSettings extends SoundSettings {

        @Setting("Particle")
        public Particle particle;
    }
}
