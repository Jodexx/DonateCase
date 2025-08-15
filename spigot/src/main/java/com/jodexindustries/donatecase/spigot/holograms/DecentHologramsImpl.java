package com.jodexindustries.donatecase.spigot.holograms;

import com.jodexindustries.donatecase.api.data.casedefinition.CaseSettings;
import com.jodexindustries.donatecase.api.data.hologram.HologramDriver;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

/**
 * Class for Decent Holograms implementation
 */
public class DecentHologramsImpl implements HologramDriver {

    private final HashMap<CaseLocation, Hologram> holograms = new HashMap<>();

    @Override
    public void forceCreate(@NotNull CaseLocation block, CaseSettings.@NotNull Hologram caseHologram) {
        if (this.holograms.containsKey(block)) return;

        double height = caseHologram.height();
        Hologram hologram = DHAPI.createHologram("DonateCase-" + UUID.randomUUID(), BukkitUtils.toBukkit(block).add(.5, height, .5));
        hologram.setDisplayRange(caseHologram.range());

        caseHologram.message().forEach(line -> DHAPI.addHologramLine(hologram, DCTools.rc(line)));
        hologram.updateAll();

        this.holograms.put(block, hologram);
    }

    @Override
    public void remove(@NotNull CaseLocation block) {
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