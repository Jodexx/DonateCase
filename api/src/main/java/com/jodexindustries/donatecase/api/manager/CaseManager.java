package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
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

    /**
     * Retrieves legacy case data for the given type.
     * <p>
     * This method exists for backward compatibility with older systems that rely on {@link CaseData}.
     * It is recommended to use {@link #getByType(String)} and work with {@link CaseDefinition} going forward.
     *
     * @param type the case type identifier
     * @return the corresponding {@link CaseData}, or {@code null} if not available
     * @deprecated Legacy API — use {@link #getByType(String)} with {@link CaseDefinition} instead
     */
    @Nullable
    @Deprecated
    CaseData get(@NotNull String type);

    /**
     * Retrieves a complete map of legacy case data.
     * <p>
     * This method is intended for backward compatibility only. Use {@link #definitions()} and
     * transform data as needed for modern use cases.
     *
     * @return a map where the key is the case type and the value is {@link CaseData}
     * @deprecated Legacy API — use {@link #definitions()} instead
     */
    @Deprecated
    Map<String, CaseData> getMap();
}
