package com.jodexindustries.donatecase.command;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.tools.Pair;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
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
            Pair<SubCommand, Addon> pair = Case.getInstance().api.getSubCommandManager().getSubCommands().get(subCommandName);

            if (pair == null) {
                sendHelp(sender, label);
                return true;
            }

            SubCommand subCommand = pair.getFirst();
            if (subCommand == null) {
                sendHelp(sender, label);
                return true;
            }

            SubCommandType type = subCommand.getType();
            boolean hasAdminPermission = sender.hasPermission("donatecase.admin");
            boolean hasModPermission = sender.hasPermission("donatecase.mod");
            boolean hasPlayerPermission = sender.hasPermission("donatecase.player");

            if (type == SubCommandType.ADMIN && hasAdminPermission ||
                    type == SubCommandType.MODER && (hasModPermission || hasAdminPermission) ||
                    (type == SubCommandType.PLAYER || type == null) && (hasPlayerPermission || hasModPermission || hasAdminPermission)) {
                subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
            } else {
                Tools.msgRaw(sender, Tools.rt(Case.getCustomConfig().getLang().getString("no-permission")));
            }
        }
        return true;
    }

    public static void sendHelp(CommandSender sender, String label) {
        if (!sender.hasPermission("donatecase.player")) {
            Tools.msgRaw(sender, Tools.rt(Case.getCustomConfig().getLang().getString("no-permission")));
            return;
        }

        Tools.msgRaw(sender, Tools.rt("&aDonateCase " + Case.getInstance().getDescription().getVersion() + " &7by &c_Jodex__"));

        boolean isAdmin = sender.hasPermission("donatecase.admin");
        boolean isMod = sender.hasPermission("donatecase.mod");

        if (!isAdmin && !isMod) {
            sendHelpMessages(sender, "help-player", label);
        } else {
            sendHelpMessages(sender, "help", label);
        }

        if (Case.getCustomConfig().getConfig().getBoolean("DonatCase.AddonsHelp", true)) {
            Map<String, List<Map<String, SubCommand>>> addonsMap = buildAddonsMap();
            if (Tools.isHasCommandForSender(sender, addonsMap)) {
                sendAddonHelpMessages(sender, addonsMap, isAdmin, isMod);
            }
        }
    }

    private static void sendHelpMessages(CommandSender sender, String path, String label) {
        for (String string : Case.getCustomConfig().getLang().getStringList(path)) {
            Tools.msgRaw(sender, Tools.rt(string, "%cmd:" + label));
        }
    }

    private static Map<String, List<Map<String, SubCommand>>> buildAddonsMap() {
        Map<String, List<Map<String, SubCommand>>> addonsMap = new HashMap<>();
        Case.getInstance().api.getSubCommandManager().getSubCommands().forEach((subCommandName, pair) -> {
            SubCommand subCommand = pair.getFirst();
            Addon addon = pair.getSecond();
            addonsMap.computeIfAbsent(addon.getName(), k -> new ArrayList<>())
                    .add(Collections.singletonMap(subCommandName, subCommand));
        });
        return addonsMap;
    }

    private static void sendAddonHelpMessages(CommandSender sender, Map<String, List<Map<String, SubCommand>>> addonsMap, boolean isAdmin, boolean isMod) {
        addonsMap.forEach((addon, commands) -> {
            if (!addon.equalsIgnoreCase("DonateCase") && Tools.isHasCommandForSender(sender, addonsMap, addon)) {
                String addonNameFormat = Case.getCustomConfig().getLang().getString("help-addons.format.name");
                if (addonNameFormat != null && !addonNameFormat.isEmpty()) {
                    Tools.msgRaw(sender, Tools.rt(addonNameFormat, "%addon:" + addon));
                }

                commands.forEach(command -> command.forEach((commandName, subCommand) -> {
                    String description = subCommand.getDescription();
                    description = (description != null) ? Tools.rt(Case.getCustomConfig().getLang().getString("help-addons.format.description"), "%description:" + description) : "";

                    StringBuilder argsBuilder = compileSubCommandArgs(subCommand.getArgs());

                    if (hasPermissionForSubCommand(sender, isAdmin, isMod, subCommand)) {
                        Tools.msgRaw(sender, Tools.rt(Case.getCustomConfig().getLang().getString("help-addons.format.command"),
                                "%cmd:" + commandName,
                                "%args:" + argsBuilder,
                                "%description:" + description
                        ));
                    }
                }));
            }
        });
    }

    private static boolean hasPermissionForSubCommand(CommandSender sender, boolean isAdmin, boolean isMod, SubCommand subCommand) {
        SubCommandType type = subCommand.getType();
        return isAdmin || isMod && (type == SubCommandType.MODER || type == SubCommandType.PLAYER || type == null)
                || sender.hasPermission("donatecase.player") && !isMod && (type == SubCommandType.PLAYER || type == null);
    }



    private static @NotNull StringBuilder compileSubCommandArgs(String[] args) {
        StringBuilder builder = new StringBuilder();
        if(args != null) {
            for (int i = 0; i < args.length; i++) {
                builder.append(args[i]);
                if (i < args.length - 1) {
                    builder.append(" ");
                }
            }
        }
        return builder;
    }


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        List<String> value = new ArrayList<>();

        if (args.length == 1) {
            boolean isAdmin = sender.hasPermission("donatecase.admin");
            boolean isMod = sender.hasPermission("donatecase.mod");
            boolean isPlayer = sender.hasPermission("donatecase.player");

            Map<String, Pair<SubCommand, Addon>> subCommands = Case.getInstance().api.getSubCommandManager().getSubCommands();

            for (Map.Entry<String, Pair<SubCommand, Addon>> entry : subCommands.entrySet()) {
                String subCommandName = entry.getKey();
                SubCommand subCommand = entry.getValue().getFirst();
                SubCommandType type = subCommand.getType();

                if (isAdmin || isMod && (type == SubCommandType.MODER || type == SubCommandType.PLAYER || type == null) || isPlayer && !isMod && (type == SubCommandType.PLAYER || type == null)) {
                    value.add(subCommandName);
                }
            }
        } else if (Case.getInstance().api.getSubCommandManager().getSubCommands().containsKey(args[0])) {
            return Case.getInstance().api.getSubCommandManager().getTabCompletionsForSubCommand(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
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


}
