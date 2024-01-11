package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.CaseData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the animation starts
 */
public class AnimationStartEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    CaseData caseData;
    Location location;
    String animation;
    CaseData.Item winItem;

    public AnimationStartEvent(@NotNull Player who, String animation, CaseData caseData, Location location, CaseData.Item winItem) {
        super(who);
        this.caseData = caseData;
        this.location = location;
        this.animation = animation;
        this.winItem = winItem;
    }

    /**
     * Get case location
     * @return case location
     */
    @NotNull
    public Location getLocation() {
        return location;
    }
    /**
     * Get case data
     * @return case data
     */
    @NotNull
    public CaseData getCaseData() {
        return caseData;
    }

    /**
     * Get case animation
     * @return case animation
     */
    @NotNull
    public String getAnimation() {
        return animation;
    }

    /**
     * Get the win item
     * @return win item
     */
    @NotNull
    public CaseData.Item getWinItem() {
        return winItem;
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
