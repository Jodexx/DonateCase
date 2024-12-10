package com.jodexindustries.friendcase;

import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.friendcase.utils.Tools;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jodexindustries.donatecase.tools.DCToolsBukkit.rc;

public class FriendSubCommand implements SubCommandExecutor<CommandSender>, SubCommandTabCompleter<CommandSender> {
    private final Tools t;

    public FriendSubCommand(Tools t) {
        this.t = t;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 0) {
                sendHelp(sender);
            } else {
                if (args.length < 3) {
                    if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("reload")) {
                            if (sender.hasPermission("donatecase.admin")) {
                                t.getConfig().reloadConfig();
                                sender.sendMessage(rc(
                                        t.getConfig().getConfig().getString("Messages.ConfigReloaded", "")
                                ));
                            }
                        } else {
                            sendHelp(sender);
                        }
                    }
                } else {
                    Player target = Bukkit.getPlayerExact(args[0]);
                    String caseType = args[1];
                    int keys;
                    try {
                        keys = Math.abs(Integer.parseInt(args[2]));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(rc(t.getConfig().getConfig().getString("Messages.NumberFormat", "")));
                        return;
                    }
                    if (!t.getConfig().getConfig().getStringList("BlackList").contains(caseType)) {
                        if (t.getDCAPI().getCaseManager().hasCaseByType(caseType)) {
                            if (t.getDCAPI().getCaseKeyManager().getKeys(caseType, p.getName()) >= 1 && t.getDCAPI().getCaseKeyManager().getKeys(caseType, p.getName()) >= keys) {
                                if (target != null) {
                                    if (target != p) {
                                        t.getDCAPI().getCaseKeyManager().removeKeys(caseType, p.getName(), keys).thenAcceptAsync(status -> {
                                            if (status == DatabaseStatus.COMPLETE) {
                                                t.getDCAPI().getCaseKeyManager().addKeys(caseType, target.getName(), keys).thenAcceptAsync(nextStatus -> {
                                                    if (nextStatus == DatabaseStatus.COMPLETE) {
                                                        target.sendMessage(rc(
                                                                t.getConfig().getConfig().getString("Messages.YouReceivedGift", "")
                                                                        .replace("%sender%", sender.getName())
                                                                        .replace("%target%", target.getName())
                                                                        .replace("%keys%", keys + "")
                                                                        .replace("%case%", caseType)
                                                        ));
                                                        sender.sendMessage(rc(
                                                                t.getConfig().getConfig().getString("Messages.YouSendGift", "")
                                                                        .replace("%target%", target.getName())
                                                                        .replace("%sender%", sender.getName())
                                                                        .replace("%keys%", keys + "")
                                                                        .replace("%case%", caseType)
                                                        ));
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        sender.sendMessage(rc(
                                                t.getConfig().getConfig().getString("Messages.GiftYourself", "")));
                                    }
                                } else {
                                    sender.sendMessage(rc(
                                            t.getConfig().getConfig().getString("Messages.PlayerNotFound", "")));
                                }
                            } else {
                                sender.sendMessage(rc(
                                        t.getConfig().getConfig().getString("Messages.MinNumber", "")
                                                .replace("%required%", keys + "")
                                ));
                            }
                        } else {
                            sender.sendMessage(rc(t.getConfig().getConfig().getString("Messages.CaseNotFound", "")));
                        }
                    } else {
                        sender.sendMessage(rc(t.getConfig().getConfig().getString("Messages.CaseInBlackList", "")));
                    }
                }
            }
        } else {
            sender.sendMessage(rc(t.getConfig().getConfig().getString("Messages.OnlyPlayers", "")));
        }
    }

    private void sendHelp(CommandSender sender) {
        for (String msg : t.getConfig().getConfig().getStringList("Messages.Help")) {
            sender.sendMessage(rc(msg));
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        List<String> strings;
        if (args.length == 1) {
            strings = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
            return strings;
        } else {
            if (args.length == 2) {
                strings = new ArrayList<>(t.getDCAPI().getCaseManager().getMap().keySet());
                return strings;
            }
        }
        return new ArrayList<>();
    }
}
