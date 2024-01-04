package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.events.AnimationPreStartEvent;
import com.jodexindustries.donatecase.api.events.AnimationRegisteredEvent;
import com.jodexindustries.donatecase.api.events.AnimationStartEvent;
import com.jodexindustries.donatecase.dc.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class AnimationManager {
    private static Map<String, Class<? extends Animation>> registeredAnimations = new HashMap<>();

    /**
     * Register custom animation
     * @param name Animation name
     * @param animation Animation class
     */
    public static void registerAnimation(String name, Class<? extends Animation> animation) {
        if(registeredAnimations.get(name) == null) {
            registeredAnimations.put(name, animation);
            String animationName;
            String animationPluginName;
            Animation animationClass;
            boolean isDefault = false;
            try {
                animationClass = animation.newInstance();
                animationName = animationClass.getName();
                animationPluginName = JavaPlugin.getProvidingPlugin(animationClass.getClass()).getName();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            AnimationRegisteredEvent animationRegisteredEvent = new AnimationRegisteredEvent(animationName, animation, animationPluginName, isDefault);
            Bukkit.getServer().getPluginManager().callEvent(animationRegisteredEvent);
        } else {
            Main.instance.getLogger().warning("Animation with name " + name + " already registered!");
        }
    }
    /**
     * Play animation
     * @param name Animation name
     * @param player Player who opened case (for who animation played)
     * @param location Case location (with pitch and yaw player)
     * @param c Case type (from config)
     */
    public static void playAnimation(String name, Player player, Location location, String c) {
        Class<? extends Animation> animationClass = registeredAnimations.get(name);
        if (animationClass != null) {
            String winGroup = Case.getRandomGroup(c);
            AnimationPreStartEvent preStartEvent = new AnimationPreStartEvent(player, name,c, location, winGroup);
            Bukkit.getPluginManager().callEvent(preStartEvent);
            try {
                Animation animation = animationClass.newInstance();
                animation.start(player, Case.getCaseLocationByBlockLocation(location), c);
                Case.ActiveCase.put(location.getBlock().getLocation(), c);
                for (Player pl : Bukkit.getOnlinePlayers()) {
                    if (Case.playerOpensCase.containsKey(pl.getUniqueId()) && Main.t.isHere(location.getBlock().getLocation(), Case.playerOpensCase.get(pl.getUniqueId()).getLocation())) {
                        pl.closeInventory();
                    }
                }

                AnimationStartEvent startEvent = new AnimationStartEvent(player, name, c, location, preStartEvent.getWinGroup());
                Bukkit.getPluginManager().callEvent(startEvent);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            Main.instance.getLogger().warning("Animation " + name + " not found!");
        }
    }
    public static boolean isRegistered(String name) {
        return registeredAnimations.get(name) != null;
    }

    /**
     * Get all registered animations
     * @return map with registered animations
     */
    public static Map<String, Class<? extends Animation>> getRegisteredAnimations() {
        return registeredAnimations;
    }
}
