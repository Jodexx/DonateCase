package com.jodexindustries.donatecase.common.command.sub;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.common.command.DefaultCommand;
import com.jodexindustries.donatecase.common.tools.LocalPlaceholder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class DelKeyCommand extends DefaultCommand {

    private final DCAPI api;

    public DelKeyCommand(DCAPI api) {
        super(api, "delkey", SubCommandType.ADMIN);
        this.api = api;
    }

    @Override
    public boolean execute(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        if (args.length == 0) {
            return false;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("all")) {
                api.getCaseKeyManager().delete().thenAcceptAsync(status ->
                        sender.sendMessage(DCTools.rc(api.getConfigManager().getMessages().getString("all-keys-cleared"))));
            }
        } else {
            String playerName = args[0];
            String caseType = args[1];
            if (!DCTools.isValidPlayerName(playerName)) {
                sender.sendMessage(
                        DCTools.rt(
                                api.getConfigManager().getMessages().getString("player-not-found"),
                                LocalPlaceholder.of("%player%", playerName)
                        )
                );
                return true;
            }
            CaseData data = api.getCaseManager().get(caseType);


            if (data != null) {
                int keys;
                if (args.length == 2) {
                    keys = api.getCaseKeyManager().get(caseType, playerName);
                    api.getCaseKeyManager().set(caseType, playerName, 0);
                } else {
                    try {
                        keys = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(DCTools.rt(
                                        api.getConfigManager().getMessages().getString("number-format-exception"),
                                        LocalPlaceholder.of("%string%", args[2])
                                )
                        );
                        return true;
                    }

                    api.getCaseKeyManager().remove(caseType, playerName, keys);
                }

                Collection<LocalPlaceholder> placeholders = LocalPlaceholder.of(data);
                placeholders.add(LocalPlaceholder.of("%player%", playerName));
                placeholders.add(LocalPlaceholder.of("%key%", keys));

                sender.sendMessage(
                        DCTools.rt(
                                api.getConfigManager().getMessages().getString("keys-cleared"),
                                placeholders
                        )
                );
            } else {
                sender.sendMessage(
                        DCTools.rt(
                                api.getConfigManager().getMessages().getString("case-does-not-exist"),
                                LocalPlaceholder.of("%casetype%", caseType)
                        )
                );
            }
        }
        return true;
    }

    @Override
    public List<String> getTabCompletions(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        return DCTools.resolveSDGCompletions(args);
    }

}
