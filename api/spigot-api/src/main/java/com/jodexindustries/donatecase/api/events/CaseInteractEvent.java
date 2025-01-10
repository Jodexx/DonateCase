package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
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
    private final Block block;
    private final CaseDataBukkit caseData;
    private final Action action;
    private final List<ActiveCase<Block, Player, CaseDataItem<CaseDataMaterialBukkit>>> activeCases;

    /**
     * Default constructor
     *
     * @param who      Player who interact
     * @param block    Block to interact
     * @param caseData Case data
     * @param action   Interact action
     */
    public CaseInteractEvent(@NotNull final Player who, @NotNull final Block block,
                             @NotNull final CaseDataBukkit caseData, @NotNull final Action action,
                             @Nullable final List<ActiveCase<Block, Player, CaseDataItem<CaseDataMaterialBukkit>>> activeCases) {
        super(who);
        this.block = block;
        this.caseData = caseData;
        this.cancel = false;
        this.action = action;
        this.activeCases = activeCases;
    }

    /**
     * Can be only LEFT_CLICK_BLOCK and RIGHT_CLICK_BLOCK
     *
     * @return click block action
     */
    @NotNull
    public Action getAction() {
        return action;
    }

    /**
     * Get clicked block
     *
     * @return block
     */
    @NotNull
    public Block getClickedBlock() {
        return block;
    }

    /**
     * Gets case data
     * @return case data
     * @since 2.0.2.5
     */
    public CaseDataBukkit getCaseData() {
        return caseData;
    }

    /**
     * Get case type
     *
     * @return case type
     * @deprecated Use instead {@link #getCaseData()}
     */
    @NotNull
    @Deprecated
    public String getCaseType() {
        return caseData.getCaseType();
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
     * Gets active cases
     * @return list of the active cases by this block
     * @since 2.0.2.5
     */
    @Nullable
    public List<ActiveCase<Block, Player, CaseDataItem<CaseDataMaterialBukkit>>> getActiveCases() {
        return activeCases;
    }

    /**
     * Is case locked
     * @since 2.0.2.5
     * @return true if case locked
     */
    public boolean isLocked() {
        return activeCases != null && activeCases.stream().anyMatch(ActiveCase::isLocked);
    }
}
