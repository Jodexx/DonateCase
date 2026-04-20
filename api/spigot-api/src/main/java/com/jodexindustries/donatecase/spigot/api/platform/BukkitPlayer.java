package com.jodexindustries.donatecase.spigot.api.platform;

import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.data.storage.CaseWorld;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class BukkitPlayer extends BukkitCommandSender implements DCPlayer {

    private final Player player;

    public BukkitPlayer(@NotNull Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public @NotNull Player getHandler() {
        return player;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public CaseWorld getWorld() {
        return BukkitUtils.fromBukkit(player.getWorld());
    }

    @Override
    public CaseLocation getLocation() {
        return BukkitUtils.fromBukkit(player.getLocation());
    }

    @Override
    public CaseLocation getTargetBlock(int maxDistance) {
        return BukkitUtils.fromBukkit(
                player.getTargetBlock(null, maxDistance).getLocation()
        );
    }

    @Override
    public void openInventory(Object inventory) {
        player.openInventory((Inventory) inventory);
    }

    @Override
    public void closeInventory() {
        player.closeInventory();
    }

    @Override
    public void showTitle(@NotNull String title, @NotNull String subTitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        player.sendTitle(title, subTitle, fadeInTicks, stayTicks, fadeOutTicks);
    }

    @Override
    public void playSound(@NotNull String sound, float volume, float pitch) {
        player.playSound(player.getLocation(), Sound.valueOf(sound.toUpperCase()), volume, pitch);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        BukkitPlayer that = (BukkitPlayer) object;
        return Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), player);
    }
}
