package com.jodexindustries.donatecase.spigot.holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.hologram.HologramDriver;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import net.Zrips.CMILib.Container.CMILocation;

import java.util.HashMap;
import java.util.UUID;

/**
 * Class for CMI Holograms implementation
 */
public class CMIHologramsImpl implements HologramDriver {

    private final HashMap<CaseLocation, CMIHologram> holograms = new HashMap<>();

    @Override
    public void create(CaseLocation block, CaseData.Hologram caseHologram) {
        if (!caseHologram.enabled()) return;

        double height = caseHologram.height();

        CMILocation location = new CMILocation(BukkitUtils.toBukkit(block).add(0.5, height, 0.5));

        CMIHologram hologram = new CMIHologram("DonateCase-" + UUID.randomUUID(), location);

        hologram.setLines(caseHologram.messages());

        hologram.setShowRange(caseHologram.range());

        CMI.getInstance().getHologramManager().addHologram(hologram);

        hologram.update();

        this.holograms.put(block, hologram);
    }

    @Override
    public void remove(CaseLocation block) {
        if (!this.holograms.containsKey(block)) return;

        CMIHologram hologram = this.holograms.get(block);

        this.holograms.remove(block);

        hologram.remove();
    }

    @Override
    public void remove() {
        this.holograms.values().forEach(CMIHologram::remove);
        this.holograms.clear();
    }
}