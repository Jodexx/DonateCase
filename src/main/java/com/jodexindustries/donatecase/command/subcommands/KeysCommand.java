package com.jodexindustries.donatecase.command.subcommands;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for /dc keys subcommand implementation
 */
public class KeysCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                handlePlayer(sender, player);
            }
        } else {
            handleMod(sender, args[0]);
        }
    }

    private void handlePlayer(CommandSender sender, Player player) {
        if (sender.hasPermission("donatecase.player")) {
            for (String message : Case.getCustomConfig().getLang().getStringList("my-keys")) {
                String formattedMessage = formatMessage(player.getName(), player, message);
                sender.sendMessage(formattedMessage);
            }
        } else {
            Tools.msgRaw(sender, Tools.rt(Case.getCustomConfig().getLang().getString("no-permission")));
        }
    }

    private void handleMod(CommandSender sender, String target) {
        if (sender.hasPermission("donatecase.mod")) {
            for (String message : Case.getCustomConfig().getLang().getStringList("player-keys")) {
                Player targetPlayer = Bukkit.getPlayerExact(target);
                String formattedMessage = formatMessage(target, targetPlayer, message);
                sender.sendMessage(formattedMessage.replace("%player", target));
            }
        } else {
            Tools.msgRaw(sender, Tools.rt(Case.getCustomConfig().getLang().getString("no-permission")));
        }
    }

    private String formatMessage(String name, Player player, String message) {
        if (player != null && Case.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            message = PAPISupport.setPlaceholders(player, message);
        }
        String placeholder = Tools.getLocalPlaceholder(message);
        String result = "0";
        if (placeholder.startsWith("keys_")) {
            String[] parts = placeholder.split("_");
            String caseTitle = parts[1];
            int keys = Case.getKeys(caseTitle, name);
            if (parts.length == 2) {
                result = String.valueOf(keys);
            } else if (parts.length == 3 && parts[2].equalsIgnoreCase("format")) {
                result = NumberFormat.getNumberInstance().format(keys);
            }
        }
        return Tools.rc(message.replace("%" + placeholder + "%", result));
    }


    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        if (args.length != 1 || !sender.hasPermission("donatecase.mod")) {
            return new ArrayList<>();
        }
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(px -> px.startsWith(args[0])).collect(Collectors.toList());
    }

    @Override
    public SubCommandType getType() {
        return SubCommandType.PLAYER;
    }
}
