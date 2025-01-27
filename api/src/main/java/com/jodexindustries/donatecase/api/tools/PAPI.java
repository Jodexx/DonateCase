package com.jodexindustries.donatecase.api.tools;

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
    String setPlaceholders(Object player, String text);
}
