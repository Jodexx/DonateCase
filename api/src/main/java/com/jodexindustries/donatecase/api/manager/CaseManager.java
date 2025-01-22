package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Interface for implement case management
 */
public interface CaseManager {

    /**
     * Is there a case with a type?
     * @param type Case type
     * @return true - if case found in memory
     */
    boolean hasByType(@NotNull String type);

    /**
     * Get a casedata by type
     *
     * @param type Case type
     * @return Case data
     */
    @Nullable
    CaseData get(@NotNull String type);

    /**
     * Gets casedata map
     * @return Map of {@link CaseData}
     */
    Map<String, CaseData> getMap();
}
