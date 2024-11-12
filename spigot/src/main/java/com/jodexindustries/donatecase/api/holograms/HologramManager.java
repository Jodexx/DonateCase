package com.jodexindustries.donatecase.api.holograms;

import com.jodexindustries.donatecase.api.data.CaseDataBukkit;
import org.bukkit.block.Block;

public abstract class HologramManager {

    /**
     * Creates the hologram
     * @param block Block, where hologram will be created
     * @param caseData Hooked CaseData
     */
    public abstract void createHologram(Block block, CaseDataBukkit caseData);

    /**
     * Removes the hologram
     * @param block Block, where hologram will be deleted
     */
    public abstract void removeHologram(Block block);

    public abstract void removeAllHolograms();

}