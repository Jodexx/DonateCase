package com.jodexindustries.donatecase.api.data.material;

import com.jodexindustries.donatecase.api.platform.Platform;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for implementing custom material factories.
 * Implementations must be located in the package
 * {@code com.jodexindustries.donatecase.spigot.materials.factory}.
 */
public interface MaterialFactory {

    /**
     * Creates a {@link CaseMaterial} for the specified platform.
     *
     * @param platform the platform for which the material should be created
     * @return the created material, or {@code null} if this factory
     *         does not support the given platform
     */
    @Nullable CaseMaterial create(Platform platform);
}
