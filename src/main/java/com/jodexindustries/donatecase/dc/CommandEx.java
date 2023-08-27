package com.jodexindustries.donatecase.dc;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.SubCommand;
import com.jodexindustries.donatecase.api.SubCommandManager;
import com.jodexindustries.donatecase.api.SubCommandType;
import com.jodexindustries.donatecase.tools.Logger;
import com.jodexindustries.donatecase.tools.PAPISupport;
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
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.jodexindustries.donatecase.api.SubCommandManager.*;
import static com.jodexindustries.donatecase.dc.Main.customConfig;
import static com.jodexindustries.donatecase.dc.Main.t;

public class CommandEx implements CommandExecutor, TabCompleter {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (args.length == 0) {
            // if sender is player
            if (sender instanceof Player) {
                if (sender.hasPermission("donatecase.player") && !sender.hasPermission("donatecase.mod")) {
                        Main.t.msg_(sender, Main.t.rt("&aDonateCase " + Main.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                    for (String string : Main.lang.getStringList("HelpPlayer")) {
                        Main.t.msg_(sender, Main.t.rt(string, "%cmd:" + label));
                    }
                } else if (sender.hasPermission("donatecase.mod")) {
                        Main.t.msg_(sender, Main.t.rt("&aDonateCase " + Main.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                    for (String string : Main.lang.getStringList("Help")) {
                        Main.t.msg_(sender, Main.t.rt(string, "%cmd:" + label));
                    }
                } else {
                    Main.t.msg_(sender, Main.t.rt(Main.lang.getString("NoPermission")));
                }
                // if sender not player
            } else {
                Main.t.msg_(sender, Main.t.rt("&aDonateCase " + Main.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                for (String string : Main.lang.getStringList("Help")) {
                    Main.t.msg_(sender, Main.t.rt(string, "%cmd:" + label));
                }
            }
        }
        if (args.length >= 1) {
            //reload command
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("donatecase.admin")) {
                    Main.instance.setupConfigs();
                    Main.instance.setupLangs();
                    Main.t.msg(sender, Main.t.rt(Main.lang.getString("ReloadConfig")));
                } else {
                    Main.t.msg_(sender, Main.t.rt(Main.lang.getString("NoPermission")));
                }
            }
            //givekey command
            if (args[0].equalsIgnoreCase("givekey") || args[0].equalsIgnoreCase("gk")) {
                if(sender.hasPermission("donatecase.mod")) {
                        if (args.length >= 4) {
                            String player = args[1];
                            String casename = args[2];
                            Player target = Bukkit.getPlayer(player);
                            int keys = Integer.parseInt(args[3]);
                            if (com.jodexindustries.donatecase.api.Case.hasCaseByName(casename)) {
                                String casetitle = customConfig.getConfig().getString("DonatCase.Cases." + casename + ".Title");
                                com.jodexindustries.donatecase.api.Case.addKeys(casename, player, keys);
                                Main.t.msg(sender, Main.t.rt(Main.lang.getString("GiveKeys"), "%player:" + player, "%key:" + keys, "%casetitle:" + casetitle, "%case:" + casename));
                                if (customConfig.getConfig().getBoolean("DonatCase.SetKeysTargetMessage")) {
                                    Main.t.msg(target, Main.t.rt(Main.lang.getString("GiveKeysTarget"), "%player:" + player, "%key:" + keys, "%casetitle:" + casetitle, "%case:" + casename));
                                }
                            } else {
                                Main.t.msg(sender, Main.t.rt(Main.lang.getString("CaseNotExist"), "%case:" + casename));
                            }
                        } else {
                                Main.t.msg_(sender, Main.t.rt("&aDonateCase " + Main.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                            for (String string : Main.lang.getStringList("Help")) {
                                Main.t.msg_(sender, Main.t.rt(string, "%cmd:" + label));
                            }
                        }
                } else {
                    Main.t.msg_(sender, Main.t.rt(Main.lang.getString("NoPermission")));
                }
            }
            //delkey
            if(args[0].equalsIgnoreCase("delkey") || args[0].equalsIgnoreCase("dk")) {
                if (sender.hasPermission("donatecase.admin") || sender.hasPermission("donatecase.mod")) {
                    if (args.length == 1) {
                        Main.t.msg_(sender, Main.t.rt("&aDonateCase " + Main.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                        for (String string : Main.lang.getStringList("Help")) {
                            Main.t.msg_(sender, Main.t.rt(string, "%cmd:" + label));
                        }
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
                    } else
                    if (args.length >= 3) {
                        String player = args[1];
                        String casename = args[2];
                        if (com.jodexindustries.donatecase.api.Case.hasCaseByName(casename)) {
                            String casetitle = customConfig.getConfig().getString("DonatCase.Cases." + casename + ".Title");
                            com.jodexindustries.donatecase.api.Case.setNullKeys(casename, player);
                            Main.t.msg(sender, Main.t.rt(Main.lang.getString("ClearKeys"), "%player:" + player, "%casetitle:" + casetitle, "%case:" + casename));
                        } else {
                            Main.t.msg(sender, Main.t.rt(Main.lang.getString("CaseNotExist"), "%case:" + casename));
                        }
                    } else {
                        Main.t.msg_(sender, Main.t.rt("&aDonateCase " + Main.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                        for (String string : Main.lang.getStringList("Help")) {
                            Main.t.msg_(sender, Main.t.rt(string, "%cmd:" + label));
                        }
                    }
                }
            }
            //setkey
            if(args[0].equalsIgnoreCase("setkey") || args[0].equalsIgnoreCase("sk")) {
                if (!sender.hasPermission("donatecase.admin") && !sender.hasPermission("donatecase.mod")) {
                    Main.t.msg_(sender, Main.t.rt(Main.lang.getString("NoPermission")));
                } else if (args.length >= 4) {
                    String player = args[1];
                    String casename = args[2];
                    Player target = Bukkit.getPlayer(player);
                    int keys = Integer.parseInt(args[3]);
                    if (com.jodexindustries.donatecase.api.Case.hasCaseByName(casename)) {
                        String casetitle = customConfig.getConfig().getString("DonatCase.Cases." + casename + ".Title");
                        com.jodexindustries.donatecase.api.Case.setKeys(casename, player, keys);
                        Main.t.msg(sender, Main.t.rt(Main.lang.getString("SetKeys"), "%player:" + player, "%key:" + keys, "%casetitle:" + casetitle, "%case:" + casename));
                        if (customConfig.getConfig().getBoolean("DonatCase.SetKeysTargetMessage")) {
                            Main.t.msg(target, Main.t.rt(Main.lang.getString("SetKeysTarget"), "%player:" + player, "%key:" + keys, "%casetitle:" + casetitle, "%case:" + casename));
                        }
                    } else {
                        Main.t.msg(sender, Main.t.rt(Main.lang.getString("CaseNotExist"), "%case:" + casename));
                    }
                } else {
                        Main.t.msg_(sender, Main.t.rt("&aDonateCase " + Main.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                    for (String string : Main.lang.getStringList("Help")) {
                        Main.t.msg_(sender, Main.t.rt(string, "%cmd:" + label));
                    }
                }
            }
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
                                    String casename = parts[1];
                                    int keys;
                                    if (Main.Tconfig) {
                                        keys = customConfig.getKeys().getInt("DonatCase.Cases." + casename + "." + Objects.requireNonNull(player.getName()));
                                    } else {
                                        keys = Main.mysql.getKey(parts[1], Objects.requireNonNull(player.getName()));
                                    }
                                    result = NumberFormat.getNumberInstance().format(keys);
                                }
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', string.replaceAll("%" + placeholder + "%", result)));
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
                                String casename = parts[1];
                                int keys;
                                if (Main.Tconfig) {
                                    keys = customConfig.getKeys().getInt("DonatCase.Cases." + casename + "." + target);
                                } else {
                                    keys = Main.mysql.getKey(parts[1], target);
                                }
                                result = NumberFormat.getNumberInstance().format(keys);
                            }
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', string).replace("%player", target)
                                    .replace("%"+placeholder+"%", result));
                        }
                    } else {
                        Main.t.msg_(sender, Main.t.rt(Main.lang.getString("NoPermission")));
                    }
                }
            }
            //cases
            if (args[0].equalsIgnoreCase("cases")) {
                if (sender.hasPermission("donatecase.mod")) {
                    ConfigurationSection cases_ = customConfig.getConfig().getConfigurationSection("DonatCase.Cases");
                    int num = 0;
                    if (cases_ != null) {
                        for (String casename : cases_.getValues(false).keySet()) {
                            num++;
                            String casetitle = customConfig.getConfig().getString("DonatCase.Cases." + casename + ".Title");
                            Main.t.msg_(sender, Main.t.rt(Main.lang.getString("CasesList"), "%casename:" + casename, "%num:" + num, "%casetitle:" + casetitle));
                        }
                    } else {
                        Main.t.msg_(sender, "null");
                    }
                }
            }
            // opencase
            if (args[0].equalsIgnoreCase("opencase")) {
                if (sender instanceof Player) {
                    String playername = sender.getName();
                    Player player = (Player) sender;
                    if (sender.hasPermission("donatecase.player")) {
                        if (args.length == 2) {
                            String casename = args[1];
                            if (Case.hasCaseByName(casename)) {
                                int keys = Case.getKeys(casename, playername);
                                if (keys >= 1) {
                                    Case.removeKeys(casename, playername, 1);
                                    String winGroup = Tools.getRandomGroup(casename);
                                    Case.onCaseOpenFinish(casename, player, true, winGroup);
                                } else {
                                    Main.t.msg(player, Main.lang.getString("NoKey"));
                                }
                            } else {
                                Main.t.msg(sender, Main.t.rt(Main.lang.getString("CaseNotExist"), "%case:" + casename));
                            }
                        } else {
                            if (sender.hasPermission("donatecase.player") && !sender.hasPermission("donatecase.mod")) {
                                Main.t.msg_(sender, Main.t.rt("&aDonateCase " + Main.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                                for (String string : Main.lang.getStringList("HelpPlayer")) {
                                    Main.t.msg_(sender, Main.t.rt(string, "%cmd:" + label));
                                }
                            } else if (sender.hasPermission("donatecase.mod") || sender.hasPermission("donatecase.admin")) {
                                Main.t.msg_(sender, Main.t.rt("&aDonateCase " + Main.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                                for (String string : Main.lang.getStringList("Help")) {
                                    Main.t.msg_(sender, Main.t.rt(string, "%cmd:" + label));
                                }
                            }
                        }
                    }
                }
            }
            //help
            if (args[0].equalsIgnoreCase("help")) {
                if (sender.hasPermission("donatecase.player") && !sender.hasPermission("donatecase.mod")) {
                        Main.t.msg_(sender, Main.t.rt("&aDonateCase " + Main.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                    for (String string : Main.lang.getStringList("HelpPlayer")) {
                        Main.t.msg_(sender, Main.t.rt(string, "%cmd:" + label));
                    }
                } else if (sender.hasPermission("donatecase.mod") || sender.hasPermission("donatecase.admin")) {
                    Main.t.msg_(sender, Main.t.rt("&aDonateCase " + Main.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                    for (String string : Main.lang.getStringList("Help")) {
                        Main.t.msg_(sender, Main.t.rt(string, "%cmd:" + label));
                    }
                }
            }
            //create
            if (args[0].equalsIgnoreCase("create")) {
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    Location l = player.getTargetBlock(null, 5).getLocation().setDirection(player.getLocation().getDirection());
                    if (sender.hasPermission("donatecase.admin")) {
                        if (args.length >= 3) {
                            String casetype = args[1];
                            String casename = args[2];
                            if (Case.hasCaseByName(casetype)) {
                                if (Case.hasCaseByLocation(l)) {
                                    Main.t.msg(sender, Main.lang.getString("HasDonatCase"));
                                } else {
                                    if(!Case.hasCaseDataByName(casename)) {
                                        Case.saveLocation(casename, casetype, l);
                                        Main.t.msg(sender, Main.lang.getString("AddDonatCase"));
                                    } else {
                                        Main.t.msg(sender, Main.t.rt(Main.lang.getString("CaseAlreadyHasByName"), "%casename:" + casename));
                                    }
                                }
                            } else {
                                Main.t.msg(sender, Main.t.rt(Main.lang.getString("CaseNotExist"), "%case:" + casetype));
                            }
                        } else {
                            Main.t.msg_(sender, Main.t.rt("&aDonateCase " + Main.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                            for (String string : Main.lang.getStringList("Help")) {
                                Main.t.msg_(sender, Main.t.rt(string, "%cmd:" + label));
                            }
                        }
                    } else {
                        Main.t.msg_(sender, Main.t.rt(Main.lang.getString("NoPermission")));
                    }
                }
            }
            //delete
            if (args[0].equalsIgnoreCase("delete")) {
                if (sender.hasPermission("donatecase.admin")) {
                    if (args.length == 1) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            Location l = player.getTargetBlock(null, 5).getLocation();
                            if (Case.hasCaseByLocation(l)) {
                                customConfig.getCases().set("DonatCase.Cases." + Case.getCaseNameByLocation(l), null);
                                customConfig.saveCases();
                                Main.t.msg(sender, Main.lang.getString("RemoveDonatCase"));
                            } else {
                                Main.t.msg(sender, Main.lang.getString("BlockDontDonatCase"));
                            }
                        }
                    } else if (args.length == 2) {
                        String name = args[1];
                        if (com.jodexindustries.donatecase.api.Case.hasCaseDataByName(name)) {
                            customConfig.getCases().set("DonatCase.Cases." + name, null);
                            customConfig.saveCases();
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
                    subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                }
            }
        }
        return true;
    }



    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        ArrayList<String> list;
        ArrayList<String> value = null;
        if (args.length == 1 && sender.hasPermission("donatecase.admin")) {
            list = new ArrayList<>();
            value = new ArrayList<>();
            value.add("help");
            value.add("create");
            value.add("delete");
            value.add("givekey");
            value.add("setkey");
            value.add("reload");
            value.add("keys");
            value.add("opencase");
            value.add("cases");
            value.add("delkey");
            for(String subCommandName : SubCommandManager.getSubCommands().keySet()) {
                SubCommand subCommand = SubCommandManager.getSubCommands().get(subCommandName);
                if(subCommand.getType() == SubCommandType.ADMIN ||
                        subCommand.getType() == SubCommandType.MODER ||
                        subCommand.getType() == SubCommandType.PLAYER) {
                    value.add(subCommandName);
                }
            }
            if (args[0].equals("")) {
                list = value;
            } else {
                for (String tmp : value) {
                    if (tmp.startsWith(args[0])) {
                        list.add(tmp);
                    }
                }
            }

            Collections.sort(list);
            return list;
        } else if (args.length == 1 && sender.hasPermission("donatecase.mod") && !sender.hasPermission("donatecase.admin")) {
            list = new ArrayList<>();
            value = new ArrayList<>();
            value.add("help");
            value.add("givekey");
            value.add("setkey");
            value.add("keys");
            value.add("cases");
            value.add("opencase");
            value.add("delkey");
            for(String subCommandName : SubCommandManager.getSubCommands().keySet()) {
                SubCommand subCommand = SubCommandManager.getSubCommands().get(subCommandName);
                if(subCommand.getType() == SubCommandType.MODER || subCommand.getType() == SubCommandType.PLAYER) {
                    value.add(subCommandName);
                }
            }
            if (args[0].equals("")) {
                list = value;
            } else {
                for (String tmp : value) {
                    if (tmp.startsWith(args[0])) {
                        list.add(tmp);
                    }
                }
            }

            Collections.sort(list);
            return list;
        } else if (args.length == 1 && sender.hasPermission("donatecase.player") && !sender.hasPermission("donatecase.admin") && !sender.hasPermission("donatecase.mod")) {
            list = new ArrayList<>();
            value = new ArrayList<>();
            value.add("help");
            value.add("keys");
            value.add("opencase");
            for(String subCommandName : SubCommandManager.getSubCommands().keySet()) {
                SubCommand subCommand = SubCommandManager.getSubCommands().get(subCommandName);
                if(subCommand.getType() == SubCommandType.PLAYER) {
                    value.add(subCommandName);
                }
            }
            if (args[0].equals("")) {
                list = value;
            } else {
                for (String tmp : value) {
                    if (tmp.startsWith(args[0])) {
                        list.add(tmp);
                    }
                }
            }

            Collections.sort(list);
            return list;
        } else if (args.length >= 1 && !sender.hasPermission("donatecase.player") && !sender.hasPermission("donatecase.admin") && !sender.hasPermission("donatecase.mod")) {
            return new ArrayList<>();
        } else {
            ConfigurationSection section;
            if (args[0].equalsIgnoreCase("create") && sender.hasPermission("donatecase.admin")) {
                list = new ArrayList<>();
                section = customConfig.getConfig().getConfigurationSection("DonatCase.Cases");
                if (section != null) {
                    value = new ArrayList<>(section.getKeys(false));
                } else {
                    value = new ArrayList<>();
                }

                if (args[1].equals("")) {
                    list = value;
                } else {
                    for (String tmp2 : value) {
                        if (tmp2.startsWith(args[1])) {
                            list.add(tmp2);
                        }
                    }
                }

                if (args.length == 3) {
                    return new ArrayList<>();
                } else {
                    Collections.sort(list);
                    return list;
                }
            } else if (args[0].equalsIgnoreCase("opencase") && sender.hasPermission("donatecase.player")) {
                list = new ArrayList<>();
                section = customConfig.getConfig().getConfigurationSection("DonatCase.Cases");
                if (section != null) {
                    value = new ArrayList<>(section.getKeys(false));
                } else {
                    value = new ArrayList<>();
                }
                if (args[1].equals("")) {
                    list = value;
                } else {
                    for (String tmp2 : value) {
                        if (tmp2.startsWith(args[1])) {
                            list.add(tmp2);
                        }
                    }
                }
                if (args.length == 3) {
                    return new ArrayList<>();
                } else {
                    Collections.sort(list);
                    return list;
                }
            }
            else if (args[0].equalsIgnoreCase("delete")) {
                if(args.length == 2) {
                    section = customConfig.getCases().getConfigurationSection("DonatCase.Cases");
                    if(section != null) {
                        value = new ArrayList<>(section.getKeys(false));
                        Collections.sort(value);
                        return value;
                    } else {
                        return new ArrayList<>();
                    }
                }
                return new ArrayList<>();
            } else if (args[0].equalsIgnoreCase("keys")) {
                if (args.length != 2) {
                    return new ArrayList<>();
                } else if (!sender.hasPermission("donatecase.mod")) {
                    return new ArrayList<>();
                } else {
                    list = new ArrayList<>();

                    for (Player o : Bukkit.getOnlinePlayers().stream().filter((px) -> px.getName().startsWith(args[1])).collect(Collectors.toList())) {
                        list.add(o.getName());
                    }

                    return list;
                }
            } else if (args[0].equalsIgnoreCase("help")) {
                return new ArrayList<>();
            } else if (args[0].equalsIgnoreCase("reload")) {
                return new ArrayList<>();
            } else if (args[0].equalsIgnoreCase("cases")) {
                return new ArrayList<>();
                //givekey gk
            } else if (args[0].equalsIgnoreCase("givekey") || args[0].equalsIgnoreCase("gk")) {
                ArrayList<String> b;
                if (!sender.hasPermission("donatecase.mod")) {
                    return new ArrayList<>();
                } else {
                    list = new ArrayList<>();
                    value = new ArrayList<>();
                    section = customConfig.getConfig().getConfigurationSection("DonatCase.Cases");
                    if (section != null) {
                        value.addAll(section.getKeys(false));
                    }
                    // playerlist
                    if (args.length == 2) {
                        b = new ArrayList<>();
                        for (Player player : Bukkit.getOnlinePlayers().stream().filter((px) -> px.getName().startsWith(args[1])).collect(Collectors.toList())) {
                            b.add(player.getName());
                        }

                        return b;
                    } else {
                        if (args[2].equals("")) {
                            list = value;
                        } else {

                            for (String tmp2 : value) {
                                if (tmp2.startsWith(args[2])) {
                                    list.add(tmp2);
                                }
                            }
                        }

                        if (args.length >= 4) {
                            return new ArrayList<>();
                        } else {
                            Collections.sort(list);
                            return list;
                        }
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("setkey") || args[0].equalsIgnoreCase("sk")) {
                ArrayList<String> b;
                if (!sender.hasPermission("donatecase.mod")) {
                    return new ArrayList<>();
                } else {
                    list = new ArrayList<>();
                    value = new ArrayList<>();
                    section = customConfig.getConfig().getConfigurationSection("DonatCase.Cases");
                    if (section != null) {
                        value.addAll(section.getKeys(false));
                    }
                    // playerlist
                    if (args.length == 2) {
                        b = new ArrayList<>();
                        for (Player player : Bukkit.getOnlinePlayers().stream().filter((px) -> px.getName().startsWith(args[1])).collect(Collectors.toList())) {
                            b.add(player.getName());
                        }

                        return b;
                    } else {
                        if (args[2].equals("")) {
                            list = value;
                        } else {

                            for (String tmp2 : value) {
                                if (tmp2.startsWith(args[2])) {
                                    list.add(tmp2);
                                }
                            }
                        }

                        if (args.length >= 4) {
                            return new ArrayList<>();
                        } else {
                            Collections.sort(list);
                            return list;
                        }
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("delkey") || args[0].equalsIgnoreCase("dk")) {
                // delkey all
                if (args[1].equalsIgnoreCase("all")) {
                    return new ArrayList<>();
                }
                ArrayList<String> b;
                list = new ArrayList<>();
                section = customConfig.getConfig().getConfigurationSection("DonatCase.Cases");
                if (section != null) {
                    value = new ArrayList<>(section.getKeys(false));
                }


                if (args.length == 2) {
                    b = new ArrayList<>();
                    for (Player player: Bukkit.getOnlinePlayers().stream().filter((px) -> px.getName().startsWith(args[1])).collect(Collectors.toList())) {
                        b.add(player.getName());
                    }

                    return b;
                } else {
                    if (args[2].equals("")) {
                        list = value;
                    } else {
                        for (String tmp2 : value) {
                            if (tmp2.startsWith(args[2])) {
                                list.add(tmp2);
                            }
                        }
                    }

                    if (args.length >= 4) {
                        return new ArrayList<>();
                    } else {
                        Collections.sort(list);
                        return list;
                    }
                }
            } else if (SubCommandManager.getSubCommands().get(args[0]) != null){
                return getTabCompletionsForSubCommand(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
            } else {
                return new ArrayList<>();
            }
        }
    }

}
