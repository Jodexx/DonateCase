package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.tools.DCTools;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class KeysCommand extends SubCommand.SubCommandBuilder implements SubCommandExecutor, SubCommandTabCompleter {

    private final DCAPI api;

    public KeysCommand(DCAPI api) {
        super();
        name("keys");
        addon(api.getPlatform());
        permission(SubCommandType.PLAYER.permission);
        executor(this);
        tabCompleter(this);
        this.api = api;
    }

    @Override
    public boolean execute(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        CompletableFuture.runAsync(() -> {
            if (args.length < 1) {
                if (sender instanceof DCPlayer) {
                    DCPlayer player = (DCPlayer) sender;
                    handlePlayer(sender, player);
                }
            } else {
                handleMod(sender, args[0]);
            }
        });
        return true;
    }

    private void handlePlayer(DCCommandSender sender, DCPlayer player) {
        if (sender.hasPermission("donatecase.player")) {
            for (String message : api.getConfig().getMessages().getStringList("my-keys")) {
                String formattedMessage = formatMessage(player.getName(), message);
                sender.sendMessage(formattedMessage);
            }
        } else {
            sender.sendMessage(DCTools.prefix(api.getConfig().getMessages().getString("no-permission")));
        }
    }

    private void handleMod(DCCommandSender sender, String target) {
        if (sender.hasPermission("donatecase.mod")) {
            for (String message : api.getConfig().getMessages().getStringList("player-keys")) {
                String formattedMessage = formatMessage(target, message);
                sender.sendMessage(formattedMessage.replace("%player", target));
            }
        } else {
            sender.sendMessage(DCTools.prefix(api.getConfig().getMessages().getString("no-permission")));
        }
    }

    private String formatMessage(String name, String message) {
        String placeholder = DCTools.getLocalPlaceholder(message);
        String result = "0";
        if (placeholder.startsWith("keys_")) {
            String[] parts = placeholder.split("_");
            String caseType = parts[1];
            int keys = api.getCaseKeyManager().get(caseType, name);
            if (parts.length == 2) {
                result = String.valueOf(keys);
            } else if (parts.length == 3 && parts[2].equalsIgnoreCase("format")) {
                result = NumberFormat.getNumberInstance().format(keys);
            }
        }
        return DCTools.rc(message.replace("%" + placeholder + "%", result));
    }


    @Override
    public List<String> getTabCompletions(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        return args.length != 1 || !sender.hasPermission("donatecase.mod") ? new ArrayList<>() :
                Arrays.stream(api.getPlatform().getOnlinePlayers())
                        .map(DCPlayer::getName)
                        .filter(px -> px.startsWith(args[0]))
                        .collect(Collectors.toList());
    }

}
