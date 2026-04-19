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
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.PlaceholderSetting;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UsedByReflection
public class HolographicDisplaysFactory implements HologramFactory {

    @UsedByReflection
    public static final HolographicDisplaysFactory INSTANCE = new HolographicDisplaysFactory();
    private static final String PLUGIN_NAME = "HolographicDisplays";

    @Override
    public @Nullable HologramDriver create(Addon addon) {
        return !Bukkit.getServer().getPluginManager().isPluginEnabled(PLUGIN_NAME) ? null : new HolographicDisplaysDriver();
    }

    @Override
    public @NotNull String name() {
        return PLUGIN_NAME;
    }

    private static class HolographicDisplaysDriver extends AbstractHologramDriver {
        @NotNull
        private final HolographicDisplaysAPI api = HolographicDisplaysAPI.get(BukkitUtils.getDonateCase());

        @Override
        public void forceCreate(@NotNull CaseLocation block, CaseSettings.@NotNull Hologram caseHologram) {
            if (this.holograms.containsKey(block)) return;

            double height = caseHologram.height();

            Hologram hologram = this.api.createHologram(BukkitUtils.toBukkit(block).add(.5, height, .5));
            hologram.setPlaceholderSetting(PlaceholderSetting.DEFAULT);
            caseHologram.message().forEach(line -> hologram.getLines().appendText(
                    DCTools.rc((line))
            ));

            this.holograms.put(block, hologram::delete);
        }
    }

}
