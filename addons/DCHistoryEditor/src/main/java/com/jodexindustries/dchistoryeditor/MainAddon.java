package com.jodexindustries.dchistoryeditor;


import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.InternalJavaAddon;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;

public final class MainAddon extends InternalJavaAddon {

    public final DCAPI api = DCAPI.getInstance();

    @Override
    public void onLoad() {
        MainCommand mainCommand = new MainCommand(this);

        SubCommand subCommand = SubCommand.builder()
                .name("historyeditor")
                .addon(this)
                .description("Edit case history")
                .args(new String[]{"&7(&aremove&7/&aset&7)", "&7(&acasetype&7)", "&7(&aindex&7/&aall&7)", "&7[&aparam&7]", "&7[&avalue&7]"})
                .executor(mainCommand)
                .tabCompleter(mainCommand)
                .permission(SubCommandType.ADMIN.permission)
                .build();

        api.getSubCommandManager().register(subCommand);
    }

    @Override
    public void onDisable() {
        api.getSubCommandManager().unregister("historyeditor");
    }

}
