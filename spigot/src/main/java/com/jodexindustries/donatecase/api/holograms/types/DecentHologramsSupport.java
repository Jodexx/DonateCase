package com.jodexindustries.donatecase.api.holograms.types;

import com.jodexindustries.donatecase.api.data.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataHologram;
import com.jodexindustries.donatecase.api.holograms.HologramManager;
import com.jodexindustries.donatecase.tools.Tools;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.UUID;

/**
 * Class for Decent Holograms implementation
 */
public class DecentHologramsSupport extends HologramManager {

    private final HashMap<Block, Hologram> holograms = new HashMap<>();

    @Override
    public void createHologram(Block block, CaseDataBukkit caseData) {
        CaseDataHologram caseHologram = caseData.getHologram();

        if (!caseHologram.isEnabled()) return;

        double height = caseHologram.getHeight();
        Hologram hologram = DHAPI.createHologram("DonateCase-" + UUID.randomUUID(), block.getLocation().add(.5, height, .5));

        hologram.setDisplayRange(caseHologram.getRange());

        caseHologram.getMessages().forEach(line -> DHAPI.addHologramLine(hologram, Tools.rc(line)));

        this.holograms.put(block, hologram);
    }

    @Override
    public void removeHologram(Block block) {
        if (!this.holograms.containsKey(block)) return;

        Hologram hologram = this.holograms.get(block);
        this.holograms.remove(block);
        hologram.delete();
    }

    @Override
    public void removeAllHolograms() {
        this.holograms.values().forEach(Hologram::delete);
        this.holograms.clear();
    }
}