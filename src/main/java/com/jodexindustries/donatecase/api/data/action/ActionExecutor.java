package com.jodexindustries.donatecase.api.data.action;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

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
    void execute(@NotNull OfflinePlayer player, @NotNull String context, int cooldown);
}
