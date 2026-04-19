package com.jodexindustries.donatecase.spigot.holograms.factory;

import com.google.j2objc.annotations.UsedByReflection;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.hologram.HologramDriver;
import com.jodexindustries.donatecase.api.data.hologram.HologramFactory;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseSettings;
import com.jodexindustries.donatecase.api.data.hologram.AbstractHologramDriver;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@UsedByReflection
public class DecentHologramsFactory implements HologramFactory {

    @UsedByReflection
    public static final DecentHologramsFactory INSTANCE = new DecentHologramsFactory();
    private static final String PLUGIN_NAME = "DecentHolograms";

    @Override
    public @Nullable HologramDriver create(Addon addon) {
        return !Bukkit.getServer().getPluginManager().isPluginEnabled(PLUGIN_NAME) ? null : new DecentHologramsDriver();
    }

    @Override
    public @NotNull String name() {
        return PLUGIN_NAME;
    }

    private static class DecentHologramsDriver extends AbstractHologramDriver {
        @Override
        public void forceCreate(@NotNull CaseLocation block, CaseSettings.@NotNull Hologram caseHologram) {
            if (this.holograms.containsKey(block)) return;

            double height = caseHologram.height();
            Hologram hologram = DHAPI.createHologram("DonateCase-" + UUID.randomUUID(), BukkitUtils.toBukkit(block).add(.5, height, .5));
            hologram.setDisplayRange(caseHologram.range());

            caseHologram.message().forEach(line -> DHAPI.addHologramLine(hologram, DCTools.rc(line)));
            hologram.updateAll();

            this.holograms.put(block, hologram::delete);
        }
    }
}