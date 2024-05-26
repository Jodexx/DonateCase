package com.jodexindustries.donatecase.api.addon.external;

import com.jodexindustries.donatecase.api.addon.Addon;
import org.bukkit.plugin.Plugin;

public interface ExternalAddon extends Addon {
    Plugin getPlugin();
}
