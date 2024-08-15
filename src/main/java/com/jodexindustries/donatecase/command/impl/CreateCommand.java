package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.SubCommandManager;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for /dc create subcommand implementation
 */
public class CreateCommand implements SubCommandExecutor, SubCommandTabCompleter {

    public CreateCommand(SubCommandManager manager) {
        SubCommand subCommand = manager.builder("create")
                .executor(this)
                .tabCompleter(this)
                .type(SubCommandType.ADMIN)
                .build();
        manager.registerSubCommand(subCommand);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Location l = player.getTargetBlock(null, 5).getLocation().setDirection(player.getLocation().getDirection());
            if (args.length >= 2) {
                String caseType = args[0];
                String caseName = args[1];
                if (Case.hasCaseByType(caseType)) {
                    if (Case.hasCaseByLocation(l)) {
                        Tools.msg(sender, Case.getConfig().getLang().getString("case-already-created"));
                    } else {
                        if (!Case.hasCaseByCustomName(caseName)) {
                            Case.saveLocation(caseName, caseType, l);
                            Tools.msg(sender, Tools.rt(Case.getConfig().getLang().getString("case-added"),
                                    "%casename:" + caseName, "%casetype:" + caseType));
                        } else {
                            Tools.msg(sender, Tools.rt(Case.getConfig().getLang().getString("case-already-exist"),
                                    "%casename:" + caseName));
                        }
                    }
                } else {
                    Tools.msg(sender, Tools.rt(Case.getConfig().getLang().getString("case-does-not-exist"),
                            "%case:" + caseType));
                }
            } else {
                GlobalCommand.sendHelp(sender, label);
            }
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        List<String> list = new ArrayList<>(Case.getConfig().getCasesConfig().getCases().keySet());
        if(args.length >= 2) {
            return new ArrayList<>();
        }
        return list;
    }

}
