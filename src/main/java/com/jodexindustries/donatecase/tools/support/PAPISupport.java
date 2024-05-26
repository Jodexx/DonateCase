package com.jodexindustries.donatecase.tools.support;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

import static com.jodexindustries.donatecase.DonateCase.instance;

public class PAPISupport {
    public static String setPlaceholders(OfflinePlayer player, String text) {
        if(instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }
}
