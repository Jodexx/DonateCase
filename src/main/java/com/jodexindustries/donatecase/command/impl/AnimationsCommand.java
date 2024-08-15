package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.AnimationManager;
import com.jodexindustries.donatecase.api.SubCommandManager;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Class for /dc animations subcommand implementation
 */
public class AnimationsCommand implements SubCommandExecutor, SubCommandTabCompleter {

    public AnimationsCommand(SubCommandManager manager) {
        com.jodexindustries.donatecase.api.data.subcommand.SubCommand subCommand = manager.builder("animations")
                .executor(this)
                .tabCompleter(this)
                .type(SubCommandType.ADMIN)
                .build();
        manager.registerSubCommand(subCommand);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        Map<String, List<CaseAnimation>> animationsMap = buildAnimationsMap();
        for (Map.Entry<String, List<CaseAnimation>> entry : animationsMap.entrySet()) {
            Tools.msgRaw(sender, "&6" + entry.getKey());
            for (CaseAnimation animation : entry.getValue()) {
                Tools.msgRaw(sender, "&9- &a" + animation.getName() + " &3- &2" + animation.getDescription());
            }
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }

    private static Map<String, List<CaseAnimation>> buildAnimationsMap() {
        Map<String, List<CaseAnimation>> animationsMap = new HashMap<>();
        AnimationManager.getRegisteredAnimations().forEach((animationName, caseAnimation) -> {
            String addon = caseAnimation.getAddon().getName();

            List<CaseAnimation> animations = animationsMap.getOrDefault(addon, new ArrayList<>());
            animations.add(caseAnimation);

            animationsMap.put(addon, animations);
        });

        AnimationManager.getOldAnimations().forEach((animationName, oldPair) -> {
            Addon addon = oldPair.getSecond();
            List<CaseAnimation> animations = animationsMap.getOrDefault(addon.getName(), new ArrayList<>());

            CaseAnimation action = new CaseAnimation(null, addon, animationName, "Old animation");
            animations.add(action);

            animationsMap.put(addon.getName(), animations);
        });
        return animationsMap;
    }
}
