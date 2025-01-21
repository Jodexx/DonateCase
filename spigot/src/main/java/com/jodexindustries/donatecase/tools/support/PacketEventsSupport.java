package com.jodexindustries.donatecase.tools.support;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.jodexindustries.donatecase.BukkitBackend;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
import org.bukkit.Bukkit;

public class PacketEventsSupport {

    private final BukkitBackend backend;

    @Getter
    private boolean usePackets;

    public PacketEventsSupport(BukkitBackend backend) {
        this.backend = backend;
        this.usePackets = backend.getAPI().getConfig().getConfig().node("DonateCase", "UsePackets").getBoolean();
    }

    public void load() {
        if (usePackets) {
            ServerVersion version = getServerVersion();
            backend.getLogger().info("Loading packetevents hooking...");
            backend.getLogger().info("Server version: " + version.getReleaseName());
            backend.getLogger().info("Server protocol version: " + version.getProtocolVersion());
            if (getServerVersion().isOlderThan(ServerVersion.V_1_18)) {
                backend.getLogger().warning("Server version older than V_1_18. PacketEvents hooking disabled!");
                return;
            }
            PacketEvents.setAPI(SpigotPacketEventsBuilder.build(backend.getPlugin()));
            SpigotEntityLibPlatform platform = new SpigotEntityLibPlatform(backend.getPlugin());
            APIConfig settings = new APIConfig(PacketEvents.getAPI())
                    .tickTickables()
                    .trackPlatformEntities();
            EntityLib.init(platform, settings);

            if (PacketEvents.getAPI().isLoaded()) {
                backend.getLogger().info("Hooked to packetevents");
                usePackets = true;
            } else {
                usePackets = false;
            }
        }
    }

    public void unload() {
        PacketEvents.getAPI().terminate();
    }

    private ServerVersion getServerVersion() {
        String bukkitVersion = Bukkit.getBukkitVersion();
        for (final ServerVersion val : ServerVersion.reversedValues()) {
            if (bukkitVersion.contains(val.getReleaseName())) {
                return val;
            }
        }
        return ServerVersion.ERROR;
    }

}
