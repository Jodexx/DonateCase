package com.jodexindustries.donatecase.api.holograms.types;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.holograms.HologramManager;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.data.*;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.api.hologram.HologramType;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.UUID;

public class FancyHologramsSupport extends HologramManager {

    private final de.oliver.fancyholograms.api.HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
    private final HashMap<Block, Hologram> holograms = new HashMap<>();

    @Override
    public void createHologram(Block block, CaseDataBukkit caseData) {
        YamlConfiguration config = Case.getConfig().getCasesConfig().getCase(caseData.getCaseType()).getSecond();
        if (config == null) return;

        ConfigurationSection section = config.getConfigurationSection("case.Hologram.FancyHolograms");
        if (section == null) return;

        HologramType type = HologramType.getByName(section.getString("type"));
        if (type == null) return;

        String name = "DonateCase-" + UUID.randomUUID();
        DisplayHologramData hologramData;
        
        switch (type) {
            case BLOCK:
                hologramData = new BlockHologramData(name, block.getLocation());
                break;
            case ITEM:
                hologramData = new ItemHologramData(name, block.getLocation());
                break;
            case TEXT:
                hologramData = new TextHologramData(name, block.getLocation());
                break;
            default:
                hologramData = new DisplayHologramData(name, type, block.getLocation());
                break;
        }

        if(!hologramData.read(section, name)) hologramData.setLocation(block.getLocation());

        Hologram hologram = manager.create(hologramData);
        manager.addHologram(hologram);
        this.holograms.put(block, hologram);
    }

    @Override
    public void removeHologram(Block block) {
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
