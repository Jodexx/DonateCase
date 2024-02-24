package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.DonateCase;
import org.bukkit.Bukkit;

import static com.jodexindustries.donatecase.DonateCase.t;

public class Logger {
    public static void log(String msg) {
        msg = t.rc("&3[&d" + DonateCase.instance.getName() + "&3]&r " + msg);
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    public static void debug(String msg) {
        msg = t.rc("&3[&d" + DonateCase.instance.getName() + "&3]&r " + msg);
        Bukkit.getConsoleSender().sendMessage("&7[&eDEBUG&7]&r" + msg);
    }
}