package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.*;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.events.AnimationPreStartEvent;
import com.jodexindustries.donatecase.api.events.AnimationRegisteredEvent;
import com.jodexindustries.donatecase.api.events.AnimationStartEvent;
import com.jodexindustries.donatecase.api.events.AnimationUnregisteredEvent;
import com.jodexindustries.donatecase.gui.CaseGui;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Animation control class, registration, playing
 */
public class AnimationManager {
    /**
     * Map of registered animations
     */
    public static final Map<String, CaseAnimation> registeredAnimations = new HashMap<>();

    private final Addon addon;

    /**
     * Default constructor
     *
     * @param addon An addon that will manage animations
     */
    public AnimationManager(@NotNull Addon addon) {
        this.addon = addon;
    }

    /**
     * Register custom animation
     *
     * @param name        Animation name
     * @param animation   Animation class
     * @param description Animation description
     */
    public void registerAnimation(@NotNull String name, @NotNull Class<? extends JavaAnimation> animation, String description) {
        if (!isRegistered(name)) {
            CaseAnimation caseAnimation = new CaseAnimation(animation, addon, name, description);
            registeredAnimations.put(name, caseAnimation);
            AnimationRegisteredEvent animationRegisteredEvent = new AnimationRegisteredEvent(caseAnimation);
            Bukkit.getServer().getPluginManager().callEvent(animationRegisteredEvent);
        } else {
            addon.getLogger().warning("Animation with name " + name + " already registered!");
        }
    }

    /**
     * Register custom animation
     *
     * @param name      Animation name
     * @param animation Animation class
     * @deprecated Use {@link #registerAnimation(String, Class, String)} instead
     */
    @Deprecated
    public void registerAnimation(String name, Class<? extends JavaAnimation> animation) {
        registerAnimation(name, animation, "Nothing to say");
    }

    /**
     * Register custom animation
     *
     * @param name      Animation name
     * @param animation Animation object
     */
    @Deprecated
    public void registerAnimation(String name, Animation animation) {
        if (!isRegistered(name)) {
            CaseAnimation caseAnimation = new CaseAnimation(animation, addon, name, "Old animation without description");
            registeredAnimations.put(name, caseAnimation);
            AnimationRegisteredEvent animationRegisteredEvent = new AnimationRegisteredEvent(caseAnimation);
            Bukkit.getServer().getPluginManager().callEvent(animationRegisteredEvent);
        } else {
            addon.getLogger().warning("Animation with name " + name + " already registered!");
        }
    }


    /**
     * Unregister custom animation
     *
     * @param name Animation name
     */
    public void unregisterAnimation(String name) {
        if (isRegistered(name)) {
            registeredAnimations.remove(name);
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
        List<String> list = new ArrayList<>(registeredAnimations.keySet());
        list.forEach(this::unregisterAnimation);
    }

    /**
     * Start animation at a specific location
     *
     * @param player   The player who opened the case
     * @param location Location where to start the animation
     * @param caseData Case data
     */
    public void startAnimation(@NotNull Player player, @NotNull Location location, @NotNull CaseData caseData) {
        if (caseData.getItems().isEmpty()) {
            addon.getLogger().log(Level.WARNING, "Player " + player.getName() + " trying to start animation without items in CaseData!");
            return;
        }

        caseData = caseData.clone();
        caseData.setItems(Tools.sortItemsByIndex(caseData.getItems()));
        String animation = caseData.getAnimation();
        if (!isRegistered(animation)) {
            Tools.msg(player, "&cAn error occurred while opening the case!");
            Tools.msg(player, "&cContact the project administration!");
            addon.getLogger().log(Level.WARNING, "Case animation " + animation + " does not exist!");
            return;
        }

        Block block = location.getBlock();

        CaseData.Item winItem = caseData.getRandomItem();
        winItem.getMaterial().setDisplayName(Case.getInstance().papi.setPlaceholders(player, winItem.getMaterial().getDisplayName()));
        AnimationPreStartEvent preStartEvent = new AnimationPreStartEvent(player, caseData, block, winItem);
        Bukkit.getPluginManager().callEvent(preStartEvent);

        ActiveCase activeCase = new ActiveCase(block, caseData.getCaseType());
        UUID uuid = UUID.randomUUID();

        if (CaseManager.getHologramManager() != null && caseData.getHologram().isEnabled()) {
            CaseManager.getHologramManager().removeHologram(block);
        }

        CaseAnimation caseAnimation = getRegisteredAnimation(animation);

        if (caseAnimation != null) {
            Location caseLocation = location;

            Location tempLocation = Case.getCaseLocationByBlockLocation(block.getLocation());
            if (tempLocation != null) caseLocation = tempLocation;

            Class<? extends JavaAnimation> animationClass = caseAnimation.getAnimation();

            try {

                if (animationClass != null) {
                    JavaAnimation javaAnimation = animationClass.getDeclaredConstructor().newInstance();

                    javaAnimation.init(player, caseLocation,
                            uuid, caseData, preStartEvent.getWinItem());
                    javaAnimation.start();

                } else {
                    Animation oldAnimation = caseAnimation.getOldAnimation();
                    if (oldAnimation == null) return;

                    oldAnimation.start(player, caseLocation, uuid,
                            caseData, preStartEvent.getWinItem());
                }

            } catch (Throwable t) {
                addon.getLogger().log(Level.WARNING, "Error with starting animation " + animation, t);
                return;
            }
        }

        for (CaseGui gui : Case.playersGui.values()) {
            if (gui.getLocation().equals(block.getLocation())) {
                gui.getPlayer().closeInventory();
            }
        }

        Case.activeCases.put(uuid, activeCase);
        Case.activeCasesByBlock.put(block, uuid);

        // AnimationStart event
        AnimationStartEvent startEvent = new AnimationStartEvent(player, animation, caseData, block, preStartEvent.getWinItem());
        Bukkit.getPluginManager().callEvent(startEvent);
    }

    /**
     * Check for animation registration
     *
     * @param name animation name
     * @return boolean
     */
    public static boolean isRegistered(String name) {
        return registeredAnimations.containsKey(name);
    }

    /**
     * Get registered animation
     *
     * @param animation Animation name
     * @return Animation class instance
     */
    @Nullable
    public static CaseAnimation getRegisteredAnimation(String animation) {
        return registeredAnimations.get(animation);
    }
}