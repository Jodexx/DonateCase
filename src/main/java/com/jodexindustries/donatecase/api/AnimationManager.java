package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.dc.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class AnimationManager {
    public static Map<String, AnimationFactory> registeredAnimations = new HashMap<>();
    public static void registerAnimation(String name, AnimationFactory animation) {
        if(registeredAnimations.get(name) == null) {
            registeredAnimations.put(name, animation);
        } else {
            Bukkit.getLogger().warning("Animation with name " + name + " already registered!");
        }
    }
    public static void playAnimation(String name, Player player, Location location, String c) {
        AnimationFactory factory = registeredAnimations.get(name);
        Animation animation = factory.createAnimation();
        if (animation != null) {
            animation.start(player, location, c);
            Case.ActiveCase.put(location, c);
            for (Player pl : Bukkit.getOnlinePlayers()) {
                if (Case.openCase.containsKey(pl) && Main.t.isHere(location, Case.openCase.get(pl))) {
                    pl.closeInventory();
                }
            }

        } else {
            Bukkit.getLogger().warning("Animation " + name + " not found!");
        }
    }
    public static boolean isRegistered(String name) {
        return registeredAnimations.get(name) != null;
    }
    public static Map<String, AnimationFactory> getRegisteredAnimations() {
        return registeredAnimations;
    }
}
