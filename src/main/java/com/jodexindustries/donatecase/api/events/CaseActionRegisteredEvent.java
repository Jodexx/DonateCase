package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.CaseAction;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CaseActionRegisteredEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String caseActionName;
    private final CaseAction caseAction;
    private final boolean isDefault;
    private final String caseActionAddonName;

    public CaseActionRegisteredEvent(String caseActionName, CaseAction caseAction, String caseActionAddonName, boolean isDefault) {
        this.caseActionName = caseActionName;
        this.caseAction = caseAction;
        this.isDefault = isDefault;
        this.caseActionAddonName = caseActionAddonName;
    }

    /**
     * Get CaseAction name
     * @return name
     */
    public String getCaseActionName() {
        return caseActionName;
    }

    /**
     * Get CaseAction addon name
     * @return addon name
     */
    public String getCaseActionAddonName() {
        return caseActionAddonName;
    }

    /**
     * Get CaseAction class
     * @return CaseAction
     */
    public CaseAction getCaseAction() {
        return caseAction;
    }

    /**
     * Get if this CaseAction is default
     * @return boolean
     */
    public boolean isDefault() {
        return isDefault;
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
