package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.CaseData;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the player successfully opens the case (from gui) and player has keys for opening.
 * <br/>
 * At this time, case gui will be already closed.
 * <br/>
 * Can be cancelled. If you cancel this event, animation will not be started and keys will not be removed.
 * <p> Very similar with {@link AnimationPreStartEvent}</p>
 */
public class OpenCaseEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    protected boolean cancel;
    private final CaseData caseData;
    private final Block block;

    /**
     * Default constructor
     *
     * @param who      Player who opened
     * @param caseData Case data
     * @param block    Case block
     */
    public OpenCaseEvent(@NotNull final Player who, @NotNull final CaseData caseData, final Block block) {
        super(who);
        this.caseData = caseData;
        this.block = block;
        cancel = false;
    }

    /**
     * Get case type
     *
     * @return case type
     */
    @NotNull
    public String getCaseType() {
        return caseData.getCaseType();
    }

    /**
     * Get case data
     * @return case data
     * @since 2.2.5.8
     */
    @NotNull
    public CaseData getCaseData() {
        return caseData;
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
