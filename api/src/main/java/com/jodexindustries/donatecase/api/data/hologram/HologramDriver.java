package com.jodexindustries.donatecase.api.data.hologram;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseSettings;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface HologramDriver {

    /**
     * Creates the hologram
     * @param block Block, where hologram will be created
     * @param hologram Case hologram
     */
    default void create(@NotNull CaseLocation block, CaseSettings.Hologram hologram) {
        if (hologram == null || !hologram.enabled()) return;

        forceCreate(block, hologram);
    }

    @ApiStatus.Internal
    void forceCreate(@NotNull CaseLocation block, @NotNull CaseSettings.Hologram hologram);

    @Deprecated
    default void create(@NotNull CaseLocation block, CaseData.Hologram hologram) {
        create(block, CaseData.Hologram.toDefinition(hologram));
    }

    /**
     * Removes the hologram
     * @param block Block, where hologram will be deleted
     */
    void remove(@NotNull CaseLocation block);

    /**
     * Removes all holograms
     */
    void remove();
}
