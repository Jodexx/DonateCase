package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.Animation;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AnimationRegisteredEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    String animationName;
    Class<? extends Animation> animationClass;
    boolean isDefault;
    String animationPluginName;

    public AnimationRegisteredEvent(String animationName, Class<? extends Animation> animationClass, String animationPluginName, boolean isDefault) {
        this.animationName = animationName;
        this.animationClass = animationClass;
        this.isDefault = isDefault;
        this.animationPluginName = animationPluginName;
    }

    public String getAnimationName() {
        return animationName;
    }

    public String getAnimationPluginName() {
        return animationPluginName;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public Class<? extends Animation> getAnimationClass() {
        return animationClass;
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
