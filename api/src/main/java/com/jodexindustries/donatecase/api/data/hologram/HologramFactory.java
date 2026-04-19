package com.jodexindustries.donatecase.api.data.hologram;

import com.jodexindustries.donatecase.api.addon.Addon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HologramFactory {

    @Nullable HologramDriver create(Addon addon);

    @NotNull String name();
}
