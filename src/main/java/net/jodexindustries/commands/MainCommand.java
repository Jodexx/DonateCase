package net.jodexindustries.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import me.clip.placeholderapi.PlaceholderAPI;
import net.jodexindustries.commands.executor.DCCommand;
import net.jodexindustries.dc.Case;
import net.jodexindustries.dc.DonateCase;
import net.jodexindustries.tools.CustomConfig;
import net.jodexindustries.tools.Languages;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class MainCommand extends DCCommand {
    public MainCommand() {
        super("donatcase");
    }
    public boolean run(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            // if sender is player
            if (sender instanceof Player) {
                if (sender.hasPermission("donatecase.player") && !sender.hasPermission("donatecase.mod")) {
                        DonateCase.t.msg_(sender, DonateCase.t.rt("&aDonateCase " + DonateCase.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                    for (String string : DonateCase.lang.getStringList("HelpPlayer")) {
                        DonateCase.t.msg_(sender, DonateCase.t.rt(string, "%cmd:" + label));
                    }
                    return false;
                } else if (sender.hasPermission("donatecase.mod")) {
                        DonateCase.t.msg_(sender, DonateCase.t.rt("&aDonateCase " + DonateCase.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                    for (String string : DonateCase.lang.getStringList("Help")) {
                        DonateCase.t.msg_(sender, DonateCase.t.rt(string, "%cmd:" + label));
                    }
                    return false;
                } else {
                    DonateCase.t.msg_(sender, DonateCase.t.rt(DonateCase.lang.getString("NoPermission")));
                }
                // if sender not player
            } else {
                DonateCase.t.msg_(sender, DonateCase.t.rt("&aDonateCase &7by &c_Jodex__"));
                for (String string : DonateCase.lang.getStringList("Help")) {
                    DonateCase.t.msg_(sender, DonateCase.t.rt(string, "%cmd:" + label));
                }
            }
        }
        if (args.length >= 1) {
            //reload command
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("donatecase.admin")) {
                    CustomConfig.setup();
                    DonateCase.lang = (new Languages(CustomConfig.getConfig().getString("DonatCase.Languages"))).getLang();
                    DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.lang.getString("ReloadConfig")));
                } else {
                    DonateCase.t.msg_(sender, DonateCase.t.rt(DonateCase.lang.getString("NoPermission")));
                }
            }
            //givekey command
            if (args[0].equalsIgnoreCase("givekey") || args[0].equalsIgnoreCase("gk")) {
                if(sender.hasPermission("donatecase.mod")) {
                        if (args.length >= 4) {
                            try {
                                String player = args[1];
                                String casename = args[2];
                                Player target = Bukkit.getPlayer(player);
                                int keys = Integer.parseInt(args[3]);
                                if (Case.hasCaseByName(casename)) {
                                    String casetitle = CustomConfig.getConfig().getString("DonatCase.Cases." + casename + ".Title");
                                    Case.addKeys(casename, player, keys);
                                    DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.lang.getString("GiveKeys"), "%player:" + player, "%key:" + keys, "%casetitle:" + casetitle, "%case:" + casename));
                                    if (CustomConfig.getConfig().getBoolean("DonatCase.SetKeysTargetMessage")) {
                                        DonateCase.t.msg(target, DonateCase.t.rt(DonateCase.lang.getString("GiveKeysTarget"), "%player:" + player, "%key:" + keys, "%casetitle:" + casetitle, "%case:" + casename));
                                    }
                                } else {
                                    DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.lang.getString("CaseNotExist"), "%case:" + casename));
                                }
                            } catch (Exception var12) {
                                    DonateCase.t.msg_(sender, DonateCase.t.rt("&aDonateCase " + DonateCase.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                                for (String string : DonateCase.lang.getStringList("Help")) {
                                    DonateCase.t.msg_(sender, DonateCase.t.rt(string, "%cmd:" + label));
                                }
                            }
                        } else {
                                DonateCase.t.msg_(sender, DonateCase.t.rt("&aDonateCase " + DonateCase.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                            for (String string : DonateCase.lang.getStringList("Help")) {
                                DonateCase.t.msg_(sender, DonateCase.t.rt(string, "%cmd:" + label));
                            }
                        }
                } else {
                    DonateCase.t.msg_(sender, DonateCase.t.rt(DonateCase.lang.getString("NoPermission")));
                }
            }
            //delkey
            if(args[0].equalsIgnoreCase("delkey") || args[0].equalsIgnoreCase("dk")) {
                if (sender.hasPermission("donatecase.admin") || sender.hasPermission("donatecase.mod")) {
                    if(args.length == 1) {
                            DonateCase.t.msg_(sender, DonateCase.t.rt("&aDonateCase " + DonateCase.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                        for (String string : DonateCase.lang.getStringList("Help")) {
                            DonateCase.t.msg_(sender, DonateCase.t.rt(string, "%cmd:" + label));
                        }
                        return false;
                    }
                    if (args[1].equalsIgnoreCase("all")) {
                        if (DonateCase.Tconfig) {
                            CustomConfig.getKeys().set("DonatCase.Cases", null);
                            CustomConfig.saveKeys();
                            DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.lang.getString("ClearAllKeys"), new String[0]));
                        } else {
                            DonateCase.mysql.delAllKey();
                            DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.lang.getString("ClearAllKeys"), new String[0]));
                        }
                        return false;
                    }
                }
                if (args.length >= 3) {
                    try {
                        String player = args[1];
                        String casename = args[2];
                        if (Case.hasCaseByName(casename)) {
                            String casetitle = CustomConfig.getConfig().getString("DonatCase.Cases." + casename + ".Title");
                            Case.setNullKeys(casename, player);
                            DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.lang.getString("ClearKeys"), "%player:" + player, "%casetitle:" + casetitle, "%case:" + casename));
                        } else {
                            DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.lang.getString("CaseNotExist"), "%case:" + casename));
                        }
                    } catch (Exception var11) {
                            DonateCase.t.msg_(sender, DonateCase.t.rt("&aDonateCase " + DonateCase.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                        for (String string : DonateCase.lang.getStringList("Help")) {
                            DonateCase.t.msg_(sender, DonateCase.t.rt(string, "%cmd:" + label));
                        }
                    }
                } else {
                        DonateCase.t.msg_(sender, DonateCase.t.rt("&aDonateCase " + DonateCase.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                    for (String string : DonateCase.lang.getStringList("Help")) {
                        DonateCase.t.msg_(sender, DonateCase.t.rt(string, "%cmd:" + label));
                    }
                }
            }
            //setkey
            if(args[0].equalsIgnoreCase("setkey") || args[0].equalsIgnoreCase("sk")) {
                if (!sender.hasPermission("donatecase.admin") && !sender.hasPermission("donatecase.mod")) {
                    DonateCase.t.msg_(sender, DonateCase.t.rt(DonateCase.lang.getString("NoPermission")));
                } else if (args.length >= 4) {
                    try {
                        String player = args[1];
                        String casename = args[2];
                        Player target = Bukkit.getPlayer(player);
                        int keys = Integer.parseInt(args[3]);
                        if (Case.hasCaseByName(casename)) {
                            String casetitle = CustomConfig.getConfig().getString("DonatCase.Cases." + casename + ".Title");
                            Case.setKeys(casename, player, keys);
                            DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.lang.getString("SetKeys"), "%player:" + player, "%key:" + keys, "%casetitle:" + casetitle, "%case:" + casename));
                            if (CustomConfig.getConfig().getBoolean("DonatCase.SetKeysTargetMessage")) {
                                DonateCase.t.msg(target, DonateCase.t.rt(DonateCase.lang.getString("SetKeysTarget"), "%player:" + player, "%key:" + keys, "%casetitle:" + casetitle, "%case:" + casename));
                            }
                        } else {
                            DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.lang.getString("CaseNotExist"), "%case:" + casename));
                        }
                    } catch (Exception var10) {
                        DonateCase.t.msg_(sender, DonateCase.t.rt("&aDonateCase " + DonateCase.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                        for (String string : DonateCase.lang.getStringList("Help")) {
                            DonateCase.t.msg_(sender, DonateCase.t.rt(string, "%cmd:" + label));
                        }
                    }
                } else {
                        DonateCase.t.msg_(sender, DonateCase.t.rt("&aDonateCase " + DonateCase.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                    for (String string : DonateCase.lang.getStringList("Help")) {
                        DonateCase.t.msg_(sender, DonateCase.t.rt(string, "%cmd:" + label));
                    }
                }
                return false;
            }
            //keys
            if (args[0].equalsIgnoreCase("keys")) {
                Player player = (Player) sender;
                if (args.length < 2) {
                    if (sender.hasPermission("donatecase.player")) {

                        for (String string : DonateCase.lang.getStringList("MyKeys")) {
                            sender.sendMessage(PlaceholderAPI.setPlaceholders(player.getPlayer(), ChatColor.translateAlternateColorCodes('&', string)));
                        }
                    }
                } else {
                    if (sender.hasPermission("donatecase.mod")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target == null) {
                            DonateCase.t.msg_(sender, DonateCase.t.rt(DonateCase.lang.getString("PlayerNotFound"), "%player:" + player));
                            return true;
                        }

                        for (String string : DonateCase.lang.getStringList("PlayerKeys")) {
                            sender.sendMessage(PlaceholderAPI.setPlaceholders(target.getPlayer(), ChatColor.translateAlternateColorCodes('&', string).replace("%player", target.getName())));
                        }
                    } else {
                        DonateCase.t.msg_(sender, DonateCase.t.rt(DonateCase.lang.getString("NoPermission")));
                    }
                }
                return false;
            }
            if (args[0].equalsIgnoreCase("help")) {
                if (sender.hasPermission("donatecase.player")) {
                        DonateCase.t.msg_(sender, DonateCase.t.rt("&aDonateCase " + DonateCase.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                    for (String string : DonateCase.lang.getStringList("HelpPlayer")) {
                        DonateCase.t.msg_(sender, DonateCase.t.rt(string, "%cmd:" + label));
                    }
                } else if (sender.hasPermission("donatecase.mod") || sender.hasPermission("donatecase.admin")) {
                    for (String string : DonateCase.lang.getStringList("Help")) {
                        DonateCase.t.msg_(sender, DonateCase.t.rt(string, "%cmd:" + label));
                    }
                }
            }
            if (args[0].equalsIgnoreCase("create")) {
                Player player = (Player)sender;
                Location l = player.getTargetBlock(null, 5).getLocation();
                String locat = l.toString();
                if (sender.hasPermission("donatecase.admin")) {
                    if (args.length >= 3) {
                        String casetype = args[1];
                        String casename = args[2];
                        if (Case.hasCaseByName(casetype)) {
                            if (Case.hasCaseByLocation(locat)) {
                                DonateCase.t.msg(sender, DonateCase.lang.getString("HasDonatCase"));
                            } else {
                                Case.saveLocation(casename, casetype, locat);
                                DonateCase.t.msg(sender, DonateCase.lang.getString("AddDonatCase"));
                            }
                        } else {
                            DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.lang.getString("CaseNotExist"), "%case:" + casetype));
                        }
                    } else {
                            DonateCase.t.msg_(sender, DonateCase.t.rt("&aDonateCase " + DonateCase.instance.getDescription().getVersion() + " &7by &c_Jodex__"));
                        for (String string : DonateCase.lang.getStringList("Help")) {
                            DonateCase.t.msg_(sender, DonateCase.t.rt(string, "%cmd:" + label));
                        }
                    }
                } else {
                    DonateCase.t.msg_(sender, DonateCase.t.rt(DonateCase.lang.getString("NoPermission")));
                }
            }
            if (args[0].equalsIgnoreCase("delete")) {
                Player player = (Player)sender;
                Location l = player.getTargetBlock(null, 5).getLocation();
                String locat = l.toString();
                if (sender.hasPermission("donatecase.admin")) {
                    if (args.length == 1) {
                        if (Case.hasCaseByLocation(locat)) {
                            CustomConfig.getCases().set("DonatCase.Cases." + Case.getCaseNameByLocation(locat), null);
                            CustomConfig.saveCases();
                            DonateCase.t.msg(sender, DonateCase.lang.getString("RemoveDonatCase"));
                        } else {
                            DonateCase.t.msg(sender, DonateCase.lang.getString("BlockDontDonatCase"));
                        }
                    } else if (args.length == 2) {
                        String name = args[1];
                        if (Case.hasCaseDataByName(name)) {
                            CustomConfig.getCases().set("DonatCase.Cases." + name, null);
                            CustomConfig.saveCases();
                            DonateCase.t.msg(sender, DonateCase.lang.getString("RemoveDonatCase"));
                        } else {
                            DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.lang.getString("CaseNotExist"), "%case:" + name));
                        }
                    }
                } else {
                    DonateCase.t.msg_(sender, DonateCase.t.rt(DonateCase.lang.getString("NoPermission")));
                }
            }
        }
        return false;
    }
    public ArrayList onTabComplete(CommandSender sender, Player $p, Command $cmd, String $label, String[] args) {
        ArrayList<String> list;
        ArrayList<String> value;
        Iterator<String> var14;
        String tmp;
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
            value.add("delkey");
            if (args[0].equals("")) {
                list = value;
            } else {
                var14 = value.iterator();

                while(var14.hasNext()) {
                    tmp = var14.next();
                    if (tmp.startsWith(args[0].toLowerCase())) {
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
            value.add("delkey");
            if (args[0].equals("")) {
                list = value;
            } else {
                var14 = value.iterator();

                while(var14.hasNext()) {
                    tmp = var14.next();
                    if (tmp.startsWith(args[0].toLowerCase())) {
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
            if (args[0].equals("")) {
                list = value;
            } else {
                var14 = value.iterator();

                while(var14.hasNext()) {
                    tmp = var14.next();
                    if (tmp.startsWith(args[0].toLowerCase())) {
                        list.add(tmp);
                    }
                }
            }

            Collections.sort(list);
            return list;
        } else if (args.length >= 1 && !sender.hasPermission("donatecase.player") && !sender.hasPermission("donatecase.admin") && !sender.hasPermission("donatecase.mod")) {
            return new ArrayList();
        } else {
            ConfigurationSection section;
            Iterator<String> var9;
            String tmp2;
            if (args[0].equalsIgnoreCase("create") && sender.hasPermission("donatecase.admin")) {
                list = new ArrayList<>();
                value = new ArrayList<>();
                section = CustomConfig.getConfig().getConfigurationSection("DonatCase.Cases");
                var9 = section.getKeys(false).iterator();

                while(var9.hasNext()) {
                    tmp2 = var9.next();
                    value.add(tmp2.toLowerCase());
                }

                if (args[1].equals("")) {
                    list = value;
                } else {
                    var9 = value.iterator();

                    while(var9.hasNext()) {
                        tmp2 = var9.next();
                        if (tmp2.startsWith(args[1].toLowerCase())) {
                            list.add(tmp2.toLowerCase());
                        }
                    }
                }

                if (args.length == 3) {
                    return new ArrayList();
                } else {
                    Collections.sort(list);
                    return list;
                }
            } else if (args[0].equalsIgnoreCase("delete")) {
                return new ArrayList();
            } else if (args[0].equalsIgnoreCase("keys")) {
                if (args.length != 2) {
                    return new ArrayList();
                } else if (!sender.hasPermission("donatecase.mod")) {
                    return new ArrayList();
                } else {
                    list = new ArrayList<>();

                    for (Player o : Bukkit.getOnlinePlayers().stream().filter((px) -> px.getName().startsWith(args[1])).collect(Collectors.toList())) {
                        Player p = o;
                        list.add(p.getName());
                    }

                    return list;
                }
            } else if (args[0].equalsIgnoreCase("help")) {
                return new ArrayList();
            } else if (args[0].equalsIgnoreCase("reload")) {
                return new ArrayList();
            } else {
                Player p;
                ArrayList<String> b;
                Iterator var16;
                if (!args[0].equalsIgnoreCase("givekey") && !args[0].equalsIgnoreCase("setkey") && !args[0].equalsIgnoreCase("gk") && !args[0].equalsIgnoreCase("sk")) {
                    if (!args[0].equalsIgnoreCase("delkey") && !args[0].equalsIgnoreCase("dk")) {
                        return (ArrayList) Arrays.asList("help", "create", "delete", "givekey", "setkey", "reload", "mykeys", "delkey");
                    } else if (args[1].equalsIgnoreCase("all")) {
                        return new ArrayList<>();
                    } else {
                        list = new ArrayList<>();
                        value = new ArrayList<>();
                        section = CustomConfig.getConfig().getConfigurationSection("DonatCase.Cases");
                        var9 = section.getKeys(false).iterator();

                        while(var9.hasNext()) {
                            tmp2 = var9.next();
                            value.add(tmp2.toLowerCase());
                        }

                        if (args.length == 2) {
                            b = new ArrayList<>();
                            var16 = ((List)Bukkit.getOnlinePlayers().stream().filter((px) -> px.getName().startsWith(args[1])).collect(Collectors.toList())).iterator();

                            while(var16.hasNext()) {
                                p = (Player)var16.next();
                                b.add(p.getName());
                            }

                            return b;
                        } else {
                            if (args[2].equals("")) {
                                list = value;
                            } else {
                                var9 = value.iterator();

                                while(var9.hasNext()) {
                                    tmp2 = var9.next();
                                    if (tmp2.startsWith(args[1].toLowerCase())) {
                                        list.add(tmp2.toLowerCase());
                                    }
                                }
                            }

                            Collections.sort(list);
                            return list;
                        }
                    }
                } else if (!sender.hasPermission("donatecase.mod")) {
                    return new ArrayList();
                } else {
                    list = new ArrayList<String>();
                    value = new ArrayList<>();
                    section = CustomConfig.getConfig().getConfigurationSection("DonatCase.Cases");
                    var9 = section.getKeys(false).iterator();

                    while(var9.hasNext()) {
                        tmp2 = var9.next();
                        value.add(tmp2.toLowerCase());
                    }

                    if (args.length == 2) {
                        b = new ArrayList<>();
                        var16 = ((List)Bukkit.getOnlinePlayers().stream().filter((px) -> px.getName().startsWith(args[1])).collect(Collectors.toList())).iterator();

                        while(var16.hasNext()) {
                            p = (Player)var16.next();
                            b.add(p.getName());
                        }

                        return b;
                    } else {
                        if (args[2].equals("")) {
                            list = value;
                        } else {
                            var9 = value.iterator();

                            while(var9.hasNext()) {
                                tmp2 = var9.next();
                                if (tmp2.startsWith(args[1].toLowerCase())) {
                                    list.add(tmp2.toLowerCase());
                                }
                            }
                        }

                        if (args.length == 4) {
                            return new ArrayList();
                        } else {
                            Collections.sort(list);
                            return list;
                        }
                    }
                }
            }
        }
    }
}
