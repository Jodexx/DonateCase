package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.CaseData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called before the animation starts
 */
public class AnimationPreStartEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    CaseData caseData;
    Location location;
    String animation;
    CaseData.Item winItem;

    public AnimationPreStartEvent(@NotNull Player who, String animation, CaseData c, Location location, CaseData.Item winItem) {
        super(who);
        this.caseData = c;
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

    /**
     * Set the prize before starting the animation (usually a random one is taken from the case configuration)
     * @param winItem Win group data
     */
    public void setWinItem(CaseData.Item winItem) {
        this.winItem = winItem;
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
