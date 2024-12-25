package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.tools.Logger;
import com.jodexindustries.donatecase.tools.PAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PAPISupport implements PAPI {
    private Placeholder placeholder = null;

    public PAPISupport() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) placeholder = new Placeholder();
        Logger.log("&aHooked to &bPlaceholderAPI");
    }

    @Override
    public void register() {
        if (placeholder != null) placeholder.register();
    }

    @Override
    public void unregister() {
        if (placeholder != null) placeholder.unregister();
    }

    @Override
    public String setPlaceholders(OfflinePlayer player, String text) {
        if (placeholder != null) text = PlaceholderAPI.setPlaceholders(player, text);
        return text;
    }

}
