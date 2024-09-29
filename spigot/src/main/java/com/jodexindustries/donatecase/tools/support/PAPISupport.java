package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.tools.Logger;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PAPISupport {
    private Placeholder placeholder = null;

    public PAPISupport() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) placeholder = new Placeholder();
        Logger.log("&aHooked to &bPlaceholderAPI");
    }

    public void register() {
        if (placeholder != null) placeholder.register();
    }

    public void unregister() {
        if (placeholder != null) placeholder.unregister();
    }

    public String setPlaceholders(OfflinePlayer player, String text) {
        if (placeholder != null) text = PlaceholderAPI.setPlaceholders(player, text);
        return text;
    }

}
