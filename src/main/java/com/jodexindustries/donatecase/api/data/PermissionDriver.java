package com.jodexindustries.donatecase.api.data;

import org.jetbrains.annotations.NotNull;

public enum PermissionDriver {
    luckperms,
    vault;
    public static PermissionDriver getDriver(@NotNull String name) {
        PermissionDriver result = null;
        try {
            result = PermissionDriver.valueOf(name.toLowerCase());
        } catch (IllegalArgumentException ignored) {}
        return result;
    }
}
