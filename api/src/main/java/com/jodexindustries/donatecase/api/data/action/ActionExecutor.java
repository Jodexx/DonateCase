package com.jodexindustries.donatecase.api.data.action;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    void execute(@Nullable P player, @NotNull String context, int cooldown);
}
