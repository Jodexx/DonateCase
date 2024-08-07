package com.jodexindustries.donatecase.command.subcommands;

import com.jodexindustries.donatecase.api.AnimationManager;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.api.data.animation.CaseAnimation;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * Class for /dc animations subcommand implementation
 */
public class AnimationsCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        Map<String, List<CaseAnimation>> animationsMap = buildAnimationsMap();
        for (Map.Entry<String, List<CaseAnimation>> entry : animationsMap.entrySet()) {
            Tools.msgRaw(sender, "&6" + entry.getKey());
            for (CaseAnimation animation : entry.getValue()) {
                Tools.msgRaw(sender, "&9- &a" + animation.getName() + " &3- &2" + animation.getDescription());
            }
        }
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public SubCommandType getType() {
        return SubCommandType.ADMIN;
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
