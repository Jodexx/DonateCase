package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.events.AnimationPreStartEvent;
import com.jodexindustries.donatecase.api.events.AnimationRegisteredEvent;
import com.jodexindustries.donatecase.api.events.AnimationStartEvent;
import com.jodexindustries.donatecase.api.events.AnimationUnregisteredEvent;
import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class AnimationManager {
    private static final Map<String, Class<? extends Animation>> registeredAnimations = new HashMap<>();

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
     * Unregister custom animation
     * @param name Animation name
     */
    public static void unregisterAnimation(String name) {
        if(registeredAnimations.get(name) != null) {
            registeredAnimations.remove(name);
            AnimationUnregisteredEvent animationUnRegisteredEvent = new AnimationUnregisteredEvent(name);
            Bukkit.getServer().getPluginManager().callEvent(animationUnRegisteredEvent);
        } else {
            Main.instance.getLogger().warning("Animation with name " + name + " already unregistered!");
        }
    }
    /**
     * Play animation
     * @param name Animation name
     * @param player Player who opened case (for who animation played)
     * @param location Case location (with pitch and yaw player)
     * @param c Case data
     */
    public static void playAnimation(String name, Player player, Location location, CaseData c) {
        Animation animation = getRegisteredAnimation(name);
        if (animation != null) {
            CaseData.Item winItem = Case.getRandomItem(c);
            winItem.getMaterial().setDisplayName(PAPISupport.setPlaceholders(player,winItem.getMaterial().getDisplayName()));
            AnimationPreStartEvent preStartEvent = new AnimationPreStartEvent(player, name, c, location, winItem);
            Bukkit.getPluginManager().callEvent(preStartEvent);
            animation.start(player, Case.getCaseLocationByBlockLocation(location), c, preStartEvent.getWinItem());
            Case.activeCases.put(location.getBlock().getLocation(), c.getCaseName());
            for (Player pl : Bukkit.getOnlinePlayers()) {
                if (Case.playersCases.containsKey(pl.getUniqueId()) && Main.t.isHere(location.getBlock().getLocation(), Case.playersCases.get(pl.getUniqueId()).getLocation())) {
                    pl.closeInventory();
                }
            }

            AnimationStartEvent startEvent = new AnimationStartEvent(player, name, c, location, preStartEvent.getWinItem());
            Bukkit.getPluginManager().callEvent(startEvent);
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

    /**
     * Get registered animation
     * @param animation Animation name
     * @return Animation class instance
     */
    private static Animation getRegisteredAnimation(String animation) {
        if (registeredAnimations.containsKey(animation)) {
            try {
                Class<? extends Animation> animationClass = getRegisteredAnimations().get(animation);
                return animationClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
