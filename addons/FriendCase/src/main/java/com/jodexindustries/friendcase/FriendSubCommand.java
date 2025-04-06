package com.jodexindustries.friendcase;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.api.event.CaseGiftEvent;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.tools.Placeholder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.jodexindustries.donatecase.api.tools.DCTools.rc;
import static com.jodexindustries.donatecase.api.tools.DCTools.rt;

public class FriendSubCommand implements SubCommandExecutor, SubCommandTabCompleter {
    private final MainAddon addon;

    public FriendSubCommand(MainAddon addon) {
        this.addon = addon;
    }

    @Override
    public boolean execute(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        if (!(sender instanceof DCPlayer)) {
            sender.sendMessage(rc(addon.config.getString("Messages", "OnlyPlayers")));
            return true;
        }
        DCPlayer p = (DCPlayer) sender;
        if (args.length == 0) {
            sendHelp(sender);
        } else {
            if (args.length < 3) {
                sendHelp(sender);
                return true;
            }

            DCPlayer target = addon.api.getPlatform().getPlayer(args[0]);
            String caseType = args[1];
            int keys;
            try {
                keys = Math.abs(Integer.parseInt(args[2]));
            } catch (NumberFormatException e) {
                sender.sendMessage(rc(addon.config.getString("Messages", "NumberFormat")));
                return true;
            }

            if (addon.config.getList("BlackList").contains(caseType)) {
                sender.sendMessage(rc(addon.config.getString("Messages", "CaseInBlackList")));
                return true;
            }

            CaseData caseData = addon.api.getCaseManager().get(caseType);

            if (caseData == null) {
                sender.sendMessage(rc(addon.config.getString("Messages", "CaseNotFound")));
                return true;
            }

            int playerKeys = addon.api.getCaseKeyManager().get(caseType, p.getName());

            if (playerKeys < 1 || playerKeys < keys) {
                sender.sendMessage(
                        rt(addon.config.getString("Messages", "MinNumber"), Placeholder.of("%required%", keys))
                );
                return true;
            }


            if (target == null) {
                sender.sendMessage(rc(addon.config.getString("Messages", "PlayerNotFound")));
                return true;
            }

            if (target == p) {
                sender.sendMessage(rc(addon.config.getString("Messages", "GiftYourself")));
                return true;
            }

            addon.api.getCaseKeyManager().remove(caseType, p.getName(), keys).thenAcceptAsync(status -> {

                if (status != DatabaseStatus.COMPLETE) {
                    return;
                }

                addon.api.getCaseKeyManager().add(caseType, target.getName(), keys).thenAcceptAsync(nextStatus -> {

                    if (nextStatus != DatabaseStatus.COMPLETE) {
                        return;
                    }

                    target.sendMessage(rt(
                            addon.config.getString("Messages", "YouReceivedGift"),
                            Placeholder.of("%sender%", sender.getName()),
                            Placeholder.of("%target%", target.getName()),
                            Placeholder.of("%keys%", keys),
                            Placeholder.of("%case%", caseType)
                    ));
                    sender.sendMessage(rt(
                            addon.config.getString("Messages", "YouSendGift"),
                            Placeholder.of("%sender%", sender.getName()),
                            Placeholder.of("%target%", target.getName()),
                            Placeholder.of("%keys%", keys),
                            Placeholder.of("%case%", caseType)
                    ));

                    addon.api.getEventBus().post(new CaseGiftEvent(p, target, caseData, keys));
                });
            });

        }

        return true;
    }

    private void sendHelp(DCCommandSender sender) {
        for (String msg : addon.config.getList("Messages", "Help")) {
            sender.sendMessage(rc(msg));
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        List<String> strings = new ArrayList<>();
        if (args.length == 1) {
            strings.addAll(Arrays.stream(addon.api.getPlatform().getOnlinePlayers()).map(DCPlayer::getName).collect(Collectors.toList()));
        } else if (args.length == 2) {
            strings.addAll(addon.api.getCaseManager().getMap().keySet());
        }
        return strings;
    }
}