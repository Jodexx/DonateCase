package com.jodexindustries.donatecase.spigot.api.armorstand;

import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.common.entity.creator.APASC;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PacketArmorStandCreator extends APASC {

    public PacketArmorStandCreator(UUID animationId, CaseLocation location) {
        super(
                resolveViewers(location),
                animationId, SpigotReflectionUtil.generateEntityId(), location
        );
    }

    private static Collection<? extends Player> resolveViewers(CaseLocation location) {
        String worldName = location.world();
        if (worldName == null) return Bukkit.getOnlinePlayers();

        World world = Bukkit.getWorld(worldName);
        if (world == null) return Bukkit.getOnlinePlayers();

        double radius = Bukkit.getViewDistance() * 16.0D;
        double radiusSquared = radius * radius;
        double x = location.x();
        double z = location.z();

        List<Player> viewers = new ArrayList<>();
        for (Player player : world.getPlayers()) {
            Location playerLocation = player.getLocation();
            double dx = playerLocation.getX() - x;
            double dz = playerLocation.getZ() - z;
            if (dx * dx + dz * dz <= radiusSquared) viewers.add(player);
        }

        return viewers;
    }

    @Override
    public void setEquipment(EquipmentSlot equipmentSlot, Object item) {
        setEquipment0(equipmentSlot, SpigotReflectionUtil.decodeBukkitItemStack((ItemStack) item));
    }

}
