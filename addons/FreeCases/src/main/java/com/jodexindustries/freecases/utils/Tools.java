package com.jodexindustries.freecases.utils;

import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.freecases.bootstrap.Main;
import com.jodexindustries.freecases.commands.MainCommand;
import com.jodexindustries.freecases.config.Config;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class Tools {
    private final Main main;
    private final Config config;

    private final Placeholder placeholder;

    public Tools(Main main) {
        this.main = main;
        this.config = new Config(main);

        this.placeholder = Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") ? new Placeholder(this) : null;
    }

    public void load() {
        MainCommand executor = new MainCommand(this);
        SubCommand<CommandSender> subCommand = main.getDCAPI().getSubCommandManager().builder("free")
                .executor(executor)
                .permission(SubCommandType.PLAYER.permission)
                .description("Get free case")
                .build();

        main.getDCAPI().getSubCommandManager().register(subCommand);

        if(placeholder != null) placeholder.register();
    }

    public void unload() {
        main.getDCAPI().getSubCommandManager().unregister("free");

        if(placeholder != null) placeholder.unregister();
    }

    public String setPlaceholders(OfflinePlayer player, String text) {
        return placeholder == null ? text : PlaceholderAPI.setPlaceholders(player, text);
    }

    public String formatTime(long time) {
        String secondChar = ChatColor.translateAlternateColorCodes('&',
                config.getConfig().getString("Second", ""));
        String minuteChar = ChatColor.translateAlternateColorCodes('&',
                config.getConfig().getString("Minute", ""));
        String hourChar = ChatColor.translateAlternateColorCodes('&',
                config.getConfig().getString("Hour", ""));
        long hours = time / 3600;
        long minutes = (time / 60) - hours * 60;
        long seconds = time % 60 % 60;
        String hour = hours + hourChar;
        String minute = minutes + minuteChar;
        String second = seconds + secondChar;
        if (seconds == 0) second = "";
        if (hours == 0) {
            hour = "";
            if (minutes == 0) minute = "";
        }
        return hour + minute + second;
    }

    public Main getMain() {
        return main;
    }

    public Config getConfig() {
        return config;
    }
}
