package com.jodexindustries.donatecase.api.addon;

/**
 * Represent an Addon
 */
public interface Addon {

    /**
     * Returns the name of the addon
     */
    String getName();

    /**
     * An addon revision identifier
     */
    String getVersion();

    /**
     * Returns a value indicating whether or not this addon is currently
     * enabled
     *
     * @return true if this addon is enabled, otherwise false
     */
    boolean isEnabled();
}