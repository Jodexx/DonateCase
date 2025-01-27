package com.jodexindustries.donatecase.api.data;

import org.jetbrains.annotations.NotNull;

public enum HologramDriver {

    decentholograms,
    holographicdisplays,
    cmi,
    fancyholograms;

    public static HologramDriver getDriver(@NotNull String name) {
        HologramDriver result = null;
        try {
            result = HologramDriver.valueOf(name.toLowerCase());
        } catch (IllegalArgumentException ignored) {
        }
        return result;
    }
}
