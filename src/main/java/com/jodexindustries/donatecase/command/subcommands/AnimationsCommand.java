package com.jodexindustries.donatecase.command.subcommands;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * Class for /dc animations subcommand implementation
 */
public class AnimationsCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        for (String addon : buildAnimationsMap().keySet()) {
            Tools.msgRaw(sender, "&6" + addon);
            for (String animation : buildAnimationsMap().get(addon)) {
                Tools.msgRaw(sender, "&9- &a" + animation);
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

    private static Map<String, List<String>> buildAnimationsMap() {
        Map<String, List<String>> animationsMap = new HashMap<>();
        Case.getInstance().api.getAnimationManager().getRegisteredAnimations().forEach((animationName, pair) -> {
            Addon addon = pair.getSecond();
            List<String> animations = animationsMap.getOrDefault(addon.getName(), new ArrayList<>());
            animations.add(animationName);

            animationsMap.put(addon.getName(), animations);
        });
        return animationsMap;
    }
}
