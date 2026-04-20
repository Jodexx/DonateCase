package com.jodexindustries.donatecase.spigot.holograms.factory;

import com.google.j2objc.annotations.UsedByReflection;
import com.jodexindustries.donatecase.api.data.hologram.HologramDriver;
import com.jodexindustries.donatecase.api.data.hologram.HologramFactory;
import com.jodexindustries.donatecase.api.platform.Platform;
import com.jodexindustries.donatecase.spigot.holograms.factory.serializer.ConfigurationSectionImpl;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.BlockHologramData;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.data.ItemHologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.api.hologram.HologramType;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseSettings;
import com.jodexindustries.donatecase.api.data.hologram.AbstractHologramDriver;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.UUID;

@UsedByReflection
public class FancyHologramsFactory implements HologramFactory {

    @UsedByReflection
    public static final FancyHologramsFactory INSTANCE = new FancyHologramsFactory();
    private static final String PLUGIN_NAME = "FancyHolograms";

    @Override
    public @Nullable HologramDriver create(Platform platform) {
        return !Bukkit.getServer().getPluginManager().isPluginEnabled(PLUGIN_NAME) ? null : new FancyHologramsDriver();
    }

    @Override
    public @NotNull String name() {
        return PLUGIN_NAME;
    }

    private static class FancyHologramsDriver extends AbstractHologramDriver {
        private final HologramManager manager = FancyHologramsPlugin.get().getHologramManager();

        @Override
        public void forceCreate(@NotNull CaseLocation block, CaseSettings.@NotNull Hologram caseHologram) {
            if (this.holograms.containsKey(block)) return;

            ConfigurationNode node = caseHologram.node();

            HologramType type = HologramType.getByName(node.node("type").getString());
            if (type == null) return;

            Location location = BukkitUtils.toBukkit(block).add(.5, caseHologram.height(), .5);

            String name = "DonateCase-" + UUID.randomUUID();
            DisplayHologramData hologramData = getData(type, name, location);
            hologramData.read(new ConfigurationSectionImpl(node), name);
            Location tempLocation = hologramData.getLocation();
            if (tempLocation.getYaw() != 0) location.setYaw(tempLocation.getYaw());
            if (tempLocation.getPitch() != 0) location.setPitch(tempLocation.getPitch());
            hologramData.setLocation(location);
            hologramData.setPersistent(false); // disable saving

            Hologram hologram = manager.create(hologramData);

            this.holograms.put(block, () -> manager.removeHologram(hologram));
            manager.addHologram(hologram);
        }

        private static @NotNull DisplayHologramData getData(HologramType type, String name, Location location) {

            return switch (type) {
                case BLOCK -> new BlockHologramData(name, location);
                case ITEM -> new ItemHologramData(name, location);
                case TEXT -> new TextHologramData(name, location);
            };
        }
    }

}
