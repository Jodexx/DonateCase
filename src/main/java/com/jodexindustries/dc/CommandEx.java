package com.jodexindustries.dc;

import com.jodexindustries.tools.CustomConfig;
import me.clip.placeholderapi.PlaceholderAPI;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
                            if (Case.hasCaseByName(casename)) {
                                String casetitle = CustomConfig.getConfig().getString("DonatCase.Cases." + casename + ".Title");
                                Case.addKeys(casename, player, keys);
                                Main.t.msg(sender, Main.t.rt(Main.lang.getString("GiveKeys"), "%player:" + player, "%key:" + keys, "%casetitle:" + casetitle, "%case:" + casename));
                                if (CustomConfig.getConfig().getBoolean("DonatCase.SetKeysTargetMessage")) {
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
                                CustomConfig.getKeys().set("DonatCase.Cases", null);
                                CustomConfig.saveKeys();
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
                        if (Case.hasCaseByName(casename)) {
                            String casetitle = CustomConfig.getConfig().getString("DonatCase.Cases." + casename + ".Title");
                            Case.setNullKeys(casename, player);
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
                    if (Case.hasCaseByName(casename)) {
                        String casetitle = CustomConfig.getConfig().getString("DonatCase.Cases." + casename + ".Title");
                        Case.setKeys(casename, player, keys);
                        Main.t.msg(sender, Main.t.rt(Main.lang.getString("SetKeys"), "%player:" + player, "%key:" + keys, "%casetitle:" + casetitle, "%case:" + casename));
                        if (CustomConfig.getConfig().getBoolean("DonatCase.SetKeysTargetMessage")) {
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
                                sender.sendMessage(PlaceholderAPI.setPlaceholders(player.getPlayer(),
                                        ChatColor.translateAlternateColorCodes('&', string)));
                            }
                        }
                    }
                } else {
                    if (sender.hasPermission("donatecase.mod")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target == null) {
                            Main.t.msg_(sender, Main.t.rt(Main.lang.getString("PlayerNotFound"), "%player:" + args[1]));
                            return true;
                        }
                        //Get player keys
                        for (String string : Main.lang.getStringList("PlayerKeys")) {
                            sender.sendMessage(PlaceholderAPI.setPlaceholders(target.getPlayer(),
                                    ChatColor.translateAlternateColorCodes('&', string).replace("%player", target.getName())));
                        }
                    } else {
                        Main.t.msg_(sender, Main.t.rt(Main.lang.getString("NoPermission")));
                    }
                }
            }
            //cases
            if (args[0].equalsIgnoreCase("cases")) {
                if (sender.hasPermission("donatecase.mod")) {
                    ConfigurationSection cases_ = CustomConfig.getConfig().getConfigurationSection("DonatCase.Cases");
                    int num = 0;
                    if (cases_ != null) {
                        for (String casename : cases_.getValues(false).keySet()) {
                            num++;
                            String casetitle = CustomConfig.getConfig().getString("DonatCase.Cases." + casename + ".Title");
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
                                    String winGroup = Main.t.getRandomGroup(casename);
                                    Main.t.onCaseOpenFinish(casename, player, true, winGroup);
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
                    Location l = player.getTargetBlock(null, 5).getLocation();
                    String locat = l.toString();
                    if (sender.hasPermission("donatecase.admin")) {
                        if (args.length >= 3) {
                            String casetype = args[1];
                            String casename = args[2];
                            if (Case.hasCaseByName(casetype)) {
                                if (Case.hasCaseByLocation(locat)) {
                                    Main.t.msg(sender, Main.lang.getString("HasDonatCase"));
                                } else {
                                    if(!Case.hasCaseDataByName(casename)) {
                                        Case.saveLocation(casename, casetype, locat);
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
                            String locat = l.toString();
                            if (Case.hasCaseByLocation(locat)) {
                                CustomConfig.getCases().set("DonatCase.Cases." + Case.getCaseNameByLocation(locat), null);
                                CustomConfig.saveCases();
                                Main.t.msg(sender, Main.lang.getString("RemoveDonatCase"));
                            } else {
                                Main.t.msg(sender, Main.lang.getString("BlockDontDonatCase"));
                            }
                        }
                    } else if (args.length == 2) {
                        String name = args[1];
                        if (Case.hasCaseDataByName(name)) {
                            CustomConfig.getCases().set("DonatCase.Cases." + name, null);
                            CustomConfig.saveCases();
                            Main.t.msg(sender, Main.lang.getString("RemoveDonatCase"));
                        } else {
                            Main.t.msg(sender, Main.t.rt(Main.lang.getString("CaseNotExist"), "%case:" + name));
                        }
                    }
                } else {
                    Main.t.msg_(sender, Main.t.rt(Main.lang.getString("NoPermission")));
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
                section = CustomConfig.getConfig().getConfigurationSection("DonatCase.Cases");
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
                section = CustomConfig.getConfig().getConfigurationSection("DonatCase.Cases");
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
                    section = CustomConfig.getCases().getConfigurationSection("DonatCase.Cases");
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
                    section = CustomConfig.getConfig().getConfigurationSection("DonatCase.Cases");
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
                    section = CustomConfig.getConfig().getConfigurationSection("DonatCase.Cases");
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
                section = CustomConfig.getConfig().getConfigurationSection("DonatCase.Cases");
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
            } else {
                return new ArrayList<>();
            }
        }
    }

}
