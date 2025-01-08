package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DelKeyCommand extends SubCommand<CommandSender> {
    
    private final DCAPIBukkit api;
    
    public DelKeyCommand(DCAPIBukkit api) {
        super("delkey", api.getAddon());
        setPermission(SubCommandType.ADMIN.permission);
        this.api = api;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(Case.getInstance(), () -> {
            if (args.length == 0) {
                GlobalCommand.sendHelp(sender, label);
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("all")) {
                    Case.getInstance().api.getCaseKeyManager().removeAllKeys().thenAcceptAsync(status -> api.getTools().msg(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("all-keys-cleared"))));
                }
            } else {
                String playerName = args[0];
                String caseType = args[1];
                if(!api.getTools().isValidPlayerName(playerName)) {
                    api.getTools().msg(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("player-not-found"), "%player:" + playerName));
                    return;
                }
                if (api.getCaseManager().hasCaseByType(caseType)) {
                    CaseDataBukkit data = api.getCaseManager().getCase(caseType);
                    if (data == null) return;
                    int keys;
                    if (args.length == 2) {
                        keys = api.getCaseKeyManager().getKeys(caseType, playerName);
                        api.getCaseKeyManager().setKeys(caseType, playerName, 0);
                    } else {
                        try {
                            keys = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            DCToolsBukkit.msgRaw(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("number-format-exception"), "%string:" + args[2]));
                            return;
                        }

                        api.getCaseKeyManager().removeKeys(caseType, playerName, keys);
                    }
                    api.getTools().msg(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("keys-cleared"),
                            "%player:" + playerName, "%casetitle:" + data.getCaseTitle(),
                            "%casedisplayname:" + data.getCaseDisplayName(), "%case:" + caseType, "%key:" + keys));
                } else {
                    api.getTools().msg(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("case-does-not-exist"), "%case:" + caseType));
                }
            }
        });
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return api.getTools().resolveSDGCompletions(args);
    }

}
