package com.jodexindustries.donatecase.api.data;

import org.bukkit.OfflinePlayer;

/**
 * Interface for registering case actions
 */
public interface CaseAction {
    void execute(OfflinePlayer player, String context, int cooldown);
}
