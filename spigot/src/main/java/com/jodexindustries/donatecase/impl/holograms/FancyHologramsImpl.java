package com.jodexindustries.donatecase.impl.holograms;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.manager.HologramManager;
import com.jodexindustries.donatecase.tools.BukkitUtils;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.data.*;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.api.hologram.HologramType;
import org.bukkit.Location;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.UUID;


public class FancyHologramsImpl implements HologramManager {

    private final de.oliver.fancyholograms.api.HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
    private final HashMap<CaseLocation, Hologram> holograms = new HashMap<>();

    @Override
    public void createHologram(CaseLocation block, CaseData caseData) {
        ConfigurationNode config = DCAPI.getInstance().getConfig().getConfigCases().getCase(caseData.getCaseType());
        if(config == null)  return;

        ConfigurationNode section = config.node("case", "Hologram", "FancyHolograms");
        if (section == null) return;

        HologramType type = HologramType.getByName(section.getString("type"));
        if (type == null) return;

        Location location = BukkitUtils.toBukkit(block);

        String name = "DonateCase-" + UUID.randomUUID();
        DisplayHologramData hologramData;
        
        switch (type) {
            case BLOCK:
                hologramData = new BlockHologramData(name, location);
                break;
            case ITEM:
                hologramData = new ItemHologramData(name, location);
                break;
            case TEXT:
                hologramData = new TextHologramData(name, location);
                break;
            default:
                hologramData = new DisplayHologramData(name, type, location);
                break;
        }

        // TODO Read data
//        if(!hologramData.read(section, name)) hologramData.setLocation(location);

        Hologram hologram = manager.create(hologramData);
        manager.addHologram(hologram);
        this.holograms.put(block, hologram);
    }

    @Override
    public void removeHologram(CaseLocation block) {
        if (!this.holograms.containsKey(block)) return;

        Hologram hologram = this.holograms.get(block);
        this.holograms.remove(block);
        manager.removeHologram(hologram);
    }

    @Override
    public void removeAllHolograms() {
        for (Hologram hologram : this.holograms.values()) {
            manager.removeHologram(hologram);
        }
        this.holograms.clear();
    }
}
