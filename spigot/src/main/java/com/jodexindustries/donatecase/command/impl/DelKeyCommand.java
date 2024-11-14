package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.manager.SubCommandManager;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.ToolsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.jodexindustries.donatecase.command.GlobalCommand.resolveSDGCompletions;

/**
 * Class for /dc delkey subcommand implementation
 */
public class DelKeyCommand implements SubCommandExecutor<CommandSender>, SubCommandTabCompleter<CommandSender> {

    public static void register(SubCommandManager<CommandSender> manager) {
        DelKeyCommand command = new DelKeyCommand();

        SubCommand<CommandSender> subCommand = manager.builder("delkey")
                .executor(command)
                .tabCompleter(command)
                .permission(SubCommandType.ADMIN.permission)
                .build();
        manager.registerSubCommand(subCommand);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(Case.getInstance(), () -> {
            if (args.length == 0) {
                GlobalCommand.sendHelp(sender, label);
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("all")) {
                    Case.getInstance().api.getCaseKeyManager().removeAllKeys().thenAcceptAsync(status -> ToolsBukkit.msg(sender, Tools.rt(Case.getConfig().getLang().getString("all-keys-cleared"))));
                }
            } else {
                String player = args[0];
                String caseType = args[1];
                if (Case.hasCaseByType(caseType)) {
                    CaseDataBukkit data = Case.getCase(caseType);
                    if (data == null) return;
                    int keys;
                    if (args.length == 2) {
                        keys = Case.getInstance().api.getCaseKeyManager().getKeys(caseType, player);
                        Case.getInstance().api.getCaseKeyManager().setKeys(caseType, player, 0);
                    } else {
                        try {
                            keys = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            Tools.msgRaw(sender, Tools.rt(Case.getConfig().getLang().getString("number-format-exception"), "%string:" + args[2]));
                            return;
                        }

                        Case.getInstance().api.getCaseKeyManager().removeKeys(caseType, player, keys);
                    }
                    ToolsBukkit.msg(sender, Tools.rt(Case.getConfig().getLang().getString("keys-cleared"),
                            "%player:" + player, "%casetitle:" + data.getCaseTitle(),
                            "%casedisplayname:" + data.getCaseDisplayName(), "%case:" + caseType, "%key:" + keys));
                } else {
                    ToolsBukkit.msg(sender, Tools.rt(Case.getConfig().getLang().getString("case-does-not-exist"), "%case:" + caseType));
                }
            }
        });
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return resolveSDGCompletions(args);
    }

}
