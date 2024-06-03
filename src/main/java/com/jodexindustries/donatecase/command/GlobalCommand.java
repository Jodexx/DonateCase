package com.jodexindustries.donatecase.command;

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

import static com.jodexindustries.donatecase.DonateCase.*;

public class GlobalCommand implements CommandExecutor, TabCompleter {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (args.length == 0) {
            sendHelp(sender, label);
        } else {
            String subCommandName = args[0];
            Pair<SubCommand, Addon> pair = api.getSubCommandManager().getSubCommands().get(subCommandName);

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
                Tools.msg_(sender, Tools.rt(customConfig.getLang().getString("NoPermission")));
            }
        }
        return true;
    }

    public static void sendHelp(CommandSender sender, String label) {
        if(sender.hasPermission("donatecase.player")) {
            Tools.msg_(sender, Tools.rt("&aDonateCase " + instance.getDescription().getVersion() + " &7by &c_Jodex__"));
            if (!sender.hasPermission("donatecase.mod")) {
                for (String string : customConfig.getLang().getStringList("HelpPlayer")) {
                    Tools.msg_(sender, Tools.rt(string, "%cmd:" + label));
                }
            } else if (sender.hasPermission("donatecase.mod") || sender.hasPermission("donatecase.admin")) {
                for (String string : customConfig.getLang().getStringList("Help")) {
                    Tools.msg_(sender, Tools.rt(string, "%cmd:" + label));
                }
            }
            if (customConfig.getConfig().getBoolean("DonatCase.AddonsHelp", true)) {
                Map<String, List<Map<String, SubCommand>>> addonsMap = new HashMap<>();
                for (String subCommandName : api.getSubCommandManager().getSubCommands().keySet()) {
                    List<Map<String, SubCommand>> list = new ArrayList<>();
                    Map<String, SubCommand> commandMap = new HashMap<>();
                    Pair<SubCommand, Addon> pair = api.getSubCommandManager().getSubCommands().get(subCommandName);
                    SubCommand subCommand = pair.getFirst();
                    Addon addon = pair.getSecond();
                    commandMap.put(subCommandName, subCommand);
                    list.add(commandMap);
                    addonsMap.put(addon.getName(), list);
                }
                if (Tools.isHasCommandForSender(sender, addonsMap)) {
                    for (String addon : addonsMap.keySet()) {
                        if (addon.equalsIgnoreCase("DonateCase")) continue;
                        if (Tools.isHasCommandForSender(sender, addonsMap, addon)) {
                            if (customConfig.getLang().getString("HelpAddons.Format.AddonName") != null && !customConfig.getLang().getString("HelpAddons.Format.AddonName", "").isEmpty()) {
                                Tools.msg_(sender, Tools.rt(customConfig.getLang().getString("HelpAddons.Format.AddonName"), "%addon:" + addon));
                            }
                            List<Map<String, SubCommand>> commands = addonsMap.get(addon);
                            for (Map<String, SubCommand> command : commands) {
                                for (String commandName : command.keySet()) {
                                    SubCommand subCommand = command.get(commandName);
                                    String description = subCommand.getDescription();
                                    if (description == null) {
                                        description = "";
                                    } else {
                                        description = Tools.rt(customConfig.getLang().getString("HelpAddons.Format.AddonDescription"), "%description:" + description);
                                    }
                                    StringBuilder builder = compileSubCommandArgs(subCommand.getArgs());
                                    if (sender.hasPermission("donatecase.admin")) {
                                        if (subCommand.getType() == SubCommandType.ADMIN || subCommand.getType() == SubCommandType.MODER || subCommand.getType() == SubCommandType.PLAYER || subCommand.getType() == null) {
                                            Tools.msg_(sender, Tools.rt(customConfig.getLang().getString("HelpAddons.Format.AddonCommand"),
                                                    "%cmd:" + commandName,
                                                    "%args:" + builder,
                                                    "%description:" + description
                                            ));
                                        }
                                    } else if (sender.hasPermission("donatecase.mod") && !sender.hasPermission("donatecase.admin")) {
                                        if (subCommand.getType() == SubCommandType.MODER || subCommand.getType() == SubCommandType.PLAYER || subCommand.getType() == null) {
                                            Tools.msg_(sender, Tools.rt(customConfig.getLang().getString("HelpAddons.Format.AddonCommand"),
                                                    "%cmd:" + commandName,
                                                    "%args:" + builder,
                                                    "%description:" + description
                                            ));
                                        }
                                    } else if (sender.hasPermission("donatecase.player") && !sender.hasPermission("donatecase.admin") && !sender.hasPermission("donatecase.mod")) {
                                        if (subCommand.getType() == SubCommandType.PLAYER || subCommand.getType() == null) {
                                            Tools.msg_(sender, Tools.rt(customConfig.getLang().getString("HelpAddons.Format.AddonCommand"),
                                                    "%cmd:" + commandName,
                                                    "%args:" + builder,
                                                    "%description:" + description
                                            ));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Tools.msg_(sender, Tools.rt(customConfig.getLang().getString("NoPermission")));
        }
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
        List<String> list;
        List<String> value = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("donatecase.admin")) {
                for (String subCommandName : api.getSubCommandManager().getSubCommands().keySet()) {
                    SubCommand subCommand = api.getSubCommandManager().getSubCommands().get(subCommandName).getFirst();
                    if (subCommand.getType() == SubCommandType.ADMIN || subCommand.getType() == SubCommandType.MODER || subCommand.getType() == null || subCommand.getType() == SubCommandType.PLAYER) {
                        value.add(subCommandName);
                    }
                }
            } else if (sender.hasPermission("donatecase.mod") && !sender.hasPermission("donatecase.admin")) {
                for (String subCommandName : api.getSubCommandManager().getSubCommands().keySet()) {
                    SubCommand subCommand = api.getSubCommandManager().getSubCommands().get(subCommandName).getFirst();
                    if (subCommand.getType() == SubCommandType.MODER || subCommand.getType() == SubCommandType.PLAYER || subCommand.getType() == null) {
                        value.add(subCommandName);
                    }
                }
            } else if (sender.hasPermission("donatecase.player") && !sender.hasPermission("donatecase.admin") && !sender.hasPermission("donatecase.mod")) {
                for (String subCommandName : api.getSubCommandManager().getSubCommands().keySet()) {
                    SubCommand subCommand = api.getSubCommandManager().getSubCommands().get(subCommandName).getFirst();
                    if (subCommand.getType() == SubCommandType.PLAYER || subCommand.getType() == null) {
                        value.add(subCommandName);
                    }
                }
            } else {
                return new ArrayList<>();
            }
        } else if (api.getSubCommandManager().getSubCommands().get(args[0]) != null) {
            return api.getSubCommandManager().getTabCompletionsForSubCommand(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
        } else {
            return new ArrayList<>();
        }

        if (args[args.length - 1].isEmpty()) {
            list = value;
        } else {
            list = value.stream().filter(tmp -> tmp.startsWith(args[args.length - 1])).collect(Collectors.toList());
        }

        Collections.sort(list);
        return list;
    }

}
