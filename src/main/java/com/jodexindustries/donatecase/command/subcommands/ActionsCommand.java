package com.jodexindustries.donatecase.command.subcommands;

import com.jodexindustries.donatecase.api.ActionManager;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * Class for /dc actions subcommand implementation
 */
public class ActionsCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Map<String, List<String>> actionsMap = buildActionsMap();
        for (Map.Entry<String, List<String>> entry : actionsMap.entrySet()) {
            Tools.msgRaw(sender, "&6" + entry.getKey());
            for (String action : entry.getValue()) {
                Tools.msgRaw(sender, "&9- &a" + action);
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

    private static Map<String, List<String>> buildActionsMap() {
        Map<String, List<String>> actionsMap = new HashMap<>();
        ActionManager.getRegisteredActions().forEach((action, pair) -> {
            Addon addon = pair.getSecond();

            List<String> actions = actionsMap.getOrDefault(addon.getName(), new ArrayList<>());
            actions.add(action);

            actionsMap.put(addon.getName(), actions);
        });

        return actionsMap;
    }
}
