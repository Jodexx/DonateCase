package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.tools.DCTools;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class OpenCaseCommand extends SubCommand.SubCommandBuilder implements SubCommandExecutor, SubCommandTabCompleter {

    private final DCAPI api;

    public OpenCaseCommand(DCAPI api) {
        super();
        name("opencase");
        addon(api.getPlatform());
        permission(SubCommandType.PLAYER.permission);
        executor(this);
        tabCompleter(this);
        this.api = api;
    }

    @Override
    public boolean execute(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        if (args.length < 1) return false;

        if (sender instanceof DCPlayer) {
            String playerName = sender.getName();
            DCPlayer player = (DCPlayer) sender;

            String caseName = args[0];
            CaseData data = api.getCaseManager().get(caseName);

            if (data != null) {
                CaseAnimation animation = api.getAnimationManager().get(data.getAnimation());
                if (animation == null) return true;

                api.getCaseKeyManager().getAsync(caseName, playerName).thenAccept((keys) -> {
                    if (keys >= 1) {
                        api.getCaseKeyManager().remove(caseName, playerName, 1).thenAccept(status -> {
                            if (status == DatabaseStatus.COMPLETE) {
                                if (animation.isRequireBlock()) {
                                    api.getAnimationManager().preEnd(data, player, player.getLocation(), data.getRandomItem());
                                } else {
                                    api.getAnimationManager().start(player, player.getLocation(), data);
                                }
                            }
                        });
                    } else {
                        sender.sendMessage(DCTools.prefix(api.getConfig().getMessages().getString("no-keys")));
                    }
                });

            } else {
                sender.sendMessage(DCTools.prefix(DCTools.rt(api.getConfig().getMessages().getString("case-does-not-exist"), "%case:" + caseName)));
            }
        }
        return true;
    }

    @Override
    public List<String> getTabCompletions(@NotNull DCCommandSender sender, @NotNull String label, String[] args) {
        List<String> list = new ArrayList<>(api.getCaseManager().getMap().keySet());
        if (args.length >= 2) {
            return new ArrayList<>();
        }
        return list;
    }
}