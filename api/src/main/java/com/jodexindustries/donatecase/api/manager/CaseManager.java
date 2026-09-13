package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

/**
 * Interface for managing case definitions and legacy case data.
 * <p>
 * Recommended usage involves {@link CaseDefinition} as the primary structure.
 */
public interface CaseManager {

    /**
     * Checks whether a case definition is available for the given type.
     *
     * @param type the case type identifier (e.g., "vote", "daily", "premium")
     * @return {@code true} if a case definition exists for the given type, {@code false} otherwise
     */
    boolean hasByType(@NotNull String type);

    /**
     * Retrieves the case definition for the specified type.
     *
     * @param type the case type identifier
     * @return an {@link Optional} containing the case definition if found, or empty if not
     */
    @NotNull
    Optional<CaseDefinition> getByType(@NotNull String type);

    /**
     * Returns an unmodifiable collection of all loaded case definitions.
     *
     * @return all currently loaded {@link CaseDefinition} instances
     */
    @NotNull
    Collection<CaseDefinition> definitions();

}
