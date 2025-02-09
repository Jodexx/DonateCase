package com.jodexindustries.dchistoryeditor.bootstrap;

import com.jodexindustries.dchistoryeditor.commands.MainCommand;
import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;

public class Bootstrap {

    private final DCAPIBukkit api;

    private final SubCommand subCommand;

    public Bootstrap(DCAPIBukkit api) {
        this.api = api;

        MainCommand mainCommand = new MainCommand(api);
        this.subCommand = SubCommand.builder()
                .name("historyeditor")
                .description("Edit case history")
                .args(new String[]{"&7(&aremove&7/&aset&7)", "&7(&acasetype&7)", "&7(&aindex&7/&aall&7)", "&7[&aparam&7]", "&7[&avalue&7]"})
                .executor(mainCommand)
                .tabCompleter(mainCommand)
                .permission(SubCommandType.ADMIN.permission)
                .build();
    }

    public void load() {
        api.getSubCommandManager().register(subCommand);
    }

    public void unload() {
        api.getSubCommandManager().unregister("historyeditor");
    }
}
