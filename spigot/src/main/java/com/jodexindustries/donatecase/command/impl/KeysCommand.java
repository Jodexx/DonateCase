package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
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

public class KeysCommand extends SubCommand<CommandSender> {

    private final DCAPIBukkit api;

    public KeysCommand(DCAPIBukkit api) {
        super("keys", api.getAddon());
        setPermission(SubCommandType.PLAYER.permission);
        this.api = api;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(api.getDonateCase(), () -> {
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
            for (String message : api.getConfig().getLang().getStringList("my-keys")) {
                String formattedMessage = formatMessage(player.getName(), player, message);
                sender.sendMessage(formattedMessage);
            }
        } else {
            DCToolsBukkit.msgRaw(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("no-permission")));
        }
    }

    private void handleMod(CommandSender sender, String target) {
        if (sender.hasPermission("donatecase.mod")) {
            for (String message : api.getConfig().getLang().getStringList("player-keys")) {
                Player targetPlayer = Bukkit.getPlayerExact(target);
                String formattedMessage = formatMessage(target, targetPlayer, message);
                sender.sendMessage(formattedMessage.replace("%player", target));
            }
        } else {
            DCToolsBukkit.msgRaw(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("no-permission")));
        }
    }

    private String formatMessage(String name, Player player, String message) {
        if (player != null) message = api.getTools().getPAPI().setPlaceholders(player, message);

        String placeholder = DCTools.getLocalPlaceholder(message);
        String result = "0";
        if (placeholder.startsWith("keys_")) {
            String[] parts = placeholder.split("_");
            String caseType = parts[1];
            int keys = api.getCaseKeyManager().getKeys(caseType, name);
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
