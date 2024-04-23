package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.DonateCase;
import org.bukkit.Bukkit;


public class Logger {
    public static void log(String msg) {
        msg = Tools.rc("&3[&d" + DonateCase.instance.getName() + "&3]&r " + msg);
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    public static void debug(String msg) {
        msg = Tools.rc("&3[&d" + DonateCase.instance.getName() + "&3]&r " + msg);
        Bukkit.getConsoleSender().sendMessage("&7[&eDEBUG&7]&r" + msg);
    }
}