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
    protected boolean cancel;
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
        cancel = false;
        ignoreKeys = false;
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
     * @since 2.2.5.8
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
     * @since 2.2.6.6
     */
    public boolean isIgnoreKeys() {
        return ignoreKeys;
    }

    /**
     * @since 2.2.6.6
     */
    public void setIgnoreKeys(boolean ignoreKeys) {
        this.ignoreKeys = ignoreKeys;
    }
}
