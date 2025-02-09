package com.jodexindustries.donatecase.api.addon;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Logger;

/**
 * Represent an Addon
 */
public interface Addon {

    /**
     * Returns the name of the addon
     *
     * @return Addon name
     */
    String getName();

    /**
     * An addon revision identifier
     *
     * @return Addon version
     */
    String getVersion();

    /**
     * Returns the addon logger associated with this server's logger. The
     * returned logger automatically tags all log messages with the addon's
     * name.
     *
     * @return Logger associated with this addon
     */
    Logger getLogger();

    /**
     * Returns the folder that the addon data's files are located in. The
     * folder may not yet exist.
     *
     * @return The folder
     */
    @NotNull
    File getDataFolder();
}