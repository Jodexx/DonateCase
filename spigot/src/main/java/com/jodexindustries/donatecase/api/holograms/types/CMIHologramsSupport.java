package com.jodexindustries.donatecase.api.holograms.types;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.jodexindustries.donatecase.api.data.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataHologram;
import com.jodexindustries.donatecase.api.holograms.HologramManager;
import org.bukkit.block.Block;
import net.Zrips.CMILib.Container.CMILocation;

import java.util.HashMap;
import java.util.UUID;

/**
 * Class for CMI Holograms implementation
 */
public class CMIHologramsSupport extends HologramManager {

    private final HashMap<Block, CMIHologram> holograms = new HashMap<>();

    @Override
    public void createHologram(Block block, CaseDataBukkit caseData) {
        CaseDataHologram caseHologram = caseData.getHologram();

        if (!caseHologram.isEnabled()) return;

        double height = caseHologram.getHeight();

        CMILocation location = new CMILocation(block.getLocation().add(0.5, height, 0.5));

        CMIHologram hologram = new CMIHologram("DonateCase-" + UUID.randomUUID(), location);

        hologram.setLines(caseHologram.getMessages());

        hologram.setShowRange(caseHologram.getRange());

        CMI.getInstance().getHologramManager().addHologram(hologram);

        hologram.update();

        this.holograms.put(block, hologram);
    }

    @Override
    public void removeHologram(Block block) {
        if (!this.holograms.containsKey(block)) return;

        CMIHologram hologram = this.holograms.get(block);

        this.holograms.remove(block);

        hologram.remove();
    }

    @Override
    public void removeAllHolograms() {
        this.holograms.values().forEach(CMIHologram::remove);
        this.holograms.clear();
    }
}