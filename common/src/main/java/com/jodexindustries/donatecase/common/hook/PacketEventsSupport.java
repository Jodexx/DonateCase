package com.jodexindustries.donatecase.common.hook;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.jodexindustries.donatecase.common.platform.BackendPlatform;
import lombok.Getter;

public abstract class PacketEventsSupport {

    protected final PacketEventsAPI<?> api = PacketEvents.getAPI();
    protected final BackendPlatform platform;

    @Getter
    protected boolean usePackets;

    protected PacketEventsSupport(BackendPlatform platform) {
        this.platform = platform;
        this.usePackets = platform.getAPI().getConfigManager().getConfig().usePackets();
    }

    public void load() {
        if (usePackets) {
            if (api.isLoaded()) {
                platform.getLogger().info("Hooked to packetevents");
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
