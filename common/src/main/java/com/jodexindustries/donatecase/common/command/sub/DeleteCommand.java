package com.jodexindustries.donatecase.common.command.sub;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.storage.CaseInfo;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.common.command.DefaultCommand;
import com.jodexindustries.donatecase.common.tools.LocalPlaceholder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class DeleteCommand extends DefaultCommand {
    
    private final DCAPI api;
    
    public DeleteCommand(DCAPI api) {
        super(api, "delete", SubCommandType.ADMIN);
        this.api = api;
    }

    @Override
    public boolean execute(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof DCPlayer) {
                DCPlayer player = (DCPlayer) sender;
                CaseLocation location = player.getTargetBlock(5);

                if (api.getAnimationManager().isLocked(location)) {
                    sender.sendMessage(DCTools.prefix(api.getConfigManager().getMessages().getString("case-opens")));
                    return true;
                }

                CaseInfo caseInfo = api.getConfigManager().getCaseStorage().get(location);

                if (caseInfo != null) {
                    api.getConfigManager().getCaseStorage().delete(location);
                    api.getHologramManager().remove(caseInfo.location());
                    sender.sendMessage(DCTools.prefix(api.getConfigManager().getMessages().getString("case-removed")));
                } else {
                    sender.sendMessage(DCTools.prefix(api.getConfigManager().getMessages().getString("block-is-not-case")));
                }
            }
        } else if (args.length == 1) {
            String caseName = args[0];

            CaseInfo caseInfo = api.getConfigManager().getCaseStorage().get(caseName);
            if (caseInfo != null) {
                if (api.getAnimationManager().isLocked(caseInfo.location())) {
                    sender.sendMessage(DCTools.prefix(api.getConfigManager().getMessages().getString("case-opens")));
                    return true;
                }

                api.getConfigManager().getCaseStorage().delete(caseName);
                api.getHologramManager().remove(caseInfo.location());
                sender.sendMessage(DCTools.prefix(api.getConfigManager().getMessages().getString("case-removed")));
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
        }
        return true;
    }

    @Override
    public List<String> getTabCompletions(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        return args.length == 1 ? new ArrayList<>(api.getConfigManager().getCaseStorage().get().keySet()) : new ArrayList<>();
    }

}
