package com.jodexindustries.donatecase.api.holograms.types;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.holograms.HologramManager;
import com.jodexindustries.donatecase.DonateCase;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.jetbrains.annotations.NotNull;
import org.bukkit.block.Block;
import java.util.HashMap;

public class HolographicDisplaysSupport extends HologramManager {

    @NotNull
    private final DonateCase plugin = DonateCase.instance;

    @NotNull
    private final HolographicDisplaysAPI api = HolographicDisplaysAPI.get(this.plugin);

    private final HashMap<Block, Hologram> holograms = new HashMap<>();

    @Override
    public void createHologram(Block block, CaseData caseData) {
        CaseData.Hologram crateHologram = caseData.getHologram();

        if (!crateHologram.isEnabled()) return;

        double height = crateHologram.getHeight();

        Hologram hologram = this.api.createHologram(block.getLocation().add(.5, height, .5));

        crateHologram.getMessages().forEach(line -> hologram.getLines().appendText(
                Case.getTools().rc((line))
        ));

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