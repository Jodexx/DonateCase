package com.jodexindustries.donatecase.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player tries to open a case via the open menu
 */
public class PreOpenCaseEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    protected boolean cancel;
    String caseType;
    Block block;

    public PreOpenCaseEvent(@NotNull final Player who, @NotNull final String caseType, Block block) {
        super(who);
        this.caseType = caseType;
        this.block = block;
        cancel = false;
    }
    /**
     * Get case type (type from config)
     * @return case type
     */
    public String getCaseType() {
        return caseType;
    }

    /**
     * Get case block
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
}
