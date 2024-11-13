package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.action.CaseAction;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the case action is registered in DonateCase
 */
public class CaseActionRegisteredEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final CaseAction<OfflinePlayer> caseAction;

    /**
     * Default constructor
     *
     * @param caseAction Case action
     */
    public CaseActionRegisteredEvent(CaseAction<OfflinePlayer> caseAction) {
        this.caseAction = caseAction;
    }

    /**
     * Get case action
     *
     * @return case action
     * @since 2.2.5.8
     */
    public CaseAction<OfflinePlayer> getCaseAction() {
        return caseAction;
    }

    /**
     * Get if this CaseAction is default
     *
     * @return boolean
     */
    public boolean isDefault() {
        return caseAction.getName().equals("DonateCase");
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
}
