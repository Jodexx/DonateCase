package com.jodexindustries.donatecase.command;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.SubCommandManager;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for /dc command implementation with subcommands
 */
public class GlobalCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (args.length == 0) {
            sendHelp(sender, label);
        } else {
            String subCommandName = args[0];
            SubCommand subCommand = SubCommandManager.registeredSubCommands.get(subCommandName);

            if (subCommand == null) {
                sendHelp(sender, label);
                return false;
            }

            SubCommandType type = subCommand.getType();

            if (type == null || type.hasPermission(sender))
                subCommand.execute(sender, label, Arrays.copyOfRange(args, 1, args.length));
            else Tools.msgRaw(sender, Tools.rt(Case.getConfig().getLang().getString("no-permission")));
        }

        return true;
    }

    public static void sendHelp(CommandSender sender, String label) {
        if (!sender.hasPermission("donatecase.player")) {
            Tools.msgRaw(sender, Tools.rt(Case.getConfig().getLang().getString("no-permission")));
            return;
        }

        Tools.msgRaw(sender, Tools.rt("&aDonateCase " + Case.getInstance().getDescription().getVersion() + " &7by &c_Jodex__"));

        if (!sender.hasPermission("donatecase.mod")) {
            sendHelpMessages(sender, "help-player", label);
        } else {
            sendHelpMessages(sender, "help", label);
        }

        if (Case.getConfig().getConfig().getBoolean("DonateCase.AddonsHelp", true)) {
            Map<String, List<Map<String, SubCommand>>> addonsMap = buildAddonsMap();
            if (Tools.isHasCommandForSender(sender, addonsMap)) {
                sendAddonHelpMessages(sender, addonsMap);
            }
        }
    }

    private static void sendHelpMessages(CommandSender sender, String path, String label) {
        for (String string : Case.getConfig().getLang().getStringList(path)) {
            Tools.msgRaw(sender, Tools.rt(string, "%cmd:" + label));
        }
    }

    private static Map<String, List<Map<String, SubCommand>>> buildAddonsMap() {
        Map<String, List<Map<String, SubCommand>>> addonsMap = new HashMap<>();
        SubCommandManager.registeredSubCommands.forEach((subCommandName, subCommand) -> {
            Addon addon = subCommand.getAddon();
            addonsMap.computeIfAbsent(addon.getName(), k -> new ArrayList<>())
                    .add(Collections.singletonMap(subCommandName, subCommand));
        });
        return addonsMap;
    }

    private static void sendAddonHelpMessages(CommandSender sender, Map<String, List<Map<String, SubCommand>>> addonsMap) {
        addonsMap.forEach((addon, commands) -> {
            if (!addon.equalsIgnoreCase("DonateCase") && Tools.isHasCommandForSender(sender, addonsMap, addon)) {
                String addonNameFormat = Case.getConfig().getLang().getString("help-addons.format.name");
                if (addonNameFormat != null && !addonNameFormat.isEmpty()) {
                    Tools.msgRaw(sender, Tools.rt(addonNameFormat, "%addon:" + addon));
                }

                commands.forEach(command -> command.forEach((commandName, subCommand) -> {
                    String description = subCommand.getDescription();
                    description = (description != null) ? Tools.rt(Case.getConfig().getLang().getString("help-addons.format.description"), "%description:" + description) : "";

                    StringBuilder argsBuilder = compileSubCommandArgs(subCommand.getArgs());
                    SubCommandType type = subCommand.getType();

                    if (type == null || type.hasPermission(sender)) {
                        Tools.msgRaw(sender, Tools.rt(Case.getConfig().getLang().getString("help-addons.format.command"),
                                "%cmd:" + commandName,
                                "%args:" + argsBuilder,
                                "%description:" + description
                        ));
                    }
                }));
            }
        });
    }

    private static @NotNull StringBuilder compileSubCommandArgs(String[] args) {
        StringBuilder builder = new StringBuilder();
        if (args != null) {
            for (String arg : args) {
                builder.append(arg).append(" ");
            }
            builder.setLength(builder.length() - 1);
        }
        return builder;
    }


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        List<String> value = new ArrayList<>();

        if (args.length == 1) {
            Map<String, SubCommand> subCommands = SubCommandManager.registeredSubCommands;

            for (Map.Entry<String, SubCommand> entry : subCommands.entrySet()) {
                String subCommandName = entry.getKey();
                SubCommand subCommand = entry.getValue();
                SubCommandType type = subCommand.getType();

                if (type == null || type.hasPermission(sender)) {
                    value.add(subCommandName);
                }
            }
        } else if (SubCommandManager.registeredSubCommands.containsKey(args[0])) {
            return SubCommandManager.getTabCompletionsForSubCommand(sender, args[0], label, Arrays.copyOfRange(args, 1, args.length));
        } else {
            return new ArrayList<>();
        }

        if (args[args.length - 1].isEmpty()) {
            Collections.sort(value);
            return value;
        }

        return value.stream()
                .filter(tmp -> tmp.startsWith(args[args.length - 1]))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * This method used in SetKeyCommand, DelKeyCommand and GiveKeyCommand classes
     * Not for API usable
     *
     * @param args tab completion args
     * @return list of completions
     */
    @NotNull
    public static List<String> resolveSDGCompletions(String[] args) {
        List<String> value = new ArrayList<>(Case.getConfig().getCasesConfig().getCases().keySet());
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(px -> px.startsWith(args[0])).collect(Collectors.toList()));
            return list;
        } else if (args.length >= 3) {
            return new ArrayList<>();
        }
        if (args[args.length - 1].isEmpty()) {
            list = value;
        } else {
            list.addAll(value.stream().filter(tmp -> tmp.startsWith(args[args.length - 1])).collect(Collectors.toList()));
        }
        return list;
    }
}
