package com.jodexindustries.donatecase.api.manager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Interface for implement case management
 * @param <C>        The type of case data structure.
 */
public interface CaseManager<C> {

    /**
     * Is there a case with a type?
     * @param type Case type
     * @return true - if case found in memory
     */
    boolean hasCaseByType(@NotNull String type);

    /**
     * Get a casedata by type
     *
     * @param type Case type
     * @return Case data
     */
    @Nullable
    C getCase(@NotNull String type);

    /**
     * Gets casedata map
     * @return Map of {@link C}
     */
    Map<String, C> getMap();
}
