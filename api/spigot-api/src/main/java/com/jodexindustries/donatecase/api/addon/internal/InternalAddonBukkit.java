package com.jodexindustries.donatecase.api.addon.internal;

import com.jodexindustries.donatecase.api.DCAPIBukkit;
import org.jetbrains.annotations.NotNull;

public interface InternalAddonBukkit extends InternalAddon {

    /**
     * Gets the class that represent all DonateCase managers
     *
     * @return The global manager
     */
    @NotNull
    DCAPIBukkit getDCAPI();
}
