package com.jodexindustries.freecases.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    public static final Map<UUID, Long> cooldowns = new HashMap<>();

    public static void setCooldown(UUID player, long time){
        if(time < 1) {
            cooldowns.remove(player);
        } else {
            cooldowns.put(player, time);
        }
    }

    public static long getCooldown(UUID player){
        return cooldowns.getOrDefault(player, 0L);
    }
}
