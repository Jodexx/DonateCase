package com.jodexindustries.donatecase.api.holograms.types;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.holograms.HologramManager;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.block.Block;
import java.util.HashMap;
import java.util.UUID;

public class DecentHologramsSupport extends HologramManager {

    private final HashMap<Block, Hologram> holograms = new HashMap<>();

    @Override
    public void createHologram(Block block, CaseData caseData) {
        CaseData.Hologram caseHologram = caseData.getHologram();

        if (!caseHologram.isEnabled()) return;

        double height = caseHologram.getHeight();
        Hologram hologram = DHAPI.createHologram("DonateCase-" + UUID.randomUUID(), block.getLocation().add(.5, height, .5));

        hologram.setDisplayRange(caseHologram.getRange());

        caseHologram.getMessages().forEach(line -> DHAPI.addHologramLine(hologram, Case.getTools().rc(line)));

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
        this.holograms.forEach((key, value) -> value.delete());
        this.holograms.clear();
    }
}