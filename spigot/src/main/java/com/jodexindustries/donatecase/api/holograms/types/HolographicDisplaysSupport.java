package com.jodexindustries.donatecase.api.holograms.types;

import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataHologram;
import com.jodexindustries.donatecase.api.holograms.HologramManager;
import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.PlaceholderSetting;
import org.jetbrains.annotations.NotNull;
import org.bukkit.block.Block;
import java.util.HashMap;

/**
 * Class for HolographicDisplays Holograms implementation
 */
public class HolographicDisplaysSupport extends HologramManager {


    @NotNull
    private final HolographicDisplaysAPI api = HolographicDisplaysAPI.get(DonateCase.instance);

    private final HashMap<Block, Hologram> holograms = new HashMap<>();

    @Override
    public void createHologram(Block block, CaseDataBukkit caseData) {
        CaseDataHologram crateHologram = caseData.getHologram();

        if (!crateHologram.isEnabled()) return;

        double height = crateHologram.getHeight();

        Hologram hologram = this.api.createHologram(block.getLocation().add(.5, height, .5));
        hologram.setPlaceholderSetting(PlaceholderSetting.DEFAULT);
        crateHologram.getMessages().forEach(line -> hologram.getLines().appendText(
                DCToolsBukkit.rc((line))
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
        this.holograms.values().forEach(Hologram::delete);
        this.holograms.clear();
    }
}