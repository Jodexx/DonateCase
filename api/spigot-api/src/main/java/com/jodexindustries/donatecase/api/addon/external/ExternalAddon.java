package com.jodexindustries.donatecase.api.addon.external;

import com.jodexindustries.donatecase.api.addon.Addon;
import org.bukkit.plugin.Plugin;

/**
 * Represents an external addon (Bukkit plugin) that extends the {@link Addon} interface.
 */
public interface ExternalAddon extends Addon {

    /**
     * Returns the plugin instance associated with this addon.
     *
     * @return the plugin instance
     */
    Plugin getPlugin();
}
