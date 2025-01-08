package com.jodexindustries.donatecase.impl.holograms;

import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataHologram;
import com.jodexindustries.donatecase.api.manager.HologramManager;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.UUID;

/**
 * Class for Decent Holograms implementation
 */
public class DecentHologramsImpl extends HologramManager {

    private final HashMap<Block, Hologram> holograms = new HashMap<>();

    @Override
    public void createHologram(Block block, CaseDataBukkit caseData) {
        CaseDataHologram caseHologram = caseData.getHologram();

        if (!caseHologram.isEnabled()) return;

        double height = caseHologram.getHeight();
        Hologram hologram = DHAPI.createHologram("DonateCase-" + UUID.randomUUID(), block.getLocation().add(.5, height, .5));

        hologram.setDisplayRange(caseHologram.getRange());

        caseHologram.getMessages().forEach(line -> DHAPI.addHologramLine(hologram, DCToolsBukkit.rc(line)));

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