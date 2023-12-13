package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.dc.Main;
import org.bukkit.Bukkit;

import static com.jodexindustries.donatecase.dc.Main.t;

public class Logger {
    public static void log(String msg) {
        msg = t.rc("&3[&d" + Main.instance.getName() + "&3]&r " + msg);
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    public static void debug(String msg) {
        msg = t.rc("&3[&d" + Main.instance.getName() + "&3]&r " + msg);
        Bukkit.getConsoleSender().sendMessage("&7[&eDEBUG&7]&r" + msg);
    }
}