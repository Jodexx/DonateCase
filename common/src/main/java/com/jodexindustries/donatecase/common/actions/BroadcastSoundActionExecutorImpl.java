package com.jodexindustries.donatecase.common.actions;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.action.ActionException;
import com.jodexindustries.donatecase.api.data.action.ActionExecutor;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.platform.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BroadcastSoundActionExecutorImpl implements ActionExecutor {

    @Override
    public void execute(@Nullable DCPlayer player, @NotNull String context) throws ActionException {
        String[] args = context.split(" ");
        String sound = args[0];

        if (sound.isEmpty()) {
            throw new ActionException("Sound not found!");
        }

        float volume;
        float pitch;

        try {
            volume = args.length > 1 ? Float.parseFloat(args[1]) : 1;
            pitch = args.length > 2 ? Float.parseFloat(args[2]) : 1;
        } catch (NumberFormatException e) {
            throw new ActionException("Invalid number format: " + context, e);
        }

        Platform platform = DCAPI.getInstance().getPlatform();

        if (!platform.isValidSound(sound)) {
            throw new ActionException("Invalid sound: " + sound.toUpperCase());
        }

        for (DCPlayer target : platform.getOnlinePlayers()) {
            if (target.hasPermission("donatecase.notify")) {
                target.playSound(sound, volume, pitch);
            }
        }
    }
}
