package com.jodexindustries.donatecase.tools.support;

import com.github.retrooper.packetevents.PacketEvents;
import com.jodexindustries.donatecase.DonateCase;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PacketEventsSupport {
    private final JavaPlugin plugin;

    private final boolean enabled;

    public PacketEventsSupport(JavaPlugin plugin) {
        this.plugin = plugin;
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("packetevents");
    }

    public void load() {
        if(enabled) {
            PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
            SpigotEntityLibPlatform platform = new SpigotEntityLibPlatform(plugin);
            APIConfig settings = new APIConfig(PacketEvents.getAPI())
                    .tickTickables()
                    .trackPlatformEntities();
            EntityLib.init(platform, settings);
            if(PacketEvents.getAPI().getServerManager().getVersion().getProtocolVersion() < 758) {
                DonateCase.instance.usePackets = false;
                DonateCase.instance.getLogger().warning("Server version <1.18.2. PacketEvents hook disabled!");
            }
        }
    }

    public void unload() {
        if(enabled) {
            PacketEvents.getAPI().terminate();
        }
    }
    public boolean isEnabled() {
        return enabled;
    }
}
