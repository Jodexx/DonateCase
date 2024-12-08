package com.jodexindustries.donatecase.tools;

import com.jodexindustries.donatecase.api.Case;
import org.bukkit.Bukkit;


public class Logger {

    public static void log(String msg) {
        Bukkit.getConsoleSender().sendMessage(DCToolsBukkit.rc("&3[&d" + Case.getInstance().getName() + "&3]&r " + msg));
    }

}