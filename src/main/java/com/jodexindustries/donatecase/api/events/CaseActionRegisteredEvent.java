package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.action.ActionExecutor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the case action is registered in DonateCase
 */
public class CaseActionRegisteredEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String caseActionName;
    private final ActionExecutor actionExecutor;
    private final boolean isDefault;
    private final String caseActionAddonName;

    /**
     * Default constructor
     *
     * @param caseActionName      Case action name
     * @param actionExecutor      Case action class
     * @param caseActionAddonName Case action addon name
     * @param isDefault           Is default?
     */
    public CaseActionRegisteredEvent(String caseActionName, ActionExecutor actionExecutor, String caseActionAddonName, boolean isDefault) {
        this.caseActionName = caseActionName;
        this.actionExecutor = actionExecutor;
        this.isDefault = isDefault;
        this.caseActionAddonName = caseActionAddonName;
    }

    /**
     * Get CaseAction name
     *
     * @return name
     */
    public String getCaseActionName() {
        return caseActionName;
    }

    /**
     * Get CaseAction addon name
     *
     * @return addon name
     */
    public String getCaseActionAddonName() {
        return caseActionAddonName;
    }

    /**
     * Get CaseAction class
     *
     * @return CaseAction
     */
    public ActionExecutor getCaseAction() {
        return actionExecutor;
    }

    /**
     * Get if this CaseAction is default
     *
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
     *
     * @return handlers list
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
