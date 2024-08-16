package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.CaseData;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the animation ends
 */
public class AnimationEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final CaseData caseData;
    private final Location location;
    private final String animation;
    private final CaseData.Item winItem;
    private final OfflinePlayer player;

    /**
     * Default constructor
     *
     * @param who       Player, who opened case
     * @param animation Case animation
     * @param caseData  Case data
     * @param location  Case location (or another, where animation was ended)
     * @param winItem   Player prize
     */
    public AnimationEndEvent(@NotNull OfflinePlayer who, String animation, CaseData caseData, Location location, CaseData.Item winItem) {
        this.player = who;
        this.caseData = caseData;
        this.location = location;
        this.animation = animation;
        this.winItem = winItem;
    }

    /**
     * Get case location
     *
     * @return case location
     */
    @NotNull
    public Location getLocation() {
        return location;
    }

    /**
     * Get case data
     *
     * @return case data
     */
    @NotNull
    public CaseData getCaseData() {
        return caseData;
    }

    /**
     * Get case animation
     *
     * @return case animation
     */
    @NotNull
    public String getAnimation() {
        return animation;
    }

    /**
     * Get the win item
     *
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

    /**
     * Get handlers
     *
     * @return handlers list
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Get who opened
     *
     * @return player
     */
    public OfflinePlayer getPlayer() {
        return player;
    }
}
