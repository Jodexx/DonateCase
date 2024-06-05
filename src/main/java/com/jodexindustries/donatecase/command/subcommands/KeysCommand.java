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

public class KeysCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (sender.hasPermission("donatecase.player")) {
                    for (String string : Case.getInstance().customConfig.getLang().getStringList("MyKeys")) {
                        if(Case.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                            string = PAPISupport.setPlaceholders(player, string);
                        }
                        String placeholder = Tools.getLocalPlaceholder(string);
                        String result = "0";
                        if(placeholder.startsWith("keys_")) {
                            String[] parts = placeholder.split("_");
                            String caseTitle = parts[1];
                            int keys = Case.getKeys(caseTitle, player.getName());
                            if(parts.length == 2) {
                                result = String.valueOf(keys);
                            } else if (parts.length == 3 && parts[2].equalsIgnoreCase("format")) {
                                result = NumberFormat.getNumberInstance().format(keys);
                            }
                        }
                        sender.sendMessage(Tools.rc(string.replace("%" + placeholder + "%", result)));
                    }
                }
            }
        } else {
            if (sender.hasPermission("donatecase.mod")) {
                String target = args[0];
                //Get player keys
                for (String string : Case.getInstance().customConfig.getLang().getStringList("PlayerKeys")) {
                    if(Case.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                        Player targetPlayer = Bukkit.getPlayerExact(target);
                        if(targetPlayer != null) {
                            string = PAPISupport.setPlaceholders(targetPlayer, string);
                        }
                    }
                    String placeholder = Tools.getLocalPlaceholder(string);
                    String result = "0";
                    if(placeholder.startsWith("keys_")) {
                        String[] parts = placeholder.split("_");
                        String caseName = parts[1];
                        int keys = Case.getKeys(caseName, target);
                        if(parts.length == 2) {
                            result = String.valueOf(keys);
                        } else if (parts.length == 3 && parts[2].equalsIgnoreCase("format")) {
                            result = NumberFormat.getNumberInstance().format(keys);
                        }
                    }
                    sender.sendMessage(Tools.rc(string).replace("%player", target)
                            .replace("%"+placeholder+"%", result));
                }
            } else {
                Tools.msgRaw(sender, Tools.rt(Case.getInstance().customConfig.getLang().getString("NoPermission")));
            }
        }
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        if (args.length != 1 || !sender.hasPermission("donatecase.mod")) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(px -> px.startsWith(args[0])).collect(Collectors.toList()));    }

    @Override
    public SubCommandType getType() {
        return SubCommandType.PLAYER;
    }
}
