package com.jodexindustries.donatecase.api.addon;

import java.util.logging.Logger;

/**
 * Represent an Addon
 */
public interface Addon {

    /**
     * Returns the name of the addon
     * @return Addon name
     */
    String getName();

    /**
     * An addon revision identifier
     * @return Addon version
     */
    String getVersion();

    /**
     * Returns a value indicating whether or not this addon is currently
     * enabled
     *
     * @return true if this addon is enabled, otherwise false
     */
    boolean isEnabled();

    /**
     * Returns the addon logger associated with this server's logger. The
     * returned logger automatically tags all log messages with the addon's
     * name.
     *
     * @return Logger associated with this addon
     */
    Logger getLogger();
}