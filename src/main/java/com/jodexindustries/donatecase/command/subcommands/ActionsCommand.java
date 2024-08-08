package com.jodexindustries.donatecase.command.subcommands;

import com.jodexindustries.donatecase.api.ActionManager;
import com.jodexindustries.donatecase.api.SubCommandManager;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.api.data.action.CaseAction;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Class for /dc actions subcommand implementation
 */
public class ActionsCommand implements SubCommandExecutor, SubCommandTabCompleter {

    public ActionsCommand(SubCommandManager manager) {
        SubCommand subCommand = manager.builder("actions")
                .executor(this)
                .tabCompleter(this)
                .type(SubCommandType.ADMIN)
                .build();
        manager.registerSubCommand(subCommand);
    }


    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        Map<String, List<CaseAction>> actionsMap = buildActionsMap();
        for (Map.Entry<String, List<CaseAction>> entry : actionsMap.entrySet()) {
            Tools.msgRaw(sender, "&6" + entry.getKey());
            for (CaseAction action : entry.getValue()) {
                Tools.msgRaw(sender, "&9- &a" + action.getName() + " &3- &2" + action.getDescription());
            }
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }

    /**
     * Key - Addon name
     * Value - list of CaseAction
     */
    private static Map<String, List<CaseAction>> buildActionsMap() {
        Map<String, List<CaseAction>> actionsMap = new HashMap<>();
        ActionManager.getRegisteredActions().forEach((name, caseAction) -> {
            String addon = caseAction.getAddon().getName();

            List<CaseAction> actions = actionsMap.getOrDefault(addon, new ArrayList<>());
            actions.add(caseAction);

            actionsMap.put(addon, actions);
        });

        return actionsMap;
    }
}
