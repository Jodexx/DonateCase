package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.platform.BukkitCommandSender;
import com.jodexindustries.donatecase.api.platform.BukkitPlayer;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

public class BukkitUtils {

    @NotNull
    public static CaseLocation fromBukkit(@NotNull Location location) {
        String world = location.getWorld() == null ? "" : location.getWorld().getName();
        return new CaseLocation(world, location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    }

    @NotNull
    public static com.jodexindustries.donatecase.api.armorstand.EulerAngle fromBukkit(@NotNull EulerAngle eulerAngle) {
        return new com.jodexindustries.donatecase.api.armorstand.EulerAngle(eulerAngle.getX(), eulerAngle.getY(), eulerAngle.getZ());
    }

    @NotNull
    public static DCPlayer fromBukkit(@NotNull Player player) {
        return new BukkitPlayer(player);
    }

    @NotNull
    public static DCCommandSender fromBukkit(@NotNull CommandSender sender) {
        return new BukkitCommandSender(sender);
    }

    @NotNull
    public static Location toBukkit(@NotNull CaseLocation location) {
        return new Location(Bukkit.getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @NotNull
    public static EulerAngle toBukkit(@NotNull com.jodexindustries.donatecase.api.armorstand.EulerAngle eulerAngle) {
        return new EulerAngle(eulerAngle.getX(), eulerAngle.getY(), eulerAngle.getZ());
    }

    @NotNull
    public static Player toBukkit(@NotNull DCPlayer player) {
        return (Player) player.getHandler();
    }

    @NotNull
    public static CommandSender toBukkit(@NotNull DCCommandSender sender) {
        return (CommandSender) sender.getHandler();
    }

}
