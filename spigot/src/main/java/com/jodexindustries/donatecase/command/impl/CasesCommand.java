package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.impl.managers.SubCommandManagerImpl;
import com.jodexindustries.donatecase.api.data.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for /dc cases subcommand implementation
 */
public class CasesCommand implements SubCommandExecutor<CommandSender>, SubCommandTabCompleter<CommandSender> {

    public static void register(SubCommandManagerImpl manager) {
        CasesCommand command = new CasesCommand();

        SubCommand subCommand = manager.builder("cases")
                .executor(command)
                .tabCompleter(command)
                .permission(SubCommandType.MODER.permission)
                .build();
        manager.registerSubCommand(subCommand);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        int num = 0;
        for (CaseDataBukkit data : Case.caseData.values()) {
            num++;
            Tools.msgRaw(sender, Tools.rt(Case.getConfig().getLang().getString("list-of-cases"),
                    "%casename:" + data.getCaseType(), "%num:" + num,
                    "%casedisplayname:" + data.getCaseDisplayName(),
                    "%casetitle:" + data.getCaseTitle()));
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }

}
