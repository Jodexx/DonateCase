package com.jodexindustries.donatecase.spigot.hook.packetevents;

import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.jodexindustries.donatecase.spigot.BukkitBackend;

public class PacketEventsSupportImpl extends com.jodexindustries.donatecase.common.hook.PacketEventsSupport {

    private final PacketEventsPacketListener listener = new PacketEventsPacketListener();

    public PacketEventsSupportImpl(BukkitBackend backend) {
        super(backend);
        load();
    }

    @Override
    public void load() {
        super.load();

        if (usePackets) {
            api.getEventManager().registerListener(listener, PacketListenerPriority.NORMAL);
        }
    }

}
