package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Called when the player interacts with the case block on the mouse's right button
 */
public class CaseInteractEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    /**
     * true if you wish to cancel this event
     */
    protected boolean cancel;
    @Getter private final Block block;
    @Getter private final CaseData caseData;
    @Getter private final Action action;
    @Getter private final List<ActiveCase> activeCases;

    /**
     * Default constructor
     *
     * @param who      Player who interact
     * @param block    Block to interact
     * @param caseData Case data
     * @param action   Interact action
     */
    public CaseInteractEvent(@NotNull final Player who, @NotNull final Block block,
                             @NotNull final CaseData caseData, @NotNull final Action action,
                             @Nullable final List<ActiveCase> activeCases) {
        super(who);
        this.block = block;
        this.caseData = caseData;
        this.cancel = false;
        this.action = action;
        this.activeCases = activeCases;
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
     * Is case locked
     * @return true if case locked
     */
    public boolean isLocked() {
        return activeCases != null && activeCases.stream().anyMatch(ActiveCase::isLocked);
    }
}
