package com.jodexindustries.donatecase.common.command.sub;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.common.command.DefaultCommand;
import com.jodexindustries.donatecase.common.tools.LocalPlaceholder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class SetKeyCommand extends DefaultCommand {

    private final DCAPI api;

    public SetKeyCommand(DCAPI api) {
        super(api, "setkey", SubCommandType.MODER);
        this.api = api;
    }

    @Override
    public boolean execute(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        if (args.length < 3) return false;

        String playerName = args[0];
        String caseName = args[1];
        if (!DCTools.isValidPlayerName(playerName)) {
            sender.sendMessage(
                    DCTools.prefix(
                            DCTools.rt(
                                    api.getConfigManager().getMessages().getString("player-not-found"),
                                    LocalPlaceholder.of("%player%", playerName)
                            )
                    )
            );
            return true;
        }

        int keys;

        try {
            keys = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(
                    DCTools.prefix(
                            DCTools.rt(
                                    api.getConfigManager().getMessages().getString("number-format-exception"),
                                    LocalPlaceholder.of("%string%", args[2])
                            )
                    )
            );
            return true;
        }

        CaseData data = api.getCaseManager().get(caseName);

        if (data != null) {
            api.getCaseKeyManager().set(caseName, playerName, keys).thenAcceptAsync(status -> {
                Collection<LocalPlaceholder> placeholders = LocalPlaceholder.of(data);
                placeholders.add(LocalPlaceholder.of("%player%", playerName));
                placeholders.add(LocalPlaceholder.of("%key%", keys));

                if (status == DatabaseStatus.COMPLETE) {
                    sender.sendMessage(
                            DCTools.prefix(
                                    DCTools.rt(
                                            api.getConfigManager().getMessages().getString("keys-sets"),
                                            placeholders
                                    )
                            )
                    );

                    if (args.length < 4 || !args[3].equalsIgnoreCase("-s")) {
                        DCPlayer target = api.getPlatform().getPlayer(playerName);
                        if (target != null) target.sendMessage(
                                DCTools.prefix(DCTools.rt(
                                        api.getConfigManager().getMessages().getString("keys-sets-target"),
                                        placeholders
                                ))
                        );
                    }
                }
            });
        } else {
            sender.sendMessage(
                    DCTools.prefix(
                            DCTools.rt(
                                    api.getConfigManager().getMessages().getString("case-does-not-exist"),
                                    LocalPlaceholder.of("%casename%", caseName)
                            )
                    )
            );
        }
        return true;
    }

    @Override
    public List<String> getTabCompletions(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        return DCTools.resolveSDGCompletions(args);
    }

}
