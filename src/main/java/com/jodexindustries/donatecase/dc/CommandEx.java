package com.jodexindustries.donatecase.dc;

import com.jodexindustries.donatecase.api.*;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import com.jodexindustries.donatecase.tools.Tools;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.jodexindustries.donatecase.api.SubCommandManager.*;
import static com.jodexindustries.donatecase.dc.Main.*;

public class CommandEx implements CommandExecutor, TabCompleter {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                if (sender.hasPermission("donatecase.player")) {
                    sendHelp(sender, label);
                } else {
                    Main.t.msg_(sender, Main.t.rt(Main.lang.getString("NoPermission")));
                }
            } else {
                sendHelp(sender, label);
            }
        } else {
            //reload command
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("donatecase.admin")) {
                    Main.instance.setupConfigs();
                    Main.instance.setupLangs();
                    Main.t.msg(sender, Main.t.rt(Main.lang.getString("ReloadConfig")));
                } else {
                    Main.t.msg_(sender, Main.t.rt(Main.lang.getString("NoPermission")));
                }
            } else
            //givekey command
            if (args[0].equalsIgnoreCase("givekey") || args[0].equalsIgnoreCase("gk")) {
                if(sender.hasPermission("donatecase.mod")) {
                        if (args.length >= 4) {
                            String player = args[1];
                            String casename = args[2];
                            Player target = Bukkit.getPlayer(player);
                            int keys;
                            try {
                                keys = Integer.parseInt(args[3]);
                            } catch (NumberFormatException e) {
                                Main.t.msg_(sender, Main.t.rt(lang.getString("NumberFormatException"), "%string:" + args[3]));
                                return true;
                            }
                            if (Case.hasCaseByName(casename)) {
                                String casetitle = Case.getCaseTitle(casename);
                                Case.addKeys(casename, player, keys);
                                Main.t.msg(sender, Main.t.rt(Main.lang.getString("GiveKeys"), "%player:" + player, "%key:" + keys, "%casetitle:" + casetitle, "%case:" + casename));
                                if (customConfig.getConfig().getBoolean("DonatCase.SetKeysTargetMessage")) {
                                    Main.t.msg(target, Main.t.rt(Main.lang.getString("GiveKeysTarget"), "%player:" + player, "%key:" + keys, "%casetitle:" + casetitle, "%case:" + casename));
                                }
                            } else {
                                Main.t.msg(sender, Main.t.rt(Main.lang.getString("CaseNotExist"), "%case:" + casename));
                            }
                        } else {
                            sendHelp(sender, label);
                        }
                } else {
                    Main.t.msg_(sender, Main.t.rt(Main.lang.getString("NoPermission")));
                }
            } else
            //delkey
            if(args[0].equalsIgnoreCase("delkey") || args[0].equalsIgnoreCase("dk")) {
                if (sender.hasPermission("donatecase.admin") || sender.hasPermission("donatecase.mod")) {
                    if (args.length == 1) {
                        sendHelp(sender, label);
                    } else
                    if (args.length == 2) {
                        if (args[1].equalsIgnoreCase("all")) {
                            if (Main.Tconfig) {
                                customConfig.getKeys().set("DonatCase.Cases", null);
                                customConfig.saveKeys();
                                Main.t.msg(sender, Main.t.rt(Main.lang.getString("ClearAllKeys")));
                            } else {
                                Main.mysql.delAllKey();
                                Main.t.msg(sender, Main.t.rt(Main.lang.getString("ClearAllKeys")));
                            }
                        }
                    } else {
                        String player = args[1];
                        String caseName = args[2];
                        if (Case.hasCaseByName(caseName)) {
                            String caseTitle = Case.getCaseTitle(caseName);
                            Case.setNullKeys(caseName, player);
                            Main.t.msg(sender, Main.t.rt(Main.lang.getString("ClearKeys"), "%player:" + player, "%casetitle:" + caseTitle, "%case:" + caseName));
                        } else {
                            Main.t.msg(sender, Main.t.rt(Main.lang.getString("CaseNotExist"), "%case:" + caseName));
                        }
                    }
                }
            } else
            if(args[0].equalsIgnoreCase("setkey") || args[0].equalsIgnoreCase("sk")) {
                if (!sender.hasPermission("donatecase.admin") && !sender.hasPermission("donatecase.mod")) {
                    Main.t.msg_(sender, Main.t.rt(Main.lang.getString("NoPermission")));
                } else if (args.length >= 4) {
                    String player = args[1];
                    String caseName = args[2];
                    Player target = Bukkit.getPlayer(player);
                    int keys;
                    try {
                        keys = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        Main.t.msg_(sender, Main.t.rt(lang.getString("NumberFormatException"), "%string:" + args[3]));
                        return true;
                    }
                    if (Case.hasCaseByName(caseName)) {
                        String caseTitle = Case.getCaseTitle(caseName);
                        Case.setKeys(caseName, player, keys);
                        Main.t.msg(sender, Main.t.rt(Main.lang.getString("SetKeys"), "%player:" + player, "%key:" + keys, "%casetitle:" + caseTitle, "%case:" + caseName));
                        if (customConfig.getConfig().getBoolean("DonatCase.SetKeysTargetMessage")) {
                            Main.t.msg(target, Main.t.rt(Main.lang.getString("SetKeysTarget"), "%player:" + player, "%key:" + keys, "%casetitle:" + caseTitle, "%case:" + caseName));
                        }
                    } else {
                        Main.t.msg(sender, Main.t.rt(Main.lang.getString("CaseNotExist"), "%case:" + caseName));
                    }
                } else {
                    sendHelp(sender, label);
                }
            } else
            //keys
            if (args[0].equalsIgnoreCase("keys")) {
                if (args.length < 2) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (sender.hasPermission("donatecase.player")) {
                            for (String string : Main.lang.getStringList("MyKeys")) {
                                if(Main.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                                    string = PAPISupport.setPlaceholders(player, string);
                                }
                                String placeholder = t.getLocalPlaceholder(string);
                                String result = "0";
                                if(placeholder.startsWith("keys_")) {
                                    String[] parts = placeholder.split("_");
                                    String caseTitle = parts[1];
                                    int keys;
                                    if (Main.Tconfig) {
                                        keys = customConfig.getKeys().getInt("DonatCase.Cases." + caseTitle + "." + player.getName());
                                    } else {
                                        keys = Main.mysql.getKey(parts[1], player.getName());
                                    }
                                    result = NumberFormat.getNumberInstance().format(keys);
                                }
                                sender.sendMessage(t.rc(string.replaceAll("%" + placeholder + "%", result)));
                            }
                        }
                    }
                } else {
                    if (sender.hasPermission("donatecase.mod")) {
                        String target = args[1];
                        //Get player keys
                        for (String string : Main.lang.getStringList("PlayerKeys")) {
                            if(Main.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                                Player targetPlayer = Bukkit.getPlayerExact(target);
                                if(targetPlayer != null) {
                                    string = PAPISupport.setPlaceholders(targetPlayer, string);
                                }
                            }
                            String placeholder = t.getLocalPlaceholder(string);
                            String result = "0";
                            if(placeholder.startsWith("keys_")) {
                                String[] parts = placeholder.split("_");
                                String caseName = parts[1];
                                int keys;
                                if (Main.Tconfig) {
                                    keys = customConfig.getKeys().getInt("DonatCase.Cases." + caseName + "." + target);
                                } else {
                                    keys = Main.mysql.getKey(parts[1], target);
                                }
                                result = NumberFormat.getNumberInstance().format(keys);
                            }
                            sender.sendMessage(t.rc(string).replace("%player", target)
                                    .replace("%"+placeholder+"%", result));
                        }
                    } else {
                        Main.t.msg_(sender, Main.t.rt(Main.lang.getString("NoPermission")));
                    }
                }
            } else
            //cases
            if (args[0].equalsIgnoreCase("cases")) {
                if (sender.hasPermission("donatecase.mod")) {
                    int num = 0;
                        for (String caseName : casesConfig.getCases().keySet()) {
                            num++;
                            String caseTitle = Case.getCaseTitle(caseName);
                            Main.t.msg_(sender, Main.t.rt(Main.lang.getString("CasesList"), "%casename:" + caseName, "%num:" + num, "%casetitle:" + caseTitle));
                        }
                }
            } else
            // opencase
            if (args[0].equalsIgnoreCase("opencase")) {
                if (sender instanceof Player) {
                    String playerName = sender.getName();
                    Player player = (Player) sender;
                    if (sender.hasPermission("donatecase.player")) {
                        if (args.length == 2) {
                            String caseName = args[1];
                            if (Case.hasCaseByName(caseName)) {
                                int keys = Case.getKeys(caseName, playerName);
                                if (keys >= 1) {
                                    Case.removeKeys(caseName, playerName, 1);
                                    String winGroup = Tools.getRandomGroup(caseName);
                                    Case.onCaseOpenFinish(caseName, player, true, winGroup);
                                } else {
                                    Main.t.msg(player, Main.lang.getString("NoKey"));
                                }
                            } else {
                                Main.t.msg(sender, Main.t.rt(Main.lang.getString("CaseNotExist"), "%case:" + caseName));
                            }
                        } else {
                            sendHelp(sender, label);
                        }
                    }
                }
            } else
            //help
            if (args[0].equalsIgnoreCase("help")) {
                sendHelp(sender, label);
            } else
            //create
            if (args[0].equalsIgnoreCase("create")) {
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    Location l = player.getTargetBlock(null, 5).getLocation().setDirection(player.getLocation().getDirection());
                    if (sender.hasPermission("donatecase.admin")) {
                        if (args.length >= 3) {
                            String caseType = args[1];
                            String caseName = args[2];
                            if (Case.hasCaseByName(caseType)) {
                                if (Case.hasCaseByLocation(l)) {
                                    Main.t.msg(sender, Main.lang.getString("HasDonatCase"));
                                } else {
                                    if(!Case.hasCaseDataByName(caseName)) {
                                        Case.saveLocation(caseName, caseType, l);
                                        Main.t.msg(sender, Main.lang.getString("AddDonatCase"));
                                    } else {
                                        Main.t.msg(sender, Main.t.rt(Main.lang.getString("CaseAlreadyHasByName"), "%casename:" + caseName));
                                    }
                                }
                            } else {
                                Main.t.msg(sender, Main.t.rt(Main.lang.getString("CaseNotExist"), "%case:" + caseType));
                            }
                        } else {
                            sendHelp(sender, label);
                        }
                    } else {
                        Main.t.msg_(sender, Main.t.rt(Main.lang.getString("NoPermission")));
                    }
                }
            } else
            //delete
            if (args[0].equalsIgnoreCase("delete")) {
                if (sender.hasPermission("donatecase.admin")) {
                    if (args.length == 1) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            Location l = player.getTargetBlock(null, 5).getLocation();
                            if (Case.hasCaseByLocation(l)) {
                                Case.deleteCaseByLocation(l);
                                Main.t.msg(sender, Main.lang.getString("RemoveDonatCase"));
                            } else {
                                Main.t.msg(sender, Main.lang.getString("BlockDontDonatCase"));
                            }
                        }
                    } else if (args.length == 2) {
                        String name = args[1];
                        if (Case.hasCaseDataByName(name)) {
                            Case.deleteCaseByName(name);
                            Main.t.msg(sender, Main.lang.getString("RemoveDonatCase"));
                        } else {
                            Main.t.msg(sender, Main.t.rt(Main.lang.getString("CaseNotExist"), "%case:" + name));
                        }
                    }
                } else {
                    Main.t.msg_(sender, Main.t.rt(Main.lang.getString("NoPermission")));
                }
            } else {
                String subCommandName = args[0];
                SubCommand subCommand = subCommands.get(subCommandName.toLowerCase());
                if(subCommand != null) {
                    if(subCommand.getType() == SubCommandType.ADMIN && sender.hasPermission("donatecase.admin")) {
                        subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                    } else if(subCommand.getType() == SubCommandType.MODER && (sender.hasPermission("donatecase.mod") || sender.hasPermission("donatecase.admin"))) {
                        subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                    } else if(subCommand.getType() == SubCommandType.PLAYER && (sender.hasPermission("donatecase.player") ||
                            sender.hasPermission("donatecase.mod") ||
                            sender.hasPermission("donatecase.admin"))) {
                        subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                    } else {
                        Main.t.msg_(sender, Main.t.rt(Main.lang.getString("NoPermission")));
                    }
                } else {
                    sendHelp(sender, label);
                }
            }
        }
        return true;
    }

    private void sendHelp(CommandSender sender, String label) {
        Main.t.msg_(sender, Main.t.rt("&aDonateCase " + Main.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
        if(sender.hasPermission("donatecase.player") && !sender.hasPermission("donatecase.mod")) {
            for (String string : Main.lang.getStringList("HelpPlayer")) {
                Main.t.msg_(sender, Main.t.rt(string, "%cmd:" + label));
            }
        } else if(sender.hasPermission("donatecase.mod") || sender.hasPermission("donatecase.admin")) {
            for (String string : Main.lang.getStringList("Help")) {
                Main.t.msg_(sender, Main.t.rt(string, "%cmd:" + label));
            }
        }
        if(customConfig.getConfig().getBoolean("DonatCase.AddonsHelp", true)) {
            Map<String, List<Map<String, SubCommand>>> addonsMap = new HashMap<>();
            for (String subCommandName : SubCommandManager.getSubCommands().keySet()) {
                List<Map<String, SubCommand>> list = new ArrayList<>();
                Map<String, SubCommand> commandMap = new HashMap<>();
                SubCommand subCommand = SubCommandManager.getSubCommands().get(subCommandName);
                String addonName = JavaPlugin.getProvidingPlugin(subCommand.getClass()).getName();
                if (addonsMap.get(addonName) != null) {
                    list = addonsMap.get(addonName);
                }
                commandMap.put(subCommandName, subCommand);
                list.add(commandMap);
                addonsMap.put(addonName, list);
            }
            if (t.isHasCommandForSender(sender, addonsMap)) {
                for (String addon : addonsMap.keySet()) {
                    if(t.isHasCommandForSender(sender, addonsMap, addon)) {
                        if(lang.getString("HelpAddons.Format.AddonName") != null && !lang.getString("HelpAddons.Format.AddonName", "").isEmpty()) {
                            t.msg_(sender, Main.t.rt(lang.getString("HelpAddons.Format.AddonName"), "%addon:" + addon));
                        }
                        List<Map<String, SubCommand>> commands = addonsMap.get(addon);
                        for (Map<String, SubCommand> command : commands) {
                            for (String commandName : command.keySet()) {
                                SubCommand subCommand = command.get(commandName);
                                String description = subCommand.getDescription();
                                if (description == null) {
                                    description = "";
                                } else {
                                    description = Main.t.rt(lang.getString("HelpAddons.Format.AddonDescription"), "%description:" + description);
                                }
                                StringBuilder builder = new StringBuilder();
                                if (subCommand.getArgs() != null) {
                                    for (String arg : subCommand.getArgs()) {
                                        builder.append(arg).append(" ");
                                    }
                                }
                                if (sender.hasPermission("donatecase.admin")) {
                                    if (subCommand.getType() == SubCommandType.ADMIN || subCommand.getType() == SubCommandType.MODER || subCommand.getType() == SubCommandType.PLAYER || subCommand.getType() == null) {
                                        t.msg_(sender, Main.t.rt(lang.getString("HelpAddons.Format.AddonCommand"),
                                                "%cmd:" + commandName,
                                                "%args:" + builder,
                                                "%description:" + description
                                        ));
                                    }
                                } else if (sender.hasPermission("donatecase.mod") && !sender.hasPermission("donatecase.admin")) {
                                    if (subCommand.getType() == SubCommandType.MODER || subCommand.getType() == SubCommandType.PLAYER || subCommand.getType() == null) {
                                        t.msg_(sender, Main.t.rt(lang.getString("HelpAddons.Format.AddonCommand"),
                                                "%cmd:" + commandName,
                                                "%args:" + builder,
                                                "%description:" + description
                                        ));
                                    }
                                } else if (sender.hasPermission("donatecase.player") && !sender.hasPermission("donatecase.admin") && !sender.hasPermission("donatecase.mod")) {
                                    if (subCommand.getType() == SubCommandType.PLAYER || subCommand.getType() == null) {
                                        t.msg_(sender, Main.t.rt(lang.getString("HelpAddons.Format.AddonCommand"),
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
    }



    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        List<String> list = new ArrayList<>();
        List<String> value = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("donatecase.admin")) {
                value.addAll(Arrays.asList("help", "create", "delete", "givekey", "setkey", "reload", "keys", "opencase", "cases", "delkey"));
                for (String subCommandName : SubCommandManager.getSubCommands().keySet()) {
                    SubCommand subCommand = SubCommandManager.getSubCommands().get(subCommandName);
                    if (subCommand.getType() == SubCommandType.ADMIN || subCommand.getType() == SubCommandType.MODER || subCommand.getType() == null || subCommand.getType() == SubCommandType.PLAYER) {
                        value.add(subCommandName);
                    }
                }
            } else if (sender.hasPermission("donatecase.mod") && !sender.hasPermission("donatecase.admin")) {
                value.addAll(Arrays.asList("help", "givekey", "setkey", "keys", "cases", "opencase", "delkey"));
                for (String subCommandName : SubCommandManager.getSubCommands().keySet()) {
                    SubCommand subCommand = SubCommandManager.getSubCommands().get(subCommandName);
                    if (subCommand.getType() == SubCommandType.MODER || subCommand.getType() == SubCommandType.PLAYER || subCommand.getType() == null) {
                        value.add(subCommandName);
                    }
                }
            } else if (sender.hasPermission("donatecase.player") && !sender.hasPermission("donatecase.admin") && !sender.hasPermission("donatecase.mod")) {
                value.addAll(Arrays.asList("help", "keys", "opencase"));
                for (String subCommandName : SubCommandManager.getSubCommands().keySet()) {
                    SubCommand subCommand = SubCommandManager.getSubCommands().get(subCommandName);
                    if (subCommand.getType() == SubCommandType.PLAYER || subCommand.getType() == null) {
                        value.add(subCommandName);
                    }
                }
            } else {
                return new ArrayList<>();
            }
        } else if (args[0].equalsIgnoreCase("create") && sender.hasPermission("donatecase.admin")) {
            value.addAll(casesConfig.getCases().keySet());
            if(args.length >= 3) {
                return new ArrayList<>();
            }
        } else if (args[0].equalsIgnoreCase("opencase") && sender.hasPermission("donatecase.player")) {
            value.addAll(casesConfig.getCases().keySet());
            if(args.length >= 3) {
                return new ArrayList<>();
            }
        } else if (args[0].equalsIgnoreCase("delete")) {
            if (args.length == 2) {
                ConfigurationSection section = customConfig.getCases().getConfigurationSection("DonatCase.Cases");
                if (section != null) {
                    value.addAll(section.getKeys(false));
                } else {
                    return new ArrayList<>();
                }
            } else {
                return new ArrayList<>();
            }
        } else if (args[0].equalsIgnoreCase("keys")) {
            if (args.length != 2 || !sender.hasPermission("donatecase.mod")) {
                return new ArrayList<>();
            }
            list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(px -> px.startsWith(args[1])).collect(Collectors.toList()));
            return list;
        } else if (Arrays.asList("reload", "cases", "help").contains(args[0].toLowerCase())) {
            return new ArrayList<>();
        } else if (Arrays.asList("givekey", "gk", "setkey", "sk", "delkey", "dk").contains(args[0].toLowerCase())) {
            if (!sender.hasPermission("donatecase.mod")) {
                return new ArrayList<>();
            }
            value.addAll(casesConfig.getCases().keySet());
            if (args.length == 2) {
                list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(px -> px.startsWith(args[1])).collect(Collectors.toList()));
                return list;
            } else if (args.length >= 4) {
                return new ArrayList<>();
            }
        } else if (SubCommandManager.getSubCommands().get(args[0]) != null) {
            return getTabCompletionsForSubCommand(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
        } else {
            return new ArrayList<>();
        }

        if (args[args.length - 1].isEmpty()) {
            list = value;
        } else {
            list.addAll(value.stream().filter(tmp -> tmp.startsWith(args[args.length - 1])).collect(Collectors.toList()));
        }

        Collections.sort(list);
        return list;
    }


}
