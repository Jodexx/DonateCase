package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.*;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;

/**
 * Animation control class, registration, playing
 */
public class AnimationManager {
    private static final Map<String, Pair<Class<? extends JavaAnimation>, Addon>> registeredAnimations = new HashMap<>();
    private static final Map<String, Pair<Animation, Addon>> oldAnimations = new HashMap<>();
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
    public void registerAnimation(String name, Class<? extends JavaAnimation> animation) {
        if(!isRegistered(name)) {
            registeredAnimations.put(name, new Pair<>(animation, addon));
            String animationPluginName = addon.getName();
            boolean isDefault = animationPluginName.equalsIgnoreCase("DonateCase");
            AnimationRegisteredEvent animationRegisteredEvent = new AnimationRegisteredEvent(name, animation, animationPluginName, isDefault);
            Bukkit.getServer().getPluginManager().callEvent(animationRegisteredEvent);
        } else {
            addon.getLogger().warning("Animation with name " + name + " already registered!");
        }
    }

    /**
     * Register custom animation
     * @param name Animation name
     * @param animation Animation object
     */
    @Deprecated
    public void registerAnimation(String name, Animation animation) {
        if(!isRegistered(name, true)) {
            oldAnimations.put(name, new Pair<>(animation, addon));
            String animationPluginName = addon.getName();
            boolean isDefault = animationPluginName.equalsIgnoreCase("DonateCase");
            AnimationRegisteredEvent animationRegisteredEvent = new AnimationRegisteredEvent(name, null,
                    animationPluginName, isDefault);
            Bukkit.getServer().getPluginManager().callEvent(animationRegisteredEvent);
        } else {
            addon.getLogger().warning("Animation with name " + name + " already registered!");
        }
    }
    

    /**
     * Unregister custom animation
     * @param name Animation name
     */
    public void unregisterAnimation(String name) {
        if(isRegistered(name)) {
            registeredAnimations.remove(name);
            AnimationUnregisteredEvent animationUnRegisteredEvent = new AnimationUnregisteredEvent(name);
            Bukkit.getServer().getPluginManager().callEvent(animationUnRegisteredEvent);
        } else {
            addon.getLogger().warning("Animation with name " + name + " already unregistered!");
        }
    }

    /**
     * Unregister old custom animation
     * @param name Animation name
     * @param old is old animation
     */
    public void unregisterAnimation(String name, boolean old) {
        if(isRegistered(name, old)) {
            oldAnimations.remove(name);
            AnimationUnregisteredEvent animationUnRegisteredEvent = new AnimationUnregisteredEvent(name);
            Bukkit.getServer().getPluginManager().callEvent(animationUnRegisteredEvent);
        } else {
            addon.getLogger().warning("Animation with name " + name + " already unregistered!");
        }
    }

    /**
     * Unregister all animations
     */
    public void unregisterAnimations() {
        List<String> list = new ArrayList<>(getRegisteredAnimations().keySet());
        list.forEach(this::unregisterAnimation);

        List<String> old = new ArrayList<>(oldAnimations.keySet());
        old.forEach(s -> unregisterAnimation(s, true));
    }

    /**
     * Start animation at a specific location
     * @param player The player who opened the case
     * @param location Location where to start the animation
     * @param caseType Case type
     */
    public void startAnimation(@NotNull Player player, @NotNull Location location, @NotNull String caseType) {
        CaseData caseData = Case.getCase(caseType);
        if (caseData == null) return;
        caseData = caseData.clone();
        caseData.setItems(Tools.sortItemsByIndex(caseData.getItems()));
        String animation = caseData.getAnimation();
        if (!isRegistered(animation) && !isRegistered(animation, true)) {
            Tools.msg(player, "&cAn error occurred while opening the case!");
            Tools.msg(player, "&cContact the project administration!");
            addon.getLogger().log(Level.WARNING, "Case animation " + animation + " does not exist!");
            return;
        }

        CaseData.Item winItem = caseData.getRandomItem();
        winItem.getMaterial().setDisplayName(PAPISupport.setPlaceholders(player, winItem.getMaterial().getDisplayName()));
        AnimationPreStartEvent preStartEvent = new AnimationPreStartEvent(player, animation, caseData, location, winItem);
        Bukkit.getPluginManager().callEvent(preStartEvent);

        ActiveCase activeCase = new ActiveCase(location, caseData.getCaseType());
        UUID uuid = UUID.randomUUID();

        Object javaAnimation = null;
        if (isRegistered(animation, true)) {
            javaAnimation = getRegisteredOldAnimation(animation);
        } else {
            Class<? extends JavaAnimation> animationClass = getRegisteredAnimation(animation);
            if (animationClass != null) {
                try {
                    javaAnimation = animationClass.getDeclaredConstructor().newInstance();
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                         IllegalAccessException ignored) {
                }
            }
        }

        if (CaseManager.getHologramManager() != null && caseData.getHologram().isEnabled()) {
            CaseManager.getHologramManager().removeHologram(location.getBlock());
        }

        Case.activeCases.put(uuid, activeCase);
        Case.activeCasesByLocation.put(location, uuid);

        Location caseLocation = Case.getCaseLocationByBlockLocation(location);
        if (javaAnimation instanceof JavaAnimation) {
            JavaAnimation anim = (JavaAnimation) javaAnimation;
            anim.init(player, caseLocation,
                    uuid, caseData, preStartEvent.getWinItem());
            anim.start();
        } else {
            Animation anim = (Animation) javaAnimation;
            if (anim != null) {
                anim.start(player, caseLocation, uuid, caseData, preStartEvent.getWinItem());
            }
        }
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (Case.playersGui.containsKey(pl.getUniqueId())) {
                pl.closeInventory();
            }
        }
        // AnimationStart event
        AnimationStartEvent startEvent = new AnimationStartEvent(player, animation, caseData, location, preStartEvent.getWinItem());
        Bukkit.getPluginManager().callEvent(startEvent);
    }

    /**
     * Check for animation registration
     * @param name animation name
     * @return boolean
     */
    public static boolean isRegistered(String name) {
        return registeredAnimations.containsKey(name);
    }

    public static boolean isRegistered(String name, boolean old) {
        if(old) return oldAnimations.containsKey(name);
        return isRegistered(name);
    }

    /**
     * Get all registered animations
     * @return map with registered animations
     */
    @NotNull
    public static Map<String, Pair<Class<? extends JavaAnimation>, Addon>> getRegisteredAnimations() {
        return registeredAnimations;
    }

    /**
     * Get all old registered animations
     * @return map with old animations
     */
    @Deprecated
    public static Map<String, Pair<Animation, Addon>> getOldAnimations() {
        return oldAnimations;
    }

    /**
     * Get registered animation
     *
     * @param animation Animation name
     * @return Animation class instance
     */
    @Nullable
    public static Class<? extends JavaAnimation> getRegisteredAnimation(String animation) {
        if (isRegistered(animation)) {
            return getRegisteredAnimations().get(animation).getFirst();
        }
        return null;
    }

    /**
     * Get old registered animations
     * @param animation Animation name
     * @return Animation object
     */
    @Nullable
    @Deprecated
    public static Animation getRegisteredOldAnimation(String animation) {
        if (isRegistered(animation, true)) {
            return oldAnimations.get(animation).getFirst();
        }
        return null;
    }

}
