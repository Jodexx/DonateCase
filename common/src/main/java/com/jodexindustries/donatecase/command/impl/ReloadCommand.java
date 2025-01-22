package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.api.tools.DCTools;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand extends SubCommand.SubCommandBuilder implements SubCommandExecutor, SubCommandTabCompleter {

    private final DCAPI api;

    public ReloadCommand(DCAPI api) {
        super();
        name("reload");
        addon(api.getPlatform());
        permission(SubCommandType.ADMIN.permission);
        executor(this);
        tabCompleter(this);
        this.api = api;
    }

    @Override
    public boolean execute(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        if (args.length == 0) {
            load();
            sender.sendMessage(
                    DCTools.prefix(DCTools.rt(api.getConfig().getMessages().getString("config-reloaded")))
            );
        } else {
            if (args[0].equalsIgnoreCase("cache")) {
                api.clear();
                load();
                sender.sendMessage(DCTools.prefix(api.getConfig().getMessages().getString("config-cache-reloaded",
                        "&aReloaded all DonateCase Cache")));
            }
            return false;
        }
        return true;
    }

    @Override
    public List<String> getTabCompletions(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.add("cache");
        }
        return list;
    }

    private void load() {
        api.getConfig().load();
        api.getCaseLoader().load();
        if(api.getPlatform().getHologramManager() != null) api.getPlatform().getHologramManager().load();
    }

}
