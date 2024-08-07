package com.jodexindustries.donatecase.command.subcommands;

import com.jodexindustries.donatecase.api.ActionManager;
import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.api.data.action.CaseAction;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * Class for /dc actions subcommand implementation
 */
public class ActionsCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Map<String, List<CaseAction>> actionsMap = buildActionsMap();
        for (Map.Entry<String, List<CaseAction>> entry : actionsMap.entrySet()) {
            Tools.msgRaw(sender, "&6" + entry.getKey());
            for (CaseAction action : entry.getValue()) {
                Tools.msgRaw(sender, "&9- &a" + action.getName() + " &3- &2" + action.getDescription());
            }
        }
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public SubCommandType getType() {
        return SubCommandType.ADMIN;
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
