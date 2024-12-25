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

import java.util.UUID;

/**
 * Called when the animation starts
 */
public class AnimationStartEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final CaseDataBukkit caseData;
    private final Block block;
    private final String animation;
    private final CaseDataItem<CaseDataMaterialBukkit> winItem;
    private final UUID uuid;

    /**
     * Default constructor
     *
     * @param who       Player who opened case
     * @param animation Animation name
     * @param caseData  Case data
     * @param block     Block where opened
     * @param winItem   Win item
     * @param uuid Animation UUID
     */
    public AnimationStartEvent(@NotNull Player who, @NotNull String animation, @NotNull CaseDataBukkit caseData,
                               @NotNull Block block, @NotNull CaseDataItem<CaseDataMaterialBukkit> winItem, @NotNull UUID uuid) {
        super(who);
        this.caseData = caseData;
        this.block = block;
        this.animation = animation;
        this.winItem = winItem;
        this.uuid = uuid;
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
        return animation;
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
     * Returns the unique identifier of the animation.
     *
     * @return the id of the animation
     */
    public UUID getUniqueId() {
        return uuid;
    }

}
