package com.jodexindustries.donatecase.api.data;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for registering case actions
 */
public interface CaseAction {
    void execute(@NotNull OfflinePlayer player, @NotNull String context, int cooldown);
}
