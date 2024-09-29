package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.SubCommandManager;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for /dc reload subcommand implementation
 */
public class ReloadCommand implements SubCommandExecutor, SubCommandTabCompleter {

    public static void register(SubCommandManager manager) {
        ReloadCommand command = new ReloadCommand();

        SubCommand subCommand = manager.builder("reload")
                .executor(command)
                .tabCompleter(command)
                .permission(SubCommandType.ADMIN.permission)
                .build();
        manager.registerSubCommand(subCommand);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (args.length == 0) {
            Case.getInstance().loadConfig();
            Case.getInstance().loadCases();
            if (Case.getInstance().hologramManager != null) Case.getInstance().hologramManager.removeAllHolograms();
            Case.getInstance().loadHolograms();
            Tools.msg(sender, Tools.rt(Case.getConfig().getLang().getString("config-reloaded")));
        } else {
            if (args[0].equalsIgnoreCase("cache")) {
                Case.cleanCache();
                Case.getInstance().loadConfig();
                Case.getInstance().loadCases();
                Tools.msg(sender, Tools.rt(Case.getConfig().getLang().getString("config-cache-reloaded", "&aReloaded all DonateCase Cache")));
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

}
