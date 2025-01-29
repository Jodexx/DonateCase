package com.jodexindustries.donatecase.spigot.api.platform;

import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

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
}
