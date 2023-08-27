package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.dc.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

public class Logger {
    public static void log(String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', "&3[&d" + Main.instance.getName() + "&3]&r " + msg);
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    public static void debug(String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', "&3[&d" + Main.instance.getName() + "&3]&r " + msg);
        Bukkit.getConsoleSender().sendMessage("&7[&eDEBUG&7]&r" + msg);
    }
}