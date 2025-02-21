package com.jodexindustries.donatecase.spigot.holograms;

import com.jodexindustries.donatecase.api.data.hologram.HologramDriver;
import com.jodexindustries.donatecase.spigot.BukkitBackend;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.PlaceholderSetting;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * Class for HolographicDisplays Holograms implementation
 */
public class HolographicDisplaysImpl implements HologramDriver {


    @NotNull
    private final HolographicDisplaysAPI api = HolographicDisplaysAPI.get(((BukkitBackend) DCAPI.getInstance().getPlatform()).getPlugin());

    private final HashMap<CaseLocation, Hologram> holograms = new HashMap<>();

    @Override
    public void create(CaseLocation block, CaseData.Hologram caseHologram) {
        if (!caseHologram.enabled()) return;

        double height = caseHologram.height();

        Hologram hologram = this.api.createHologram(BukkitUtils.toBukkit(block).add(.5, height, .5));
        hologram.setPlaceholderSetting(PlaceholderSetting.DEFAULT);
        caseHologram.messages().forEach(line -> hologram.getLines().appendText(
                DCTools.rc((line))
        ));

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
        this.holograms.values().forEach(me.filoghost.holographicdisplays.api.hologram.Hologram::delete);
        this.holograms.clear();
    }
}