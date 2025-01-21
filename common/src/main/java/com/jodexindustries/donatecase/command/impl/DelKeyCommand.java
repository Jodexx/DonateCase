package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.api.tools.DCTools;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DelKeyCommand extends SubCommand {
    
    private final DCAPI api;
    
    public DelKeyCommand(DCAPI api) {
        super("delkey", api.getPlatform());
        setPermission(SubCommandType.ADMIN.permission);
        this.api = api;
    }

    @Override
    public boolean execute(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
            if (args.length == 0) {
                return false;
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("all")) {
                    api.getCaseKeyManager().removeAllKeys().thenAcceptAsync(status ->
                            sender.sendMessage(DCTools.rt(api.getConfig().getMessages().getString("all-keys-cleared"))));
                }
            } else {
                String playerName = args[0];
                String caseType = args[1];
                if(!api.getPlatform().getTools().isValidPlayerName(playerName)) {
                    sender.sendMessage(DCTools.rt(api.getConfig().getMessages().getString("player-not-found"), "%player:" + playerName));
                    return true;
                }
                CaseData data = api.getCaseManager().getCase(caseType);


                if (data != null) {
                    int keys;
                    if (args.length == 2) {
                        keys = api.getCaseKeyManager().getKeys(caseType, playerName);
                        api.getCaseKeyManager().setKeys(caseType, playerName, 0);
                    } else {
                        try {
                            keys = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(DCTools.rt(api.getConfig().getMessages().getString("number-format-exception"), "%string:" + args[2]));
                            return true;
                        }

                        api.getCaseKeyManager().removeKeys(caseType, playerName, keys);
                    }
                    sender.sendMessage(DCTools.rt(api.getConfig().getMessages().getString("keys-cleared"),
                            "%player:" + playerName, "%casetitle:" + data.getCaseGui().getTitle(),
                            "%casedisplayname:" + data.getCaseDisplayName(), "%case:" + caseType, "%key:" + keys));
                } else {
                    sender.sendMessage(DCTools.rt(api.getConfig().getMessages().getString("case-does-not-exist"), "%case:" + caseType));
                }
            }
        return true;
    }

    @Override
    public List<String> getTabCompletions(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        return api.getPlatform().getTools().resolveSDGCompletions(args);
    }

}
