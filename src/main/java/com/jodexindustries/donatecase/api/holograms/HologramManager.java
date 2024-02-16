package com.jodexindustries.donatecase.api.holograms;

import com.jodexindustries.donatecase.api.data.CaseData;
import org.bukkit.block.Block;

public abstract class HologramManager {

    public abstract void createHologram(Block block, CaseData caseData);

    public abstract void removeHologram(Block block);

    public abstract void removeAllHolograms();

}