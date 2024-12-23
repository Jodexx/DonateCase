package com.jodexindustries.donatecase.api.data.action;

import com.jodexindustries.donatecase.api.addon.Addon;

/**
 * Class for custom actions storage
 * @param <P> the type of Player
 */
public class CaseAction<P> {
    private final ActionExecutor<P> executor;
    private final Addon addon;
    private final String name;
    private final String description;

    /**
     * Default constructor
     *
     * @param executor    Action executor
     * @param addon       Action addon
     * @param name        Action name
     * @param description Action description
     */
    public CaseAction(ActionExecutor<P> executor, Addon addon, String name, String description) {
        this.executor = executor;
        this.addon = addon;
        this.name = name;
        this.description = description;

    }

    /**
     * Gets addon which registered this action
     *
     * @return addon action
     */
    public Addon getAddon() {
        return addon;
    }

    /**
     * Gets action description
     *
     * @return action description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets action name
     *
     * @return action name
     */
    public String getName() {
        return name;
    }

    public ActionExecutor<P> getExecutor() {
        return executor;
    }

    @Override
    public String toString() {
        return name + " (" + description + ")";
    }
}
