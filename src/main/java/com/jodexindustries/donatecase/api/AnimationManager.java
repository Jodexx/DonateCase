package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.Animation;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.events.AnimationPreStartEvent;
import com.jodexindustries.donatecase.api.events.AnimationRegisteredEvent;
import com.jodexindustries.donatecase.api.events.AnimationStartEvent;
import com.jodexindustries.donatecase.api.events.AnimationUnregisteredEvent;
import com.jodexindustries.donatecase.tools.Pair;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;

/**
 * Animation control class, registration, playing
 */
public class AnimationManager {
    private static final Map<String, Pair<Animation, Addon>> registeredAnimations = new HashMap<>();
    private final Addon addon;

    /**
     * Default constructor
     * @param addon An addon that will manage animations
     */
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
            registeredAnimations.put(name, new Pair<>(animation, addon));
            String animationPluginName = addon.getName();
            boolean isDefault = false;
            AnimationRegisteredEvent animationRegisteredEvent = new AnimationRegisteredEvent(name, animation, animationPluginName, isDefault);
            Bukkit.getServer().getPluginManager().callEvent(animationRegisteredEvent);
        } else {
            Case.getInstance().getLogger().warning("Animation with name " + name + " already registered!");
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
            Case.getInstance().getLogger().warning("Animation with name " + name + " already unregistered!");
        }
    }

    /**
     * Unregister all animations
     */
    public void unregisterAnimations() {
        List<String> list = new ArrayList<>(getRegisteredAnimations().keySet());
        for (String s : list) {
            unregisterAnimation(s);
        }
    }

    /**
     * Start animation at a specific location
     * @param player The player who opened the case
     * @param location Location where to start the animation
     * @param caseType Case type
     */
    public void startAnimation(Player player, Location location, String caseType) {
        CaseData caseData = Case.getCase(caseType);
        if(caseData == null) return;
        caseData = caseData.clone();
        String animation = caseData.getAnimation();
        if (isRegistered(animation)) {
            if (CaseManager.getHologramManager() != null && caseData.getHologram().isEnabled()) {
                CaseManager.getHologramManager().removeHologram(location.getBlock());
            }

            Animation animationClass = getRegisteredAnimation(animation);
            if (animationClass != null) {
                CaseData.Item winItem = Case.getRandomItem(caseData);
                winItem.getMaterial().setDisplayName(PAPISupport.setPlaceholders(player, winItem.getMaterial().getDisplayName()));
                AnimationPreStartEvent preStartEvent = new AnimationPreStartEvent(player, animation, caseData, location, winItem);
                Bukkit.getPluginManager().callEvent(preStartEvent);

                ActiveCase activeCase = new ActiveCase(location, caseData.getCaseType());
                UUID uuid = UUID.randomUUID();
                Case.activeCases.put(uuid, activeCase);
                Case.activeCasesByLocation.put(location.getBlock().getLocation(), uuid);

                animationClass.start(player, Case.getCaseLocationByBlockLocation(location), uuid, caseData, preStartEvent.getWinItem());
                for (Player pl : Bukkit.getOnlinePlayers()) {
                    if (Case.playersGui.containsKey(pl.getUniqueId())) {
                        pl.closeInventory();
                    }
                }
                // AnimationStart event
                AnimationStartEvent startEvent = new AnimationStartEvent(player, animation, caseData, location, preStartEvent.getWinItem());
                Bukkit.getPluginManager().callEvent(startEvent);
            } else {
                Case.getInstance().getLogger().warning("Animation " + animation + " not found!");
            }
        } else {
            Tools.msg(player, "&cAn error occurred while opening the case!");
            Tools.msg(player, "&cContact the project administration!");
            Case.getInstance().getLogger().log(Level.WARNING, "Case animation " + animation + " does not exist!");
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
    public Map<String, Pair<Animation, Addon>> getRegisteredAnimations() {
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
                Animation animationClass = getRegisteredAnimations().get(animation).getFirst();
                return animationClass.getClass().getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                Case.getInstance().getLogger().warning(e.getLocalizedMessage());
            }
        }
        return null;
    }


}
