package com.jodexindustries.donatecase.tools.support;

import com.github.retrooper.packetevents.PacketEvents;
import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.protocol.ServerVersion;
import com.jodexindustries.donatecase.tools.Logger;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
import org.bukkit.Bukkit;

public class PacketEventsSupport {

    public PacketEventsSupport(DonateCase plugin) {

        boolean usePackets = plugin.config.getConfig().getBoolean("DonateCase.UsePackets");
        if (usePackets) {
            ServerVersion version = getServerVersion();
            Logger.log("&aLoading &bpacketevents &ahooking...");
            Logger.log("&aServer version: &b" + version.getReleaseName());
            Logger.log("&aServer protocol version: &b" + version.getProtocolVersion());
            if (getServerVersion().isOlderThan(ServerVersion.V_1_18)) {
                DonateCase.instance.usePackets = false;
                plugin.getLogger().warning("Server version older than V_1_18. PacketEvents hooking disabled!");
                return;
            }
            PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
            SpigotEntityLibPlatform platform = new SpigotEntityLibPlatform(plugin);
            APIConfig settings = new APIConfig(PacketEvents.getAPI())
                    .tickTickables()
                    .trackPlatformEntities();
            EntityLib.init(platform, settings);

            if (PacketEvents.getAPI().isLoaded()) {
                Logger.log("&aHooked to &bpacketevents");
                DonateCase.instance.usePackets = true;
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
