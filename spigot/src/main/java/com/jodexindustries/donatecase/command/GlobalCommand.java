package com.jodexindustries.donatecase.command;

import com.jodexindustries.donatecase.BuildConstants;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static com.jodexindustries.donatecase.DonateCase.instance;

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
            SubCommand<CommandSender> subCommand = instance.api.getSubCommandManager().getRegisteredSubCommand(subCommandName);

            if (subCommand == null) {
                sendHelp(sender, label);
                return false;
            }

            String permission = subCommand.getPermission();

            if (permission == null || sender.hasPermission(permission))
                subCommand.getExecutor().execute(sender, label, Arrays.copyOfRange(args, 1, args.length));
            else DCToolsBukkit.msgRaw(sender, DCToolsBukkit.rt(instance.api.getConfig().getLang().getString("no-permission")));
        }

        return true;
    }

    public static void sendHelp(CommandSender sender, String label) {
        if (!sender.hasPermission("donatecase.player")) {
            DCToolsBukkit.msgRaw(sender, DCToolsBukkit.rt(instance.api.getConfig().getLang().getString("no-permission")));
            return;
        }

        DCToolsBukkit.msgRaw(sender, DCToolsBukkit.rt("&aDonateCase &7v&6" + Case.getInstance().getDescription().getVersion() + " &7(&eAPI &7v&6" + BuildConstants.api + "&7) by &c_Jodex__"));

        if (!sender.hasPermission("donatecase.mod")) {
            sendHelpMessages(sender, "help-player", label);
        } else {
            sendHelpMessages(sender, "help", label);
        }

        if (instance.api.getConfig().getConfig().getBoolean("DonateCase.AddonsHelp", true)) {
            Map<String, List<Map<String, SubCommand<CommandSender>>>> addonsMap = buildAddonsMap();
            if (DCToolsBukkit.isHasCommandForSender(sender, addonsMap)) {
                sendAddonHelpMessages(sender, addonsMap);
            }
        }
    }

    private static void sendHelpMessages(CommandSender sender, String path, String label) {
        for (String string : instance.api.getConfig().getLang().getStringList(path)) {
            DCToolsBukkit.msgRaw(sender, DCToolsBukkit.rt(string, "%cmd:" + label));
        }
    }

    private static Map<String, List<Map<String, SubCommand<CommandSender>>>> buildAddonsMap() {
        Map<String, List<Map<String, SubCommand<CommandSender>>>> addonsMap = new HashMap<>();
        instance.api.getSubCommandManager().getRegisteredSubCommands().forEach((subCommandName, subCommand) -> {
            Addon addon = subCommand.getAddon();
            addonsMap.computeIfAbsent(addon.getName(), k -> new ArrayList<>())
                    .add(Collections.singletonMap(subCommandName, subCommand));
        });
        return addonsMap;
    }

    private static void sendAddonHelpMessages(CommandSender sender, Map<String, List<Map<String, SubCommand<CommandSender>>>> addonsMap) {
        addonsMap.forEach((addon, commands) -> {
            if (!addon.equalsIgnoreCase("DonateCase") && DCToolsBukkit.isHasCommandForSender(sender, addonsMap, addon)) {
                String addonNameFormat = instance.api.getConfig().getLang().getString("help-addons.format.name");
                if (addonNameFormat != null && !addonNameFormat.isEmpty()) {
                    DCToolsBukkit.msgRaw(sender, DCToolsBukkit.rt(addonNameFormat, "%addon:" + addon));
                }

                commands.forEach(command -> command.forEach((commandName, subCommand) -> {
                    String description = subCommand.getDescription();
                    description = (description != null) ? DCToolsBukkit.rt(instance.api.getConfig().getLang().getString("help-addons.format.description"), "%description:" + description) : "";

                    StringBuilder argsBuilder = compileSubCommandArgs(subCommand.getArgs());
                    String permission = subCommand.getPermission();

                    if (permission == null || sender.hasPermission(permission)) {
                        DCToolsBukkit.msgRaw(sender, DCToolsBukkit.rt(instance.api.getConfig().getLang().getString("help-addons.format.command"),
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
            Map<String, SubCommand<CommandSender>> subCommands = instance.api.getSubCommandManager().getRegisteredSubCommands();

            for (Map.Entry<String, SubCommand<CommandSender>> entry : subCommands.entrySet()) {
                String subCommandName = entry.getKey();
                SubCommand<CommandSender> subCommand = entry.getValue();
                String permission = subCommand.getPermission();

                if (permission == null || sender.hasPermission(permission)) {
                    value.add(subCommandName);
                }
            }
        } else if (instance.api.getSubCommandManager().getRegisteredSubCommands().containsKey(args[0])) {
            return instance.api.getSubCommandManager().getTabCompletionsForSubCommand(sender, args[0], label, Arrays.copyOfRange(args, 1, args.length));
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
        List<String> value = new ArrayList<>(instance.api.getConfig().getConfigCases().getCases().keySet());
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(px -> px.startsWith(args[0])).collect(Collectors.toList()));
            return list;
        } else if (args.length >= 3) {
            if (args.length == 4) {
                list.add("-s");
                return list;
            }
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
