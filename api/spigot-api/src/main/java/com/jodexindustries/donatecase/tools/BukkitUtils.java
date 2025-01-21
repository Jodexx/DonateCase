package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.EulerAngle;

public class BukkitUtils {

    public static CaseLocation fromBukkit(Location location) {
        String world = location.getWorld() == null ? "" : location.getWorld().getName();
        return new CaseLocation(world, location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    }

    public static com.jodexindustries.donatecase.api.armorstand.EulerAngle fromBukkit(EulerAngle eulerAngle) {
        return new com.jodexindustries.donatecase.api.armorstand.EulerAngle(eulerAngle.getX(), eulerAngle.getY(), eulerAngle.getZ());
    }

    public static Location toBukkit(CaseLocation location) {
        return new Location(Bukkit.getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public static EulerAngle toBukkit(com.jodexindustries.donatecase.api.armorstand.EulerAngle eulerAngle) {
        return new EulerAngle(eulerAngle.getX(), eulerAngle.getY(), eulerAngle.getZ());
    }

}
