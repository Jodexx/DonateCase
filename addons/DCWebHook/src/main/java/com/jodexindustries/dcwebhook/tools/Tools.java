package com.jodexindustries.dcwebhook.tools;

import com.jodexindustries.dcwebhook.bootstrap.Main;
import com.jodexindustries.dcwebhook.commands.MainCommand;
import com.jodexindustries.dcwebhook.config.Config;
import com.jodexindustries.dcwebhook.events.EventListener;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class Tools {
    private final Main main;
    private final Config config;

    public Tools(Main main) {
        this.main = main;
        this.config = new Config(main);
    }

    public void load() {
        String ver = Case.getInstance().getDescription().getVersion();
        int intVer = com.jodexindustries.donatecase.tools.Tools.getPluginVersion(ver);
        if (intVer < 2245) {
            main.getLogger().log(Level.SEVERE, "Unsupported version of the DonateCase! Use >2.2.4.5");
            return;
        }

        Bukkit.getServer().getPluginManager().registerEvents(new EventListener(this), main.getPlugin());
        MainCommand mainCommand = new MainCommand(this);
        SubCommand subCommand = main.getCaseAPI().getSubCommandManager().builder("webhook")
                .executor(mainCommand)
                .tabCompleter(mainCommand)
                .permission(SubCommandType.ADMIN.permission)
                .args(new String[]{"reload"})
                .description("Reload addon config")
                .build();

        main.getCaseAPI().getSubCommandManager().registerSubCommand(subCommand);
    }

    public void unload() {
        main.getCaseAPI().getSubCommandManager().unregisterSubCommand("webhook");
    }

    public Main getMain() {
        return main;
    }

    public Config getConfig() {
        return config;
    }
}
