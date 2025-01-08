package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.manager.SubCommandManager;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.jodexindustries.donatecase.DonateCase.instance;
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
                    Case.getInstance().api.getCaseKeyManager().removeAllKeys().thenAcceptAsync(status -> instance.api.getTools().msg(sender, DCToolsBukkit.rt(instance.api.getConfig().getLang().getString("all-keys-cleared"))));
                }
            } else {
                String playerName = args[0];
                String caseType = args[1];
                if(!instance.api.getTools().isValidPlayerName(playerName)) {
                    instance.api.getTools().msg(sender, DCToolsBukkit.rt(instance.api.getConfig().getLang().getString("player-not-found"), "%player:" + playerName));
                    return;
                }
                if (instance.api.getCaseManager().hasCaseByType(caseType)) {
                    CaseDataBukkit data = instance.api.getCaseManager().getCase(caseType);
                    if (data == null) return;
                    int keys;
                    if (args.length == 2) {
                        keys = Case.getInstance().api.getCaseKeyManager().getKeys(caseType, playerName);
                        Case.getInstance().api.getCaseKeyManager().setKeys(caseType, playerName, 0);
                    } else {
                        try {
                            keys = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            DCToolsBukkit.msgRaw(sender, DCToolsBukkit.rt(instance.api.getConfig().getLang().getString("number-format-exception"), "%string:" + args[2]));
                            return;
                        }

                        Case.getInstance().api.getCaseKeyManager().removeKeys(caseType, playerName, keys);
                    }
                    instance.api.getTools().msg(sender, DCToolsBukkit.rt(instance.api.getConfig().getLang().getString("keys-cleared"),
                            "%player:" + playerName, "%casetitle:" + data.getCaseTitle(),
                            "%casedisplayname:" + data.getCaseDisplayName(), "%case:" + caseType, "%key:" + keys));
                } else {
                    instance.api.getTools().msg(sender, DCToolsBukkit.rt(instance.api.getConfig().getLang().getString("case-does-not-exist"), "%case:" + caseType));
                }
            }
        });
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return resolveSDGCompletions(args);
    }

}
