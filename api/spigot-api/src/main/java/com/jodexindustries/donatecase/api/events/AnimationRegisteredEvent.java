package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.animation.JavaAnimationBukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the animation is registered in DonateCase
 */
public class AnimationRegisteredEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final CaseAnimation<JavaAnimationBukkit> caseAnimation;

    /**
     * Default constructor
     *
     * @param caseAnimation Case animation
     */
    public AnimationRegisteredEvent(CaseAnimation<JavaAnimationBukkit> caseAnimation) {
        this.caseAnimation = caseAnimation;
    }

    /**
     * Get case animation
     *
     * @return animation
     */
    public CaseAnimation<JavaAnimationBukkit> getCaseAnimation() {
        return caseAnimation;
    }

    /**
     * Check if animation registered from DonateCase
     * @return true, if default
     */
    public boolean isDefault() {
        return caseAnimation.getAddon().getName().equals("DonateCase");
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
