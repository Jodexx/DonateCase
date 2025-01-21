package com.jodexindustries.donatecase.api.data.action;

import com.jodexindustries.donatecase.api.platform.DCPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for registering case actions
 */
public interface ActionExecutor {

    /**
     * Called for executing custom action
     *
     * @param player   Player for executing
     * @param context  Executing context
     * @param cooldown Action cooldown
     */
    void execute(@Nullable DCPlayer player, @NotNull String context, int cooldown);
}
