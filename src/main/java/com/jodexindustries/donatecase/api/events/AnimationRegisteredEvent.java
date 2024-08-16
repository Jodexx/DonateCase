package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.IAnimation;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the animation is registered in DonateCase
 */
public class AnimationRegisteredEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String animationName;
    private final Class<? extends IAnimation> animationClass;
    private final boolean isDefault;
    private final String animationAddonName;

    /**
     * Default constructor
     *
     * @param animationName      Animation name
     * @param animationClass     Animation class
     * @param animationAddonName Animation addon name
     * @param isDefault          Is default?
     */
    public AnimationRegisteredEvent(String animationName, Class<? extends IAnimation> animationClass, String animationAddonName, boolean isDefault) {
        this.animationName = animationName;
        this.animationClass = animationClass;
        this.isDefault = isDefault;
        this.animationAddonName = animationAddonName;
    }

    /**
     * Get animation name
     *
     * @return animation name
     */
    public String getAnimationName() {
        return animationName;
    }

    /**
     * Get animation addon name
     *
     * @return animation addon name
     */
    public String getAnimationAddonName() {
        return animationAddonName;
    }

    /**
     * Get if this animation is default
     *
     * @return boolean
     */
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * Get animation class
     *
     * @return animation class
     */
    public Class<? extends IAnimation> getAnimation() {
        return animationClass;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get handlers
     *
     * @return handlers list
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
