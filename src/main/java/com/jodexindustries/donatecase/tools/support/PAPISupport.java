package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.tools.Logger;
import com.jodexindustries.donatecase.tools.Placeholder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PAPISupport {
    private final Placeholder placeholder;

    public PAPISupport() {
        this.placeholder = new Placeholder();
    }

    public void register() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholder.register();
            Logger.log("&aPlaceholders registered!");
        }
    }
    public void unregister() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholder.unregister();
        }
    }

    public static String setPlaceholders(OfflinePlayer player, String text) {
        if(Case.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }
}
