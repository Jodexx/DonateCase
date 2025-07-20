package com.jodexindustries.donatecase.common.command.sub;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.common.command.DefaultCommand;
import com.jodexindustries.donatecase.common.tools.LocalPlaceholder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OpenCaseCommand extends DefaultCommand {

    private final DCAPI api;

    public OpenCaseCommand(DCAPI api) {
        super(api, "opencase", SubCommandType.PLAYER);
        this.api = api;
    }

    @Override
    public boolean execute(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        if (args.length < 1)
            return false;

        if (sender instanceof DCPlayer) {
            String playerName = sender.getName();
            DCPlayer player = (DCPlayer) sender;

            String caseType = args[0];
            Optional<CaseDefinition> optional = api.getCaseManager().getByType(caseType);

            if (optional.isPresent()) {
                CaseDefinition definition = optional.get();
                CaseAnimation animation = api.getAnimationManager().get(definition.settings().animation());
                if (animation == null)
                    return true;

                api.getCaseKeyManager().getAsync(caseType, playerName).thenAccept((keys) -> {
                    if (keys >= 1) {
                        api.getCaseKeyManager().remove(caseType, playerName, 1).thenAccept(status -> {
                            if (status == DatabaseStatus.COMPLETE) {
                                if (animation.isRequireBlock()) {
                                    api.getAnimationManager().preEnd(definition, player,
                                            definition.items().getRandomItem());
                                } else {
                                    api.getAnimationManager().start(player, player.getLocation(), definition);
                                }
                            }
                        });
                    } else {
                        sender.sendMessage(DCTools.prefix(api.getConfigManager().getMessages().getString("no-keys")));
                    }
                });
            } else {
                sender.sendMessage(DCTools.prefix(DCTools.rt(
                        api.getConfigManager().getMessages().getString("case-does-not-exist"),
                        LocalPlaceholder.of("%casetype%", caseType))));
            }
        }
        return true;
    }

    @Override
    public List<String> getTabCompletions(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        List<String> list = api.getCaseManager().definitions().stream()
                .map(def -> def.settings().type())
                .collect(Collectors.toList());
        if (args.length >= 2) {
            return new ArrayList<>();
        }
        return list;
    }
}