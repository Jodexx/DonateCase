package com.jodexindustries.donatecase.api.addon.internal;

import com.jodexindustries.donatecase.api.CaseManager;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface InternalAddonBukkit extends InternalAddon {
    @NotNull
    Plugin getDonateCaseBukkit();

    /**
     * Gets the class that represent all DonateCase managers
     *
     * @return The global manager
     */
    @NotNull
    CaseManager getCaseAPI();
}
