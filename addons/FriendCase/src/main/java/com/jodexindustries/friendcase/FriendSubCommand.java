package com.jodexindustries.friendcase;

import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.api.events.CaseGiftEvent;
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
        if (!(sender instanceof Player)) {
            sender.sendMessage(rc(t.getConfig().getConfig().getString("Messages.OnlyPlayers", "")));
            return;
        }
        Player p = (Player) sender;
        if (args.length == 0) {
            sendHelp(sender);
        } else {
            if (args.length < 3) {
                sendHelp(sender);
                return;
            }

            Player target = Bukkit.getPlayerExact(args[0]);
            String caseType = args[1];
            int keys;
            try {
                keys = Math.abs(Integer.parseInt(args[2]));
            } catch (NumberFormatException e) {
                sender.sendMessage(rc(t.getConfig().getConfig().getString("Messages.NumberFormat", "")));
                return;
            }

            if (t.getConfig().getConfig().getStringList("BlackList").contains(caseType)) {
                sender.sendMessage(rc(t.getConfig().getConfig().getString("Messages.CaseInBlackList", "")));
                return;
            }

            CaseDataBukkit caseData = t.getDCAPI().getCaseManager().getCase(caseType);

            if (caseData == null) {
                sender.sendMessage(rc(t.getConfig().getConfig().getString("Messages.CaseNotFound", "")));
                return;
            }

            int playerKeys = t.getDCAPI().getCaseKeyManager().getKeys(caseType, p.getName());

            if (playerKeys < 1 || playerKeys < keys) {
                sender.sendMessage(rc(
                        t.getConfig().getConfig().getString("Messages.MinNumber", "")
                                .replace("%required%", keys + "")
                ));
                return;
            }


            if (target == null) {
                sender.sendMessage(rc(t.getConfig().getConfig().getString("Messages.PlayerNotFound", "")));
                return;
            }

            if (target == p) {
                sender.sendMessage(rc(t.getConfig().getConfig().getString("Messages.GiftYourself", "")));
                return;
            }

            t.getDCAPI().getCaseKeyManager().removeKeys(caseType, p.getName(), keys).thenAcceptAsync(status -> {

                if (status != DatabaseStatus.COMPLETE) {
                    return;
                }

                t.getDCAPI().getCaseKeyManager().addKeys(caseType, target.getName(), keys).thenAcceptAsync(nextStatus -> {

                    if (nextStatus != DatabaseStatus.COMPLETE) {
                        return;
                    }

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

                    Bukkit.getScheduler().runTask(t.getDCAPI().getDonateCase(), () -> {
                        CaseGiftEvent event = new CaseGiftEvent(p, target, caseData, keys);
                        Bukkit.getPluginManager().callEvent(event);
                    });
                });
            });

        }
    }

    private void sendHelp(CommandSender sender) {
        for (String msg : t.getConfig().getConfig().getStringList("Messages.Help")) {
            sender.sendMessage(rc(msg));
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        List<String> strings = new ArrayList<>();
        if (args.length == 1) {
            strings.addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
        } else if (args.length == 2) {
            strings.addAll(t.getDCAPI().getCaseManager().getMap().keySet());
        }
        return strings;
    }
}