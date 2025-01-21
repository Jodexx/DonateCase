package com.jodexindustries.donatecase.api.platform;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface DCOfflinePlayer {

    @Nullable String getName();

    @NotNull Object getHandler();

    @NotNull UUID getUniqueId();
}
