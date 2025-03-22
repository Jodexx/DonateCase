package com.jodexindustries.donatecase.spigot.animations;

import org.bukkit.Sound;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class SoundSettings {

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
