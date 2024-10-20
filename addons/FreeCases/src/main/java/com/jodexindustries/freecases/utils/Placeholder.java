package com.jodexindustries.freecases.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class Placeholder extends PlaceholderExpansion {
    private final Tools t;

    public Placeholder(Tools t) {
        this.t = t;
    }


    @Override
    public @NotNull String getAuthor() {
        return "_Jodex__";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "FreeCases";
    }

    @Override
    public @NotNull String getVersion() {
        return t.getMain().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.equalsIgnoreCase("time")) {
            long timeStamp = (CooldownManager.getCooldown(player.getUniqueId()) + (t.getConfig().getConfig().getLong("TimeToPlay") * 1000L) ) - System.currentTimeMillis();
            long time = Duration.ofMillis(timeStamp).getSeconds();
            String result = t.formatTime(time);
            if(time > 0) {
                return result;
            } else {
                return ChatColor.translateAlternateColorCodes('&',
                        t.getConfig().getConfig().getString("Received", ""));
            }
        }

        return null;
    }
}
