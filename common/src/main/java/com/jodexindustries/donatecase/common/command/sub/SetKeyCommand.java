package com.jodexindustries.donatecase.common.command.sub;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
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
import java.util.Optional;

public class SetKeyCommand extends DefaultCommand {

    private final DCAPI api;

    public SetKeyCommand(DCAPI api) {
        super(api, "setkey", SubCommandType.MODER);
        this.api = api;
    }

    @Override
    public boolean execute(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        if (args.length < 3) return false;

        String plainName = args[0];
        String caseName = args[1];

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

        Optional<CaseDefinition> optional = api.getCaseManager().getByType(caseName);

        if (!optional.isPresent()) {
            sender.sendMessage(
                    DCTools.prefix(
                            DCTools.rt(
                                    api.getConfigManager().getMessages().getString("case-does-not-exist"),
                                    LocalPlaceholder.of("%casename%", caseName)
                            )
                    )
            );
            return true;
        }

        DCTools.formatPlayerName(plainName).thenAccept(playerName -> api.getCaseKeyManager().set(caseName, playerName, keys).thenAcceptAsync(status -> {
            Collection<LocalPlaceholder> placeholders = LocalPlaceholder.of(optional.get());
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
        }));
        return true;
    }

    @Override
    public List<String> getTabCompletions(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        return DCTools.resolveSDGCompletions(args);
    }

}
