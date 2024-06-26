package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.Animation;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the animation is registered in DonateCase
 */
public class AnimationRegisteredEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    String animationName;
    Animation animationClass;
    boolean isDefault;
    String animationPluginName;

    public AnimationRegisteredEvent(String animationName, Animation animationClass, String animationPluginName, boolean isDefault) {
        this.animationName = animationName;
        this.animationClass = animationClass;
        this.isDefault = isDefault;
        this.animationPluginName = animationPluginName;
    }

    /**
     * Get animation name
     * @return animation name
     */
    public String getAnimationName() {
        return animationName;
    }

    /**
     * Get animation plugin name
     * @return animation plugin name
     */
    public String getAnimationPluginName() {
        return animationPluginName;
    }

    /**
     * Get if this animation is default
     * @return boolean
     */
    public boolean isDefault() {
        return isDefault;
    }

    /** Get animation class
     * @return animation class
     */
    public Animation getAnimationClass() {
        return animationClass;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get handlers
     * @return handlers list
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
