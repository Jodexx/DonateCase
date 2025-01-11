package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.animation.JavaAnimationBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.database.DatabaseStatus;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class OpenCaseCommand extends SubCommand<CommandSender> {

    private final DCAPIBukkit api;

    public OpenCaseCommand(DCAPIBukkit api) {
        super("opencase", api.getAddon());
        setPermission(SubCommandType.PLAYER.permission);
        this.api = api;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            String playerName = sender.getName();
            Player player = (Player) sender;
            if (args.length >= 1) {
                String caseName = args[0];
                CaseDataBukkit data = api.getCaseManager().getCase(caseName);

                if (data != null) {
                    CaseAnimation<JavaAnimationBukkit> animation = api.getAnimationManager().getRegisteredAnimation(data.getAnimation());
                    if (animation == null) return;

                    api.getCaseKeyManager().getKeysAsync(caseName, playerName).thenAccept((keys) -> {
                        if (keys >= 1) {
                            api.getCaseKeyManager().removeKeys(caseName, playerName, 1).thenAccept(status -> {
                                if (status == DatabaseStatus.COMPLETE) {
                                    if (animation.isRequireBlock()) {
                                        api.getAnimationManager().animationPreEnd(data, player, player.getLocation(), data.getRandomItem());
                                    } else {
                                        api.getAnimationManager().startAnimation(player, player.getLocation(), data);
                                    }
                                }
                            });
                        } else {
                            api.getTools().msg(player, api.getConfig().getLang().getString("no-keys"));
                        }
                    });

                } else {
                    api.getTools().msg(sender, DCToolsBukkit.rt(api.getConfig().getLang().getString("case-does-not-exist"), "%case:" + caseName));
                }
            } else {
                GlobalCommand.sendHelp(sender, label);
            }
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        List<String> list = new ArrayList<>(api.getConfig().getConfigCases().getCases().keySet());
        if (args.length >= 2) {
            return new ArrayList<>();
        }
        return list;
    }
}