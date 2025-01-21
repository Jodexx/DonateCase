package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;

public interface HologramManager {

    /**
     * Creates the hologram
     * @param block Block, where hologram will be created
     * @param caseData Hooked CaseData
     */
    void createHologram(CaseLocation block, CaseData caseData);

    /**
     * Removes the hologram
     * @param block Block, where hologram will be deleted
     */
    void removeHologram(CaseLocation block);

    void removeAllHolograms();

}