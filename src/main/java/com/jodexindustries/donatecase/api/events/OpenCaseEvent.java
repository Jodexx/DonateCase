package com.jodexindustries.donatecase.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the player successfully opens the case (from gui).
 * <br/>
 * At this time, case gui will be already closed, and case key removed from player
 * <br/>
 * Can be cancelled. If you cancel this event, animation will not be started.
 * <p> Very similar with {@link AnimationPreStartEvent}</p>
 */
public class OpenCaseEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    protected boolean cancel;
    private final String caseType;
    private final Block block;

    /**
     * Default constructor
     *
     * @param who      Player who opened
     * @param caseType Case type
     * @param block    Case block
     */
    public OpenCaseEvent(@NotNull final Player who, @NotNull final String caseType, final Block block) {
        super(who);
        this.caseType = caseType;
        this.block = block;
        cancel = false;
    }

    /**
     * Get case type
     *
     * @return case type
     */
    public String getCaseType() {
        return caseType;
    }

    /**
     * Get case block
     *
     * @return case block
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
}
