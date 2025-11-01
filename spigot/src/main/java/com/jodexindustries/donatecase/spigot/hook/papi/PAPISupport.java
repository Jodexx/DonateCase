package com.jodexindustries.donatecase.spigot.hook.papi;

import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.spigot.BukkitBackend;
import com.jodexindustries.donatecase.api.tools.PAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PAPISupport implements PAPI {

    private DonateCaseExpansion donateCaseExpansion = null;

    public PAPISupport(BukkitBackend backend) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            donateCaseExpansion = new DonateCaseExpansion(backend);
            backend.getLogger().info("Hooked to PlaceholderAPI");
        }
    }

    @Override
    public void register() {
        if (donateCaseExpansion != null) donateCaseExpansion.register();
    }

    @Override
    public void unregister() {
        if (donateCaseExpansion != null) donateCaseExpansion.unregister();
    }

    @Override
    public String setPlaceholders(@Nullable Object player, String text) {
        if (donateCaseExpansion == null) return text;

        OfflinePlayer offlinePlayer = getPlayer(player);

        return PlaceholderAPI.setPlaceholders(offlinePlayer, text);
    }

    @Override
    public List<String> setPlaceholders(@Nullable Object player, List<String> text) {
        if (donateCaseExpansion == null) return text;

        OfflinePlayer offlinePlayer = getPlayer(player);

        return PlaceholderAPI.setPlaceholders(offlinePlayer, text);
    }

    @Nullable
    private OfflinePlayer getPlayer(@Nullable Object player) {
        if (player == null) return null;

        return player instanceof DCPlayer ? ((OfflinePlayer) ((DCPlayer) player).getHandler()) : ((OfflinePlayer) player);
    }
}
