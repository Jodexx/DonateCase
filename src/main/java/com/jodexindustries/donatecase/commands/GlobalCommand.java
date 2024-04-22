package com.jodexindustries.donatecase.commands;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.*;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.addon.JavaAddon;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import com.jodexindustries.donatecase.tools.Tools;
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
import static com.jodexindustries.donatecase.DonateCase.*;

public class GlobalCommand implements CommandExecutor, TabCompleter {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                if (sender.hasPermission("donatecase.player")) {
                    sendHelp(sender, label);
                } else {
                    DonateCase.t.msg_(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("NoPermission")));
                }
            } else {
                sendHelp(sender, label);
            }
        } else {
            //reload command
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("donatecase.admin")) {
                    if(args.length == 1) {
                        instance.setupConfigs();
                        instance.setupLangs();
                        if(hologramManager != null) hologramManager.removeAllHolograms();
                        instance.loadHolograms();
                        DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("ReloadConfig")));
                    } else {
                        if(args[1].equalsIgnoreCase("cache")) {
                            instance.cleanCache();
                            instance.setupConfigs();
                            instance.setupLangs();
                            DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("ReloadConfigCache", "&aReloaded all DonateCase Cache")));
                        }
                    }
                } else {
                    DonateCase.t.msg_(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("NoPermission")));
                }
            } else
            //givekey command
            if (args[0].equalsIgnoreCase("givekey") || args[0].equalsIgnoreCase("gk")) {
                if(sender.hasPermission("donatecase.mod")) {
                        if (args.length >= 4) {
                            String player = args[1];
                            String caseName = args[2];
                            Player target = Bukkit.getPlayer(player);
                            int keys;
                            try {
                                keys = Integer.parseInt(args[3]);
                            } catch (NumberFormatException e) {
                                DonateCase.t.msg_(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("NumberFormatException"), "%string:" + args[3]));
                                return true;
                            }
                            if (Case.hasCaseByType(caseName)) {
                                CaseData data = Case.getCase(caseName).clone();
                                String caseTitle = data.getCaseTitle();
                                String caseDisplayName = data.getCaseDisplayName();
                                Case.addKeys(caseName, player, keys);
                                DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("GiveKeys"), "%player:" + player, "%key:" + keys, "%casetitle:" + caseTitle, "%casedisplayname:" + caseDisplayName, "%case:" + caseName));
                                if (customConfig.getConfig().getBoolean("DonatCase.SetKeysTargetMessage")) {
                                    DonateCase.t.msg(target, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("GiveKeysTarget"), "%player:" + player, "%key:" + keys, "%casetitle:" + caseTitle, "%casedisplayname:" + caseDisplayName, "%case:" + caseName));
                                }
                            } else {
                                DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("CaseNotExist"), "%case:" + caseName));
                            }
                        } else {
                            sendHelp(sender, label);
                        }
                } else {
                    DonateCase.t.msg_(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("NoPermission")));
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
                            if (!DonateCase.sql) {
                                customConfig.getKeys().set("DonatCase.Cases", null);
                                customConfig.saveKeys();
                                DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("ClearAllKeys")));
                            } else {
                                DonateCase.mysql.delAllKey();
                                DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("ClearAllKeys")));
                            }
                        }
                    } else {
                        String player = args[1];
                        String caseName = args[2];
                        if (Case.hasCaseByType(caseName)) {
                            CaseData data = Case.getCase(caseName).clone();
                            String caseTitle = data.getCaseTitle();
                            String caseDisplayName = data.getCaseDisplayName();
                            Case.setNullKeys(caseName, player);
                            DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("ClearKeys"), "%player:" + player, "%casetitle:" + caseTitle, "%casedisplayname:" + caseDisplayName, "%case:" + caseName));
                        } else {
                            DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("CaseNotExist"), "%case:" + caseName));
                        }
                    }
                }
            } else
            if(args[0].equalsIgnoreCase("setkey") || args[0].equalsIgnoreCase("sk")) {
                if (!sender.hasPermission("donatecase.admin") && !sender.hasPermission("donatecase.mod")) {
                    DonateCase.t.msg_(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("NoPermission")));
                } else if (args.length >= 4) {
                    String player = args[1];
                    String caseName = args[2];
                    Player target = Bukkit.getPlayer(player);
                    int keys;
                    try {
                        keys = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        DonateCase.t.msg_(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("NumberFormatException"), "%string:" + args[3]));
                        return true;
                    }
                    if (Case.hasCaseByType(caseName)) {
                        CaseData data = Case.getCase(caseName).clone();
                        String caseTitle = data.getCaseTitle();
                        String caseDisplayName = data.getCaseDisplayName();
                        Case.setKeys(caseName, player, keys);
                        DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("SetKeys"), "%player:" + player, "%key:" + keys, "%casetitle:" + caseTitle, "%casedisplayname:" + caseDisplayName, "%case:" + caseName));
                        if (customConfig.getConfig().getBoolean("DonatCase.SetKeysTargetMessage")) {
                            DonateCase.t.msg(target, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("SetKeysTarget"), "%player:" + player, "%key:" + keys, "%casetitle:" + caseTitle, "%casedisplayname:" + caseDisplayName, "%case:" + caseName));
                        }
                    } else {
                        DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("CaseNotExist"), "%case:" + caseName));
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
                            for (String string : DonateCase.customConfig.getLang().getStringList("MyKeys")) {
                                if(DonateCase.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                                    string = PAPISupport.setPlaceholders(player, string);
                                }
                                String placeholder = t.getLocalPlaceholder(string);
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
                                sender.sendMessage(t.rc(string.replace("%" + placeholder + "%", result)));
                            }
                        }
                    }
                } else {
                    if (sender.hasPermission("donatecase.mod")) {
                        String target = args[1];
                        //Get player keys
                        for (String string : DonateCase.customConfig.getLang().getStringList("PlayerKeys")) {
                            if(DonateCase.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
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
                                int keys = Case.getKeys(caseName, target);
                                if(parts.length == 2) {
                                    result = String.valueOf(keys);
                                } else if (parts.length == 3 && parts[2].equalsIgnoreCase("format")) {
                                    result = NumberFormat.getNumberInstance().format(keys);
                                }
                            }
                            sender.sendMessage(t.rc(string).replace("%player", target)
                                    .replace("%"+placeholder+"%", result));
                        }
                    } else {
                        DonateCase.t.msg_(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("NoPermission")));
                    }
                }
            } else
            //cases
            if (args[0].equalsIgnoreCase("cases")) {
                if (sender.hasPermission("donatecase.mod")) {
                    int num = 0;
                        for (String caseName : casesConfig.getCases().keySet()) {
                            num++;
                            CaseData data = Case.getCase(caseName).clone();
                            String caseTitle = data.getCaseTitle();
                            String caseDisplayName = data.getCaseDisplayName();

                            DonateCase.t.msg_(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("CasesList"), "%casename:" + caseName, "%num:" + num, "%casedisplayname:" + caseDisplayName, "%casetitle:" + caseTitle ));
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
                            if (Case.hasCaseByType(caseName)) {
                                int keys = Case.getKeys(caseName, playerName);
                                if (keys >= 1) {
                                    Case.removeKeys(caseName, playerName, 1);
                                    CaseData caseData = Case.getCase(caseName).clone();
                                    CaseData.Item winGroup = Tools.getRandomGroup(caseData);
                                    Case.onCaseOpenFinish(caseData, player, true, winGroup);
                                } else {
                                    DonateCase.t.msg(player, DonateCase.customConfig.getLang().getString("NoKey"));
                                }
                            } else {
                                DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("CaseNotExist"), "%case:" + caseName));
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
                            if (Case.hasCaseByType(caseType)) {
                                if (Case.hasCaseByLocation(l)) {
                                    DonateCase.t.msg(sender, DonateCase.customConfig.getLang().getString("HasDonatCase"));
                                } else {
                                    if(!Case.hasCaseTypeByCustomName(caseName)) {
                                        Case.saveLocation(caseName, caseType, l);
                                        DonateCase.t.msg(sender, DonateCase.customConfig.getLang().getString("AddDonatCase"));
                                    } else {
                                        DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("CaseAlreadyHasByName"), "%casename:" + caseName));
                                    }
                                }
                            } else {
                                DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("CaseNotExist"), "%case:" + caseType));
                            }
                        } else {
                            sendHelp(sender, label);
                        }
                    } else {
                        DonateCase.t.msg_(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("NoPermission")));
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
                                if(Case.getHologramManager() != null) Case.getHologramManager().removeHologram(l.getBlock());
                                DonateCase.t.msg(sender, DonateCase.customConfig.getLang().getString("RemoveDonatCase"));
                            } else {
                                DonateCase.t.msg(sender, DonateCase.customConfig.getLang().getString("BlockDontDonatCase"));
                            }
                        }
                    } else if (args.length == 2) {
                        String name = args[1];
                        if (Case.hasCaseTypeByCustomName(name)) {
                            Case.deleteCaseByName(name);
                            Location location = Case.getCaseLocationByCustomName(name);
                            if(Case.getHologramManager() != null) if(location != null) Case.getHologramManager().removeHologram(location.getBlock());
                            DonateCase.t.msg(sender, DonateCase.customConfig.getLang().getString("RemoveDonatCase"));
                        } else {
                            DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("CaseNotExist"), "%case:" + name));
                        }
                    }
                } else {
                    DonateCase.t.msg_(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("NoPermission")));
                }
            } else {
                String subCommandName = args[0];
                SubCommand subCommand = subCommands.get(subCommandName.toLowerCase());
                if(subCommand != null) {
                    if(subCommand.getType() == SubCommandType.ADMIN && sender.hasPermission("donatecase.admin")) {
                        subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                    } else if(subCommand.getType() == SubCommandType.MODER && (sender.hasPermission("donatecase.mod") || sender.hasPermission("donatecase.admin"))) {
                        subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                    } else if(( subCommand.getType() == SubCommandType.PLAYER || subCommand.getType() == null ) && (sender.hasPermission("donatecase.player") ||
                            sender.hasPermission("donatecase.mod") ||
                            sender.hasPermission("donatecase.admin"))) {
                        subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                    } else {
                        DonateCase.t.msg_(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("NoPermission")));
                    }
                } else {
                    sendHelp(sender, label);
                }
            }
        }
        return true;
    }

    private void sendHelp(CommandSender sender, String label) {
        DonateCase.t.msg_(sender, DonateCase.t.rt("&aDonateCase " + DonateCase.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
        if(sender.hasPermission("donatecase.player") && !sender.hasPermission("donatecase.mod")) {
            for (String string : DonateCase.customConfig.getLang().getStringList("HelpPlayer")) {
                DonateCase.t.msg_(sender, DonateCase.t.rt(string, "%cmd:" + label));
            }
        } else if(sender.hasPermission("donatecase.mod") || sender.hasPermission("donatecase.admin")) {
            for (String string : DonateCase.customConfig.getLang().getStringList("Help")) {
                DonateCase.t.msg_(sender, DonateCase.t.rt(string, "%cmd:" + label));
            }
        }
        if(customConfig.getConfig().getBoolean("DonatCase.AddonsHelp", true)) {
            Map<String, List<Map<String, SubCommand>>> addonsMap = new HashMap<>();
            for (String subCommandName : SubCommandManager.getSubCommands().keySet()) {
                List<Map<String, SubCommand>> list = new ArrayList<>();
                Map<String, SubCommand> commandMap = new HashMap<>();
                SubCommand subCommand = SubCommandManager.getSubCommands().get(subCommandName);
                String addonName = null;
                try {
                    addonName = JavaPlugin.getProvidingPlugin(subCommand.getClass()).getName();
                } catch (IllegalArgumentException | IllegalStateException ignored) {}

                if(addonName == null) addonName = JavaAddon.getNameByClassLoader(subCommand.getClass().getClassLoader());
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
                        if(DonateCase.customConfig.getLang().getString("HelpAddons.Format.AddonName") != null && !DonateCase.customConfig.getLang().getString("HelpAddons.Format.AddonName", "").isEmpty()) {
                            t.msg_(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("HelpAddons.Format.AddonName"), "%addon:" + addon));
                        }
                        List<Map<String, SubCommand>> commands = addonsMap.get(addon);
                        for (Map<String, SubCommand> command : commands) {
                            for (String commandName : command.keySet()) {
                                SubCommand subCommand = command.get(commandName);
                                String description = subCommand.getDescription();
                                if (description == null) {
                                    description = "";
                                } else {
                                    description = DonateCase.t.rt(DonateCase.customConfig.getLang().getString("HelpAddons.Format.AddonDescription"), "%description:" + description);
                                }
                                StringBuilder builder = new StringBuilder();
                                if (subCommand.getArgs() != null) {
                                    for (String arg : subCommand.getArgs()) {
                                        builder.append(arg).append(" ");
                                    }
                                }
                                if (sender.hasPermission("donatecase.admin")) {
                                    if (subCommand.getType() == SubCommandType.ADMIN || subCommand.getType() == SubCommandType.MODER || subCommand.getType() == SubCommandType.PLAYER || subCommand.getType() == null) {
                                        t.msg_(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("HelpAddons.Format.AddonCommand"),
                                                "%cmd:" + commandName,
                                                "%args:" + builder,
                                                "%description:" + description
                                        ));
                                    }
                                } else if (sender.hasPermission("donatecase.mod") && !sender.hasPermission("donatecase.admin")) {
                                    if (subCommand.getType() == SubCommandType.MODER || subCommand.getType() == SubCommandType.PLAYER || subCommand.getType() == null) {
                                        t.msg_(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("HelpAddons.Format.AddonCommand"),
                                                "%cmd:" + commandName,
                                                "%args:" + builder,
                                                "%description:" + description
                                        ));
                                    }
                                } else if (sender.hasPermission("donatecase.player") && !sender.hasPermission("donatecase.admin") && !sender.hasPermission("donatecase.mod")) {
                                    if (subCommand.getType() == SubCommandType.PLAYER || subCommand.getType() == null) {
                                        t.msg_(sender, DonateCase.t.rt(DonateCase.customConfig.getLang().getString("HelpAddons.Format.AddonCommand"),
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
