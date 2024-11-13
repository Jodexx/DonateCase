package com.jodexindustries.donatecase.api.data.action;

import org.jetbrains.annotations.NotNull;

/**
 * Interface for registering case actions
 */
public interface ActionExecutor<P> {

    /**
     * Called for executing custom action
     *
     * @param player   Player for executing
     * @param context  Executing context
     * @param cooldown Action cooldown
     */
    void execute(@NotNull P player, @NotNull String context, int cooldown);
}
