package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.SubCommandManager;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * Class for /dc opencase subcommand implementation
 */
public class OpenCaseCommand implements SubCommandExecutor, SubCommandTabCompleter {

    public static void register(SubCommandManager manager) {
        OpenCaseCommand command = new OpenCaseCommand();

        SubCommand subCommand = manager.builder("opencase")
                .executor(command)
                .tabCompleter(command)
                .permission(SubCommandType.PLAYER.permission)
                .build();
        manager.registerSubCommand(subCommand);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            String playerName = sender.getName();
            Player player = (Player) sender;
            if (args.length >= 1) {
                String caseName = args[0];
                if (Case.hasCaseByType(caseName)) {
                    Case.getKeysAsync(caseName, playerName).thenAcceptAsync((keys) -> {
                        if (keys >= 1) {
                            Case.removeKeys(caseName, playerName, 1);
                            CaseData data = Case.getCase(caseName);
                            if (data == null) return;
                            CaseData.Item winGroup = data.getRandomItem();
                            Case.animationPreEnd(data, player, player.getLocation(), winGroup);
                        } else {
                            Tools.msg(player, Case.getConfig().getLang().getString("no-keys"));
                        }
                    });
                } else {
                    Tools.msg(sender, Tools.rt(Case.getConfig().getLang().getString("case-does-not-exist"), "%case:" + caseName));
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
