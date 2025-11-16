package com.jodexindustries.donatecase.spigot.animations.futurewheel;

import com.jodexindustries.donatecase.api.data.animation.Facing;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.spigot.animations.SoundSettings;
import org.bukkit.Particle;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class FutureWheelSettings {

    @Setting("StartPosition")
    public CaseLocation startPosition = new CaseLocation(.5, -.5, .5);

    @Setting("HologramPosition")
    public CaseLocation hologramPosition = new CaseLocation(0, 1.0, 0);

    @Setting("Spawn")
    public Spawn spawn = new Spawn();

    @Setting("Scroll")
    public ParticleSound scroll = new ParticleSound();

    @Setting("CircleRadius")
    public double radius = 1.0;

    @Setting("ScrollingTime")
    public int scrollingTime = 120;

    @Setting("SkipTicks")
    public int skipTicks = 5;

    @Nullable
    @Setting("Facing")
    public Facing facing = null;

    @ConfigSerializable
    public static class Spawn extends ParticleSound {

        @Setting("Interval")
        public int interval = 20;
    }

    @ConfigSerializable
    public static class ParticleSound extends SoundSettings {

        @Setting("Particle")
        public Particle particle = null;

        @Setting("ParticleCount")
        public int particleCount = 3;
    }
}
