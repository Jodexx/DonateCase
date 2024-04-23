package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.Animation;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.events.AnimationPreStartEvent;
import com.jodexindustries.donatecase.api.events.AnimationRegisteredEvent;
import com.jodexindustries.donatecase.api.events.AnimationStartEvent;
import com.jodexindustries.donatecase.api.events.AnimationUnregisteredEvent;
import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Animation control class, registration, playing
 */
public class AnimationManager {
    private static final Map<String, Animation> registeredAnimations = new HashMap<>();
    private final Addon addon;
    public AnimationManager(Addon addon) {
        this.addon = addon;
    }
    /**
     * Register custom animation
     * @param name Animation name
     * @param animation Animation class
     */
    public void registerAnimation(String name, Animation animation) {
        if(registeredAnimations.get(name) == null) {
            registeredAnimations.put(name, animation);
            String animationPluginName = addon.getName();
            boolean isDefault = false;
            AnimationRegisteredEvent animationRegisteredEvent = new AnimationRegisteredEvent(animation.getName(), animation, animationPluginName, isDefault);
            Bukkit.getServer().getPluginManager().callEvent(animationRegisteredEvent);
        } else {
            DonateCase.instance.getLogger().warning("Animation with name " + name + " already registered!");
        }
    }

    /**
     * Unregister custom animation
     * @param name Animation name
     */
    public void unregisterAnimation(String name) {
        if(registeredAnimations.containsKey(name)) {
            registeredAnimations.remove(name);
            AnimationUnregisteredEvent animationUnRegisteredEvent = new AnimationUnregisteredEvent(name);
            Bukkit.getServer().getPluginManager().callEvent(animationUnRegisteredEvent);
        } else {
            DonateCase.instance.getLogger().warning("Animation with name " + name + " already unregistered!");
        }
    }
    /**
     * Play animation
     * @param name Animation name
     * @param player Player who opened case (for who animation played)
     * @param location Case location (with pitch and yaw player)
     * @param c Case data
     */
    public void playAnimation(String name, Player player, Location location, CaseData c) {
        if(addon.getCaseAPI().getHologramManager() != null && c.getHologram().isEnabled()) {
            addon.getCaseAPI().getHologramManager().removeHologram(location.getBlock());
        }

        Animation animation = getRegisteredAnimation(name);
        if (animation != null) {
            CaseData.Item winItem = addon.getCaseAPI().getRandomItem(c);
            winItem.getMaterial().setDisplayName(PAPISupport.setPlaceholders(player,winItem.getMaterial().getDisplayName()));
            AnimationPreStartEvent preStartEvent = new AnimationPreStartEvent(player, name, c, location, winItem);
            Bukkit.getPluginManager().callEvent(preStartEvent);

            ActiveCase activeCase = new ActiveCase(location, c.getCaseName());
            UUID uuid = UUID.randomUUID();
            CaseAPI.activeCases.put(uuid, activeCase);
            CaseAPI.activeCasesByLocation.put(location, uuid);

            animation.start(player,  addon.getCaseAPI().getCaseLocationByBlockLocation(location), uuid, c, preStartEvent.getWinItem());
            for (Player pl : Bukkit.getOnlinePlayers()) {
                if (CaseAPI.playersGui.containsKey(pl.getUniqueId()) && Tools.isHere(location.getBlock().getLocation(), CaseAPI.playersGui.get(pl.getUniqueId()).getLocation())) {
                    pl.closeInventory();
                }
            }

            AnimationStartEvent startEvent = new AnimationStartEvent(player, name, c, location, preStartEvent.getWinItem());
            Bukkit.getPluginManager().callEvent(startEvent);
        } else {
            DonateCase.instance.getLogger().warning("Animation " + name + " not found!");
        }
    }

    /**
     * Check for animation registration
     * @param name animation name
     * @return boolean
     */
    public boolean isRegistered(String name) {
        return registeredAnimations.get(name) != null;
    }

    /**
     * Get all registered animations
     * @return map with registered animations
     */
    public Map<String, Animation> getRegisteredAnimations() {
        return registeredAnimations;
    }

    /**
     * Get registered animation
     * @param animation Animation name
     * @return Animation class instance
     */
    private Animation getRegisteredAnimation(String animation) {
        if (registeredAnimations.containsKey(animation)) {
            try {
                Animation animationClass = getRegisteredAnimations().get(animation);
                return animationClass.getClass().getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                DonateCase.instance.getLogger().warning(e.getLocalizedMessage());
            }
        }
        return null;
    }


}
