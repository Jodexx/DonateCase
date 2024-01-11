package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.dc.Main;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PAPISupport {
    public static String setPlaceholders(Player player, String text) {
        if(Main.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }
}
