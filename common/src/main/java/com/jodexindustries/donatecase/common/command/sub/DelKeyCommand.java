package com.jodexindustries.donatecase.common.command.sub;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.common.command.DefaultCommand;
import com.jodexindustries.donatecase.common.tools.LocalPlaceholder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
        } else {
            String plainName = args[0];

            if (plainName.equalsIgnoreCase("all")) {
                if (args.length == 1) {
                    api.getCaseKeyManager().delete().thenAcceptAsync(status ->
                            sender.sendMessage(DCTools.rc(api.getConfigManager().getMessages().getString("all-keys-cleared"))));
                    return true;
                } else if (args.length == 2) {
                    api.getCaseKeyManager().delete(args[1]).thenAcceptAsync(status ->
                            sender.sendMessage(DCTools.rc(api.getConfigManager().getMessages().getString("all-keys-cleared"))));
                    return true;
                }
            }

            if (args.length < 2) return false;

            String caseType = args[1];

            Optional<CaseDefinition> optional = api.getCaseManager().getByType(caseType);
            if (!optional.isPresent()) {
                sender.sendMessage(
                        DCTools.rt(
                                api.getConfigManager().getMessages().getString("case-does-not-exist"),
                                LocalPlaceholder.of("%casetype%", caseType)
                        )
                );
                return true;
            }

            DCTools.formatPlayerName(plainName).thenAccept(playerName -> {
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
                        return;
                    }

                    api.getCaseKeyManager().remove(caseType, playerName, keys);
                }

                Collection<LocalPlaceholder> placeholders = LocalPlaceholder.of(optional.get());
                placeholders.add(LocalPlaceholder.of("%player%", playerName));
                placeholders.add(LocalPlaceholder.of("%key%", keys));

                sender.sendMessage(
                        DCTools.rt(
                                api.getConfigManager().getMessages().getString("keys-cleared"),
                                placeholders
                        )
                );

            });
        }
        return true;
    }

    @Override
    public List<String> getTabCompletions(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        return DCTools.resolveSDGCompletions(args);
    }

}
