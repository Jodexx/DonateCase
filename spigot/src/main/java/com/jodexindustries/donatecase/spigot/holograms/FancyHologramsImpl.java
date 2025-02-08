package com.jodexindustries.donatecase.spigot.holograms;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.hologram.HologramDriver;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.spigot.serializer.ConfigurationSectionImpl;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.*;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.api.hologram.HologramType;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.UUID;

public class FancyHologramsImpl implements HologramDriver {

    private final HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
    private final HashMap<CaseLocation, Hologram> holograms = new HashMap<>();

    @Override
    public void create(CaseLocation block, CaseData.Hologram caseHologram) {
        ConfigurationNode node = caseHologram.getNode();

        HologramType type = HologramType.getByName(node.node("type").getString());
        if (type == null) return;

        Location location = BukkitUtils.toBukkit(block).add(.5, caseHologram.getHeight(), .5);

        String name = "DonateCase-" + UUID.randomUUID();
        DisplayHologramData hologramData = getData(type, name, location);
        hologramData.read(new ConfigurationSectionImpl(node), name);
        hologramData.setLocation(location);

        Hologram hologram = manager.create(hologramData);
        manager.addHologram(hologram);
        this.holograms.put(block, hologram);
    }

    @Override
    public void remove(CaseLocation block) {
        Hologram hologram = this.holograms.get(block);
        if(hologram == null) return;

        this.holograms.remove(block);
        manager.removeHologram(hologram);
    }

    @Override
    public void remove() {
        for (Hologram hologram : this.holograms.values()) {
            manager.removeHologram(hologram);
        }
        this.holograms.clear();
    }

    private static @NotNull DisplayHologramData getData(HologramType type, String name, Location location) {
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

        return hologramData;
    }

}