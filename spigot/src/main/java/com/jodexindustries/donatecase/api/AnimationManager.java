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
import org.bukkit.configuration.ConfigurationSection;
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
     * Gets Builder for creating animation
     * @param name Animation name to create
     * @return CaseAnimation Builder
     * @since 2.2.6.2
     */
    @NotNull
    public CaseAnimation.Builder builder(String name) {
        return new CaseAnimation.Builder(name, addon);
    }

    /**
     * Register animation
     * @see #builder(String)
     * @param caseAnimation Case animation object
     * @return true, if successful
     * @since 2.2.6.2
     */
    public boolean registerAnimation(CaseAnimation caseAnimation) {
        String name = caseAnimation.getName();
        if(!isRegistered(name)) {
            registeredAnimations.put(name, caseAnimation);
            AnimationRegisteredEvent animationRegisteredEvent = new AnimationRegisteredEvent(caseAnimation);
            Bukkit.getServer().getPluginManager().callEvent(animationRegisteredEvent);
            return true;
        } else {
            addon.getLogger().warning("Animation " + name + " already registered!");
        }
        return false;
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
    public boolean startAnimation(@NotNull Player player, @NotNull Location location, @NotNull CaseData caseData) {
        Block block = location.getBlock();

        if(Case.activeCasesByBlock.containsKey(block)) {
            addon.getLogger().log(Level.WARNING, "Player " + player.getName() + " trying to start animation while another animation is running!");
            return false;
        }

        if (caseData.getItems().isEmpty()) {
            addon.getLogger().log(Level.WARNING, "Player " + player.getName() + " trying to start animation without items in CaseData!");
            return false;
        }

        caseData = caseData.clone();
        caseData.setItems(Tools.sortItemsByIndex(caseData.getItems()));
        String animation = caseData.getAnimation();
        if (!isRegistered(animation)) {
            Tools.msg(player, "&cAn error occurred while opening the case!");
            Tools.msg(player, "&cContact the project administration!");
            addon.getLogger().log(Level.WARNING, "Case animation " + animation + " does not exist!");
            return false;
        }

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
                Case.activeCasesByBlock.put(block, uuid);

                if (animationClass != null) {
                    ConfigurationSection settings =
                            caseData.getAnimationSettings() != null ?
                                    caseData.getAnimationSettings() :
                                    Case.getConfig().getAnimations().getConfigurationSection(animation);

                    if(caseAnimation.isRequireSettings() && settings == null)
                        throw new IllegalArgumentException("Animation " + animation + " requires settings for starting!");

                    JavaAnimation javaAnimation = animationClass.getDeclaredConstructor().newInstance();

                    javaAnimation.init(player, caseLocation,
                            uuid, caseData, preStartEvent.getWinItem(), settings);
                    javaAnimation.start();

                } else {
                    throw new IllegalArgumentException("Animation executable class does not exist!");
                }

            } catch (Throwable t) {
                addon.getLogger().log(Level.WARNING, "Error with starting animation " + animation, t);
                Case.activeCasesByBlock.remove(block);
                return false;
            }
        }

        for (CaseGui gui : Case.playersGui.values()) {
            if (gui.getLocation().equals(block.getLocation())) {
                gui.getPlayer().closeInventory();
            }
        }

        Case.activeCases.put(uuid, activeCase);

        // AnimationStart event
        AnimationStartEvent startEvent = new AnimationStartEvent(player, animation, caseData, block, preStartEvent.getWinItem(), uuid);
        Bukkit.getPluginManager().callEvent(startEvent);
        return true;
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