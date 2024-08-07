package com.jodexindustries.donatecase.api.data.action;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for registering case actions
 */
public interface ActionExecutor {
    void execute(@NotNull OfflinePlayer player, @NotNull String context, int cooldown);
}
