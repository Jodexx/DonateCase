package com.jodexindustries.donatecase.api.tools;

import com.jodexindustries.donatecase.api.platform.DCPlayer;
import org.jetbrains.annotations.NotNull;

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
    String setPlaceholders(@NotNull Object player, String text);

    String setPlaceholders(@NotNull DCPlayer player, String text);
}
