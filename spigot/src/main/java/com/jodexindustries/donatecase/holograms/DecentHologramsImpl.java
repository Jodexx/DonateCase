package com.jodexindustries.donatecase.holograms;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.manager.HologramManager;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.tools.BukkitUtils;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;

import java.util.HashMap;
import java.util.UUID;

/**
 * Class for Decent Holograms implementation
 */
public class DecentHologramsImpl implements HologramManager {

    private final HashMap<CaseLocation, Hologram> holograms = new HashMap<>();

    @Override
    public void create(CaseLocation block, CaseData.Hologram caseHologram) {
        if (!caseHologram.isEnabled()) return;

        double height = caseHologram.getHeight();
        Hologram hologram = DHAPI.createHologram("DonateCase-" + UUID.randomUUID(), BukkitUtils.toBukkit(block).add(.5, height, .5));

        hologram.setDisplayRange(caseHologram.getRange());

        caseHologram.getMessages().forEach(line -> DHAPI.addHologramLine(hologram, DCTools.rc(line)));

        this.holograms.put(block, hologram);
    }

    @Override
    public void remove(CaseLocation block) {
        if (!this.holograms.containsKey(block)) return;

        Hologram hologram = this.holograms.get(block);
        this.holograms.remove(block);
        hologram.delete();
    }

    @Override
    public void remove() {
        this.holograms.values().forEach(eu.decentsoftware.holograms.api.holograms.Hologram::delete);
        this.holograms.clear();
    }
}