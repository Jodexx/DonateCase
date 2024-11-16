package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.manager.SubCommandManager;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.ToolsBukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.jodexindustries.donatecase.DonateCase.instance;

/**
 * Class for /dc create subcommand implementation
 */
public class CreateCommand implements SubCommandExecutor<CommandSender>, SubCommandTabCompleter<CommandSender> {

    public static void register(SubCommandManager<CommandSender> manager) {
        CreateCommand command = new CreateCommand();

        SubCommand<CommandSender> subCommand = manager.builder("create")
                .executor(command)
                .tabCompleter(command)
                .permission(SubCommandType.ADMIN.permission)
                .build();
        manager.registerSubCommand(subCommand);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location l = player.getTargetBlock(null, 5).getLocation().setDirection(player.getLocation().getDirection());
            if (args.length >= 2) {
                String caseType = args[0];
                String caseName = args[1];
                if (instance.api.getCaseManager().hasCaseByType(caseType)) {
                    if (Case.hasCaseByLocation(l)) {
                        ToolsBukkit.msg(sender, Case.getConfig().getLang().getString("case-already-created"));
                    } else {
                        if (!Case.hasCaseByCustomName(caseName)) {
                            Case.saveLocation(caseName, caseType, l);
                            ToolsBukkit.msg(sender, Tools.rt(Case.getConfig().getLang().getString("case-added"),
                                    "%casename:" + caseName, "%casetype:" + caseType));
                        } else {
                            ToolsBukkit.msg(sender, Tools.rt(Case.getConfig().getLang().getString("case-already-exist"),
                                    "%casename:" + caseName));
                        }
                    }
                } else {
                    ToolsBukkit.msg(sender, Tools.rt(Case.getConfig().getLang().getString("case-does-not-exist"),
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
        if (args.length >= 2) {
            return new ArrayList<>();
        }
        return list;
    }

}
