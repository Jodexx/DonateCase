package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.storage.CaseInfo;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;

import java.util.Map;

public interface HologramManager {

    default void load() {
        DCAPI api = DCAPI.getInstance();

        remove();
        for (Map.Entry<String, CaseInfo> entry : api.getConfig().getCaseStorage().get().entrySet()) {
            CaseInfo info = entry.getValue();

            String caseType = info.getType();

            CaseData caseData = api.getCaseManager().get(caseType);

            if(caseData == null || !caseData.getHologram().isEnabled()) continue;

            CaseLocation location = info.getLocation();

            if(!api.getPlatform().isWorldLoaded(location.getWorld())) {
                api.getPlatform().getLogger().warning("Hologram creation error. World " + location.getWorld() + " is null for case name: " + entry.getKey());
                continue;
            }

            create(location, caseData);
        }
    }

    /**
     * Creates the hologram
     * @param block Block, where hologram will be created
     * @param caseData Hooked CaseData
     */
    void create(CaseLocation block, CaseData caseData);

    /**
     * Removes the hologram
     * @param block Block, where hologram will be deleted
     */
    void remove(CaseLocation block);

    void remove();

}