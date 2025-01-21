package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand extends SubCommand {

    private final DCAPI api;

    public ReloadCommand(DCAPI api) {
        super("reload", api.getPlatform());
        setPermission(SubCommandType.ADMIN.permission);
        this.api = api;
    }

    @Override
    public boolean execute(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        if (args.length == 0) {
            load();
            api.getPlatform().getTools().msg(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("config-reloaded")));
        } else {
            if (args[0].equalsIgnoreCase("cache")) {
                Case.cleanCache();
                load();
                Case.getInstance().loadDatabase();
                api.getPlatform().getTools().msg(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("config-cache-reloaded",
                        "&aReloaded all DonateCase Cache")));
            }
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.add("cache");
        }
        return list;
    }

    private void load() {
        api.getConfig().load();
        Case.getInstance().loader.load();
        Case.getInstance().loadHolograms();
    }

}
