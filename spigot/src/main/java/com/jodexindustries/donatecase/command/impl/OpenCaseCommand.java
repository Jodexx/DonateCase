package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.api.manager.SubCommandManager;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.ToolsBukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * Class for /dc opencase subcommand implementation
 */
public class OpenCaseCommand implements SubCommandExecutor<CommandSender>, SubCommandTabCompleter<CommandSender> {

    public static void register(SubCommandManager<CommandSender> manager) {
        OpenCaseCommand command = new OpenCaseCommand();

        SubCommand<CommandSender> subCommand = manager.builder("opencase")
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
                    Case.getInstance().api.getCaseKeyManager().getKeysAsync(caseName, playerName).thenAcceptAsync((keys) -> {
                        if (keys >= 1) {
                            Case.getInstance().api.getCaseKeyManager().removeKeys(caseName, playerName, 1);
                            CaseDataBukkit data = Case.getCase(caseName);
                            if (data == null) return;
                            CaseDataItem<CaseDataMaterialBukkit> winGroup = data.getRandomItem();
                            Case.animationPreEnd(data, player, player.getLocation(), winGroup);
                        } else {
                            ToolsBukkit.msg(player, Case.getConfig().getLang().getString("no-keys"));
                        }
                    });
                } else {
                    ToolsBukkit.msg(sender, Tools.rt(Case.getConfig().getLang().getString("case-does-not-exist"), "%case:" + caseName));
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
