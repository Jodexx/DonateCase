package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player tries to open a case via the open menu
 * <br>
 * Key checks has not started yet
 */
public class PreOpenCaseEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel;
    private final CaseDataBukkit caseData;
    private final Block block;
    private boolean ignoreKeys;

    /**
     * Default constructor
     *
     * @param who      Player who opened
     * @param caseData Case data
     * @param block    Case block
     */
    public PreOpenCaseEvent(@NotNull final Player who, @NotNull final CaseDataBukkit caseData, Block block) {
        super(who);
        this.caseData = caseData;
        this.block = block;
    }

    /**
     * Get case type (type from config)
     *
     * @return case type
     */
    @NotNull
    public String getCaseType() {
        return caseData.getCaseType();
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
     * Get case block
     *
     * @return Case block
     */
    public Block getBlock() {
        return block;
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

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Determines whether the player's keys should be ignored.
     *
     * @return {@code true} if the keys should be ignored, {@code false} otherwise.
     */
    public boolean isIgnoreKeys() {
        return ignoreKeys;
    }

    /**
     * Sets whether the player's keys should be ignored.
     *
     * @param ignoreKeys {@code true} to ignore the player's keys, {@code false} otherwise.
     */
    public void setIgnoreKeys(boolean ignoreKeys) {
        this.ignoreKeys = ignoreKeys;
    }

}
