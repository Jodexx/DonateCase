package com.jodexindustries.dchistoryeditor.bootstrap;

import com.jodexindustries.dchistoryeditor.commands.MainCommand;
import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import org.bukkit.command.CommandSender;

public class Bootstrap {

    private final DCAPIBukkit api;

    private final SubCommand<CommandSender> subCommand;

    public Bootstrap(DCAPIBukkit api) {
        this.api = api;

        MainCommand mainCommand = new MainCommand(api);
        this.subCommand = api.getSubCommandManager().builder("historyeditor")
                .description("Edit case history")
                .args(new String[]{"&7(&aremove&7/&aset&7)", "&7(&acasetype&7)", "&7(&aindex&7/&aall&7)", "&7[&aparam&7]", "&7[&avalue&7]"})
                .executor(mainCommand)
                .tabCompleter(mainCommand)
                .permission(SubCommandType.ADMIN.permission)
                .build();
    }

    public void load() {
        api.getSubCommandManager().registerSubCommand(subCommand);
    }

    public void unload() {
        api.getSubCommandManager().unregisterSubCommand("historyeditor");
    }
}
