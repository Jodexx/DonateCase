package com.jodexindustries.donatecase.spigot.hook.papi;

import com.jodexindustries.donatecase.spigot.BukkitBackend;
import com.jodexindustries.donatecase.api.tools.PAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

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
    public String setPlaceholders(Object player, String text) {
        if (donateCaseExpansion != null) text = PlaceholderAPI.setPlaceholders((OfflinePlayer) player, text);
        return text;
    }

}
