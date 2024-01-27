package com.jodexindustries.donatecase.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the animation is registered in DonateCase
 */
public class AnimationUnregisteredEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    String animationName;

    public AnimationUnregisteredEvent(String animationName) {
        this.animationName = animationName;
    }

    /**
     * Get animation name
     * @return animation name
     */
    public String getAnimationName() {
        return animationName;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
