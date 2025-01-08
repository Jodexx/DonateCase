package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SetKeyCommand extends SubCommand<CommandSender> {
    
    private final DCAPIBukkit api;
    
    public SetKeyCommand(DCAPIBukkit api) {
        super("setkey", api.getAddon());
        setPermission(SubCommandType.MODER.permission);
        this.api = api;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (args.length >= 3) {
            String playerName = args[0];
            String caseName = args[1];
            if(!api.getTools().isValidPlayerName(playerName)) {
                api.getTools().msg(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("player-not-found"), "%player:" + playerName));
                return;
            }
            int keys;
            try {
                keys = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                DCToolsBukkit.msgRaw(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("number-format-exception"), "%string:" + args[3]));
                return;
            }
            if (api.getCaseManager().hasCaseByType(caseName)) {
                CaseDataBukkit data = api.getCaseManager().getCase(caseName);
                if (data == null) return;
                api.getCaseKeyManager().setKeys(caseName, playerName, keys).thenAcceptAsync(status -> {
                    if(status == DatabaseStatus.COMPLETE) {
                        api.getTools().msg(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("keys-sets"),
                                "%player:" + playerName, "%key:" + keys,
                                "%casetitle:" + data.getCaseTitle(), "%casedisplayname:" + data.getCaseDisplayName(), "%case:" + caseName));

                        if (args.length < 4 || !args[3].equalsIgnoreCase("-s")) {
                            Player target = Bukkit.getPlayer(playerName);
                            api.getTools().msg(target, DCToolsBukkit.rt(api.getConfig().getLang().getString("keys-sets-target"),
                                    "%player:" + playerName, "%key:" + keys,
                                    "%casetitle:" + data.getCaseTitle(), "%casedisplayname:" + data.getCaseDisplayName(), "%case:" + caseName));
                        }
                    }
                });
            } else {
                api.getTools().msg(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("case-does-not-exist"),
                        "%case:" + caseName));
            }
        } else {
            GlobalCommand.sendHelp(sender, label);
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return api.getTools().resolveSDGCompletions(args);
    }

}
