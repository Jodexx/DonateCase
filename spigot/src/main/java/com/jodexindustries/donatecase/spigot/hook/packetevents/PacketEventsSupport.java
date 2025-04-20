package com.jodexindustries.donatecase.spigot.hook.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.jodexindustries.donatecase.spigot.BukkitBackend;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
import org.bukkit.Bukkit;

public class PacketEventsSupport {

    private final PacketEventsAPI<?> api = PacketEvents.getAPI();
    private final PacketEventsPacketListener listener = new PacketEventsPacketListener();

    private final BukkitBackend backend;

    @Getter
    private boolean usePackets;

    public PacketEventsSupport(BukkitBackend backend) {
        this.backend = backend;
        this.usePackets = backend.getAPI().getConfigManager().getConfig().usePackets();
        load();
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
            APIConfig settings = new APIConfig(api)
                    .tickTickables()
                    .trackPlatformEntities();
            EntityLib.init(platform, settings);

            api.getEventManager().registerListener(listener, PacketListenerPriority.NORMAL);

            if (api.isLoaded()) {
                backend.getLogger().info("Hooked to packetevents");
                usePackets = true;
            } else {
                usePackets = false;
            }
        }
    }

    public void unload() {
        api.terminate();
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
