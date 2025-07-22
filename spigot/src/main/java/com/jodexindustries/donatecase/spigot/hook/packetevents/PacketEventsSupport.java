package com.jodexindustries.donatecase.spigot.hook.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.jodexindustries.donatecase.spigot.BukkitBackend;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;

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
            backend.getLogger().info("Loading packetevents hooking...");
            PacketEvents.setAPI(SpigotPacketEventsBuilder.build(backend.getPlugin()));

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

}
