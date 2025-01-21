package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.command.GlobalCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GiveKeyCommand extends SubCommand {
    
    private final DCAPI api;

    public GiveKeyCommand(DCAPI api) {
        super("givekey", api.getPlatform());
        setPermission(SubCommandType.MODER.permission);
        this.api = api;
    }

    @Override
    public boolean execute(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        if (args.length >= 3) {
            String playerName = args[0];
            String caseName = args[1];
            if(!api.getPlatform().getTools().isValidPlayerName(playerName)) {
                api.getPlatform().getTools().msg(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("player-not-found"), "%player:" + playerName));
                return;
            }

            int keys;
            try {
                keys = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                DCToolsBukkit.msgRaw(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("number-format-exception"), "%string:" + args[2]));
                return;
            }
            if (api.getCaseManager().hasCaseByType(caseName)) {
                CaseDataBukkit data = api.getCaseManager().getCase(caseName);
                if (data == null) return;
                api.getCaseKeyManager().addKeys(caseName, playerName, keys).thenAcceptAsync(status -> {
                    if(status == DatabaseStatus.COMPLETE) {
                        api.getPlatform().getTools().msg(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("keys-given"),
                                "%player:" + playerName, "%key:" + keys, "%casetitle:" + data.getCaseTitle(),
                                "%casedisplayname:" + data.getCaseDisplayName(), "%case:" + caseName));

                        if (args.length < 4 || !args[3].equalsIgnoreCase("-s")) {
                            Player target = Bukkit.getPlayer(playerName);
                            api.getPlatform().getTools().msg(target, DCToolsBukkit.rt(api.getConfig().getLang().getString("keys-given-target"),
                                    "%player:" + playerName, "%key:" + keys, "%casetitle:" + data.getCaseTitle(),
                                    "%casedisplayname:" + data.getCaseDisplayName(), "%case:" + caseName));
                        }
                    }
                });
            } else {
                api.getPlatform().getTools().msg(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("case-does-not-exist"), "%case:" + caseName));
            }
        } else {
            GlobalCommand.sendHelp(sender, label);
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return api.getPlatform().getTools().resolveSDGCompletions(args);
    }
}