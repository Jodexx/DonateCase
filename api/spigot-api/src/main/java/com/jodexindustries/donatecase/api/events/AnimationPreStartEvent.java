package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
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
    private final CaseDataBukkit caseData;
    private final Block block;
    private CaseDataItem<CaseDataMaterialBukkit> winItem;

    /**
     * Default constructor
     *
     * @param who       Player who opened
     * @param caseData  Case data
     * @param block     Block where opened
     * @param winItem   Win item
     */
    public AnimationPreStartEvent(@NotNull final Player who,
                                  @NotNull final CaseDataBukkit caseData, @NotNull final Block block,
                                  @NotNull final CaseDataItem<CaseDataMaterialBukkit> winItem) {
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
    public CaseDataBukkit getCaseData() {
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
    public CaseDataItem<CaseDataMaterialBukkit> getWinItem() {
        return winItem;
    }

    /**
     * Set the prize before starting the animation (usually a random one is taken from the case configuration)
     *
     * @param winItem Win group data
     */
    public void setWinItem(@NotNull CaseDataItem<CaseDataMaterialBukkit> winItem) {
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
