package com.jodexindustries.dcwebhook.tools;

import com.jodexindustries.dcwebhook.bootstrap.Main;
import com.jodexindustries.dcwebhook.commands.MainCommand;
import com.jodexindustries.dcwebhook.config.Config;
import com.jodexindustries.dcwebhook.events.EventListener;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class Tools {
    private final Main main;
    private final Config config;

    public Tools(Main main) {
        this.main = main;
        this.config = new Config(main);
    }

    public void load() {
        Bukkit.getServer().getPluginManager().registerEvents(new EventListener(this), main.getPlugin());
        MainCommand mainCommand = new MainCommand(this);
        SubCommand<CommandSender> subCommand = main.getDCAPI().getSubCommandManager().builder("webhook")
                .executor(mainCommand)
                .tabCompleter(mainCommand)
                .permission(SubCommandType.ADMIN.permission)
                .args(new String[]{"reload"})
                .description("Reload addon config")
                .build();

        main.getDCAPI().getSubCommandManager().registerSubCommand(subCommand);
    }

    public void unload() {
        main.getDCAPI().getSubCommandManager().unregisterSubCommand("webhook");
    }

    public Main getMain() {
        return main;
    }

    public Config getConfig() {
        return config;
    }
}
