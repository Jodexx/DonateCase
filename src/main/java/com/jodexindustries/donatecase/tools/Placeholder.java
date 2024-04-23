package com.jodexindustries.donatecase.tools;

import java.text.NumberFormat;

import com.jodexindustries.donatecase.DonateCase;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class Placeholder extends PlaceholderExpansion {

    public @NotNull String getAuthor() {
        return "JodexIndustries";
    }

    public @NotNull String getIdentifier() {
        return "DonateCase";
    }

    public @NotNull String getVersion() {
        return DonateCase.instance.getDescription().getVersion();
    }

    public boolean persist() {
        return true;
    }

    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if(params.startsWith("keys")) {
            String[] parts = params.split("_", 2);
            int keys = 0;
            for (String caseName : DonateCase.api.getCases().keySet()) {
                keys += DonateCase.api.getKeys(caseName, player.getName());
            }
            if(parts.length == 1) {
                return String.valueOf(keys);
            } else if(parts[1].equalsIgnoreCase("format")) {
                return NumberFormat.getNumberInstance().format(keys);
            }
        }
        if (params.startsWith("keys_")) {
            String[] parts = params.split("_", 3);
            int s = DonateCase.api.getKeys(parts[1], player.getName());
            if(parts.length == 2) {
                return String.valueOf(s);
            } else if(parts[2].equalsIgnoreCase("format")) {
                return NumberFormat.getNumberInstance().format(s);
            } else {
                return String.valueOf(s);
            }
        } else {
            return null;
        }
    }
}
