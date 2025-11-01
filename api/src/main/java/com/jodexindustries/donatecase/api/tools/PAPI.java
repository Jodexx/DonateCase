package com.jodexindustries.donatecase.api.tools;

import org.jetbrains.annotations.Nullable;

import java.util.List;

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
    String setPlaceholders(@Nullable Object player, String text);

    List<String> setPlaceholders(@Nullable Object player, List<String> text);
}
