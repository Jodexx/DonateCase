package com.jodexindustries.donatecase.api.data.hologram;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseSettings;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;

public interface HologramDriver {

    /**
     * Creates the hologram
     * @param block Block, where hologram will be created
     * @param hologram Hooked CaseData Hologram
     */
    void create(CaseLocation block, CaseSettings.Hologram hologram);

    @Deprecated
    default void create(CaseLocation block, CaseData.Hologram hologram) {
        create(block, CaseData.Hologram.toDefinition(hologram));
    }

    /**
     * Removes the hologram
     * @param block Block, where hologram will be deleted
     */
    void remove(CaseLocation block);

    /**
     * Removes all holograms
     */
    void remove();
}
