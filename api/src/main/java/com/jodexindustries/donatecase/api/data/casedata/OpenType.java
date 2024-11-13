package com.jodexindustries.donatecase.api.data.casedata;

import org.jetbrains.annotations.NotNull;

/**
 * Type of case opening (animation starting)
 */
public enum OpenType {
    /**
     * Case will be opened from GUI
     */
    GUI,
    /**
     * Case will be opened from BLOCK click
     */
    BLOCK;

    /**
     * Get open type
     *
     * @param type string
     * @return open type, if null, return GUI
     */
    @NotNull
    public static OpenType getOpenType(@NotNull String type) {
        try {
            return valueOf(type.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }
        return GUI;
    }
}
