package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.command.DefaultCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GiveKeyCommand extends DefaultCommand {

    private final DCAPI api;

    public GiveKeyCommand(DCAPI api) {
        super(api, "givekey", SubCommandType.MODER);
        this.api = api;
    }

    @Override
    public boolean execute(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        if (args.length < 3) {
            return false;
        }

        String playerName = args[0];
        String caseName = args[1];
        if (!DCTools.isValidPlayerName(playerName)) {
            sender.sendMessage(
                    DCTools.prefix(DCTools.rt(api.getConfig().getMessages().getString("player-not-found"), "%player:" + playerName))
            );
            return true;
        }

        int keys;
        try {
            keys = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            DCTools.prefix(DCTools.rt(api.getConfig().getMessages().getString("number-format-exception"), "%string:" + args[2]));
            return true;
        }
        if (api.getCaseManager().hasByType(caseName)) {
            CaseData data = api.getCaseManager().get(caseName);
            if (data == null) return true;
            api.getCaseKeyManager().add(caseName, playerName, keys).thenAcceptAsync(status -> {
                if (status == DatabaseStatus.COMPLETE) {
                    DCTools.prefix(DCTools.rt(api.getConfig().getMessages().getString("keys-given"),
                            "%player:" + playerName, "%key:" + keys, "%casetitle:" + data.getCaseGui().getTitle(),
                            "%casedisplayname:" + data.getCaseDisplayName(), "%case:" + caseName));

                    if (args.length < 4 || !args[3].equalsIgnoreCase("-s")) {
                        DCPlayer target = api.getPlatform().getPlayer(playerName);
                        if (target != null)
                            target.sendMessage(DCTools.prefix(DCTools.rt(api.getConfig().getMessages().getString("keys-given-target"),
                                    "%player:" + playerName, "%key:" + keys, "%casetitle:" + data.getCaseGui().getTitle(),
                                    "%casedisplayname:" + data.getCaseDisplayName(), "%case:" + caseName)));
                    }
                }
            });
        } else {
            DCTools.prefix(DCTools.rt(api.getConfig().getMessages().getString("case-does-not-exist"), "%case:" + caseName));
        }

        return true;
    }

    @Override
    public List<String> getTabCompletions(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        return DCTools.resolveSDGCompletions(args);
    }
}