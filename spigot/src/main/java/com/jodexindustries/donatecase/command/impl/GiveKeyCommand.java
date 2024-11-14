package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.manager.SubCommandManager;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.ToolsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.jodexindustries.donatecase.command.GlobalCommand.resolveSDGCompletions;

/**
 * Class for /dc givekey subcommand implementation
 */
public class GiveKeyCommand implements SubCommandExecutor<CommandSender>, SubCommandTabCompleter<CommandSender> {

    public static void register(SubCommandManager<CommandSender> manager) {
        GiveKeyCommand command = new GiveKeyCommand();

        SubCommand<CommandSender> subCommand = manager.builder("givekey")
                .executor(command)
                .tabCompleter(command)
                .permission(SubCommandType.MODER.permission)
                .build();
        manager.registerSubCommand(subCommand);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (args.length >= 3) {
            String player = args[0];
            String caseName = args[1];
            Player target = Bukkit.getPlayer(player);
            int keys;
            try {
                keys = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                Tools.msgRaw(sender, Tools.rt(Case.getConfig().getLang().getString("number-format-exception"), "%string:" + args[2]));
                return;
            }
            if (Case.hasCaseByType(caseName)) {
                CaseDataBukkit data = Case.getCase(caseName);
                if (data == null) return;
                Case.getInstance().api.getCaseKeyManager().addKeys(caseName, player, keys).thenAcceptAsync(status -> {
                    if(status == DatabaseStatus.COMPLETE) {
                        ToolsBukkit.msg(sender, Tools.rt(Case.getConfig().getLang().getString("keys-given"),
                                "%player:" + player, "%key:" + keys, "%casetitle:" + data.getCaseTitle(),
                                "%casedisplayname:" + data.getCaseDisplayName(), "%case:" + caseName));

                        if (args.length < 4 || !args[3].equalsIgnoreCase("-s")) {
                            ToolsBukkit.msg(target, Tools.rt(Case.getConfig().getLang().getString("keys-given-target"),
                                    "%player:" + player, "%key:" + keys, "%casetitle:" + data.getCaseTitle(),
                                    "%casedisplayname:" + data.getCaseDisplayName(), "%case:" + caseName));
                        }
                    }
                });
            } else {
                ToolsBukkit.msg(sender, Tools.rt(Case.getConfig().getLang().getString("case-does-not-exist"), "%case:" + caseName));
            }
        } else {
            GlobalCommand.sendHelp(sender, label);
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return resolveSDGCompletions(args);
    }
}