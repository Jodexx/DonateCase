package com.jodexindustries.donatecase.tools.support;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PAPISupport {
    public static String setPlaceholders(Player player, String text) {
        text = PlaceholderAPI.setPlaceholders(player, text);
        return text;
    }
}
