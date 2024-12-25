package com.jodexindustries.donatecase.tools;

import org.bukkit.OfflinePlayer;

/**
 * Interface for placeholder api interaction
 */
public interface PAPI {

    /**
     * Registers placeholder expansion
     */
    void register();
    /**
     * Unregisters placeholder expansion
     */
    void unregister();

    /**
     * Sets placeholders for the string
     * @param player Player for parsing
     * @param text String for parsing
     * @return string with placeholders
     */
    String setPlaceholders(OfflinePlayer player, String text);
}
