package com.jodexindustries.donatecase.spigot.tools;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.data.storage.CaseWorld;
import com.jodexindustries.donatecase.api.platform.Platform;
import com.jodexindustries.donatecase.spigot.api.platform.BukkitCommandSender;
import com.jodexindustries.donatecase.spigot.api.platform.BukkitPlayer;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BukkitUtils {

    public static CaseWorld fromBukkit(World world) {
        if (world == null) return null;
        CaseLocation spawnLocation = fromBukkit(world.getSpawnLocation());
        CaseWorld caseWorld = new CaseWorld(world.getName());
        caseWorld.spawnLocation(spawnLocation);
        return caseWorld;
    }

    @NotNull
    public static CaseLocation fromBukkit(@NotNull Location location) {
        return new CaseLocation(
                location.getWorld() != null ? location.getWorld().getName() : null,
                location.getX(), location.getY(), location.getZ(),
                location.getPitch(), location.getYaw()
        );
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
        CaseWorld world = location.getWorld();
        World bukkitWorld = null;
        if (world != null) bukkitWorld = Bukkit.getWorld(world.name());

        return new Location(bukkitWorld, location.x(), location.y(), location.z(), location.yaw(), location.pitch());
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

    public static Plugin getDonateCase() {
        try {
            Platform platform = DCAPI.getInstance().getPlatform();

            Method method = platform.getClass().getDeclaredMethod("getPlugin");
            return (Plugin) method.invoke(platform);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
