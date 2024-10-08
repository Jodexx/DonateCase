package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.CaseData;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called before the animation starts
 */
public class AnimationPreStartEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final CaseData caseData;
    private final Block block;
    private CaseData.Item winItem;

    /**
     * Default constructor
     *
     * @param who       Player who opened
     * @param caseData  Case data
     * @param block     Block where opened
     * @param winItem   Win item
     */
    public AnimationPreStartEvent(@NotNull final Player who,
                                  @NotNull final CaseData caseData, @NotNull final Block block,
                                  @NotNull final CaseData.Item winItem) {
        super(who);
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
    @NotNull
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

    /**
     * Set the prize before starting the animation (usually a random one is taken from the case configuration)
     *
     * @param winItem Win group data
     */
    public void setWinItem(@NotNull CaseData.Item winItem) {
        this.winItem = winItem;
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
