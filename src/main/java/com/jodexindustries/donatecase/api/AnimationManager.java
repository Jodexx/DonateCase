package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.dc.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class AnimationManager {
    private static Map<String, Class<? extends Animation>> registeredAnimations = new HashMap<>();
    public static void registerAnimation(String name, Class<? extends Animation> animation) {
        if(registeredAnimations.get(name) == null) {
            registeredAnimations.put(name, animation);
        } else {
            Bukkit.getLogger().warning("Animation with name " + name + " already registered!");
        }
    }
    public static void playAnimation(String name, Player player, Location location, String c) {
        Class<? extends Animation> animationClass = registeredAnimations.get(name);
        if (animationClass != null) {
            try {
                Animation animation = animationClass.newInstance();
                animation.start(player, location, c);
                Case.ActiveCase.put(location.getBlock().getLocation(), c);
                for (Player pl : Bukkit.getOnlinePlayers()) {
                    if (Case.openCase.containsKey(pl) && Main.t.isHere(location.getBlock().getLocation(), Case.openCase.get(pl))) {
                        pl.closeInventory();
                    }
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getLogger().warning("Animation " + name + " not found!");
        }
    }
    public static boolean isRegistered(String name) {
        return registeredAnimations.get(name) != null;
    }
    public static Map<String, Class<? extends Animation>> getRegisteredAnimations() {
        return registeredAnimations;
    }
}
