package com.jodexindustries.donatecase.tools.support;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.tools.Logger;
import com.jodexindustries.donatecase.tools.Placeholder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

public class PAPISupport {
    private final Placeholder placeholder;

    public PAPISupport() {
        this.placeholder = new Placeholder();
    }

    public void register() {
        placeholder.register();
        Logger.log("&aHooked to &bPlaceholderAPI");
    }
    public void unregister() {
        placeholder.unregister();
    }

    public static String setPlaceholders(OfflinePlayer player, String text) {
        if(DonateCase.instance.papi != null) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }
}
