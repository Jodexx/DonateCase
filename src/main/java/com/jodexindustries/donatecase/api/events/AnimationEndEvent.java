package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.CaseData;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the animation ends
 */
public class AnimationEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final CaseData caseData;
    private final Block block;
    private final CaseData.Item winItem;
    private final OfflinePlayer player;

    /**
     * Default constructor
     *
     * @param who       Player, who opened case
     * @param animation Case animation
     * @param caseData  Case data
     * @param block     Case block (or another, where animation was ended)
     * @param winItem   Player prize
     */
    public AnimationEndEvent(@NotNull OfflinePlayer who, CaseData caseData, Block block, CaseData.Item winItem) {
        this.player = who;
        this.caseData = caseData;
        this.block = block;
        this.winItem = winItem;
    }

    /**
     * Get case location
     *
     * @return case location
     */
    @NotNull
    public Location getLocation() {
        return block.getLocation();
    }

    /**
     * Get case block
     *
     * @return case block
     * @since 2.2.5.8
     */
    public Block getBlock() {
        return block;
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
        return caseData.getAnimation();
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
