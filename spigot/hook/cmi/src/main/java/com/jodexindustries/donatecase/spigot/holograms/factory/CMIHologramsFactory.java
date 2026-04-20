package com.jodexindustries.donatecase.spigot.holograms.factory;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.google.j2objc.annotations.UsedByReflection;
import com.jodexindustries.donatecase.api.data.hologram.HologramDriver;
import com.jodexindustries.donatecase.api.data.hologram.HologramFactory;
import com.jodexindustries.donatecase.api.platform.Platform;
import net.Zrips.CMILib.Container.CMILocation;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseSettings;
import com.jodexindustries.donatecase.api.data.hologram.AbstractHologramDriver;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@UsedByReflection
public class CMIHologramsFactory implements HologramFactory {

    @UsedByReflection
    public static final CMIHologramsFactory INSTANCE = new CMIHologramsFactory();
    private static final String PLUGIN_NAME = "CMI";

    @Override
    public @Nullable HologramDriver create(Platform platform) {
        return !Bukkit.getServer().getPluginManager().isPluginEnabled(PLUGIN_NAME) ? null : new CMIHologramsDriver();
    }

    @Override
    public @NotNull String name() {
        return PLUGIN_NAME;
    }

    private static class CMIHologramsDriver extends AbstractHologramDriver {
        @Override
        public void forceCreate(@NotNull CaseLocation block, CaseSettings.@NotNull Hologram caseHologram) {
            double height = caseHologram.height();

            CMILocation location = new CMILocation(BukkitUtils.toBukkit(block).add(0.5, height, 0.5));

            CMIHologram hologram = new CMIHologram("DonateCase-" + UUID.randomUUID(), location);

            hologram.getPages().setLines(caseHologram.message());
            hologram.getSettings().setVisibilityRange(caseHologram.range());

            CMI.getInstance().getHologramManager().add(hologram);

            hologram.update();

            this.holograms.put(block, hologram::remove);
        }
    }

}
