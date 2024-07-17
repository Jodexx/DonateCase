package com.jodexindustries.donatecase.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the case action is unregistered in DonateCase
 */
public class CaseActionUnregisteredEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String caseActionName;

    public CaseActionUnregisteredEvent(String caseActionName) {
        this.caseActionName = caseActionName;
    }

    /**
     * Get CaseAction name
     * @return name
     */
    public String getCaseActionName() {
        return caseActionName;
    }


    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get handlers
     * @return handlers list
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
