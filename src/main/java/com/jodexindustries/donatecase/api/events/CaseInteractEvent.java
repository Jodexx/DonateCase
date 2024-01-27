package com.jodexindustries.donatecase.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the player interacts with the case block on the mouse's right button
 */
public class CaseInteractEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    protected boolean cancel;
    Block block;
    String caseType;
    public CaseInteractEvent(@NotNull final Player who, @NotNull final Block block, @NotNull final String caseType) {
        super(who);
        this.block = block;
        this.caseType = caseType;
        cancel = false;
    }
    @NotNull
    public Block getClickedBlock() {
        return block;
    }

    /**
     * Get case type
     * @return case type
     */
    @NotNull
    public String getCaseType() {
        return caseType;
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
