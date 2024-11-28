package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.manager.SubCommandManager;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jodexindustries.donatecase.DonateCase.instance;

/**
 * Class for /dc keys subcommand implementation
 */
public class KeysCommand implements SubCommandExecutor<CommandSender>, SubCommandTabCompleter<CommandSender> {

    public static void register(SubCommandManager<CommandSender> manager) {
        KeysCommand command = new KeysCommand();

        SubCommand<CommandSender> subCommand = manager.builder("keys")
                .executor(command)
                .tabCompleter(command)
                .permission(SubCommandType.PLAYER.permission)
                .build();
        manager.registerSubCommand(subCommand);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(Case.getInstance(), () -> {
            if (args.length < 1) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    handlePlayer(sender, player);
                }
            } else {
                handleMod(sender, args[0]);
            }
        });
    }

    private void handlePlayer(CommandSender sender, Player player) {
        if (sender.hasPermission("donatecase.player")) {
            for (String message : instance.api.getConfig().getLang().getStringList("my-keys")) {
                String formattedMessage = formatMessage(player.getName(), player, message);
                sender.sendMessage(formattedMessage);
            }
        } else {
            DCToolsBukkit.msgRaw(sender, DCToolsBukkit.rt(instance.api.getConfig().getLang().getString("no-permission")));
        }
    }

    private void handleMod(CommandSender sender, String target) {
        if (sender.hasPermission("donatecase.mod")) {
            for (String message : instance.api.getConfig().getLang().getStringList("player-keys")) {
                Player targetPlayer = Bukkit.getPlayerExact(target);
                String formattedMessage = formatMessage(target, targetPlayer, message);
                sender.sendMessage(formattedMessage.replace("%player", target));
            }
        } else {
            DCToolsBukkit.msgRaw(sender, DCToolsBukkit.rt(instance.api.getConfig().getLang().getString("no-permission")));
        }
    }

    private String formatMessage(String name, Player player, String message) {
        if (player != null) message = Case.getInstance().papi.setPlaceholders(player, message);

        String placeholder = DCTools.getLocalPlaceholder(message);
        String result = "0";
        if (placeholder.startsWith("keys_")) {
            String[] parts = placeholder.split("_");
            String caseType = parts[1];
            int keys = Case.getInstance().api.getCaseKeyManager().getKeys(caseType, name);
            if (parts.length == 2) {
                result = String.valueOf(keys);
            } else if (parts.length == 3 && parts[2].equalsIgnoreCase("format")) {
                result = NumberFormat.getNumberInstance().format(keys);
            }
        }
        return DCToolsBukkit.rc(message.replace("%" + placeholder + "%", result));
    }


    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (args.length != 1 || !sender.hasPermission("donatecase.mod")) {
            return new ArrayList<>();
        }
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(px -> px.startsWith(args[0])).collect(Collectors.toList());
    }

}
