package com.jodexindustries.donatecase.api.data.hologram;

import com.jodexindustries.donatecase.api.platform.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for implementing custom hologram factories.
 * Implementations must be located in the package
 * {@code com.jodexindustries.donatecase.spigot.holograms.factory}.
 */
public interface HologramFactory {

    /**
     * Creates a {@link HologramDriver} for the specified platform.
     *
     * @param platform the platform for which the hologram driver should be created
     * @return the created driver, or {@code null} if this factory
     *         does not support the given platform
     */
    @Nullable HologramDriver create(Platform platform);

    /**
     * Returns the unique name of driver.
     *
     * @return the driver name
     */
    @NotNull String name();
}
