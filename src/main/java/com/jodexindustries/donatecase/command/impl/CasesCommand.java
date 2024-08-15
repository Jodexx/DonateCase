package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.SubCommandManager;
import com.jodexindustries.donatecase.api.data.CaseData;
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
 * Class for /dc cases subcommand implementation
 */
public class CasesCommand implements SubCommandExecutor, SubCommandTabCompleter {

    public CasesCommand(SubCommandManager manager) {
        SubCommand subCommand = manager.builder("cases")
                .executor(this)
                .tabCompleter(this)
                .type(SubCommandType.MODER)
                .build();
        manager.registerSubCommand(subCommand);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        int num = 0;
        for (String caseName : Case.getConfig().getCasesConfig().getCases().keySet()) {
            num++;
            CaseData data = Case.getCase(caseName);
            if(data == null) return;
            String caseTitle = data.getCaseTitle();
            String caseDisplayName = data.getCaseDisplayName();

            Tools.msgRaw(sender, Tools.rt(Case.getConfig().getLang().getString("list-of-cases"), "%casename:" + caseName, "%num:" + num, "%casedisplayname:" + caseDisplayName, "%casetitle:" + caseTitle ));
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }

}
