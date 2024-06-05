package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.api.Case;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

public class PAPISupport {
    public static String setPlaceholders(OfflinePlayer player, String text) {
        if(Case.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }
}
