package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import lombok.Getter;
import lombok.Setter;
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
    private final CaseData caseData;
    /**
     * -- GETTER --
     *  Get case block
     *
     * @return Case block
     */
    @Getter
    private final Block block;
    /**
     * -- GETTER --
     *  Determines whether the player's keys should be ignored.
     *
     *
     * -- SETTER --
     *  Sets whether the player's keys should be ignored.
     *
     @return {@code true} if the keys should be ignored, {@code false} otherwise.
      * @param ignoreKeys {@code true} to ignore the player's keys, {@code false} otherwise.
     */
    @Setter
    @Getter
    private boolean ignoreKeys;

    /**
     * Default constructor
     *
     * @param who      Player who opened
     * @param caseData Case data
     * @param block    Case block
     */
    public PreOpenCaseEvent(@NotNull final Player who, @NotNull final CaseData caseData, Block block) {
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
    public CaseData getCaseData() {
        return caseData;
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
