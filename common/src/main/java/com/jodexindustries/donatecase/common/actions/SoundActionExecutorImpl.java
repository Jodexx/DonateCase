package com.jodexindustries.donatecase.common.actions;

import com.jodexindustries.donatecase.api.data.action.ActionException;
import com.jodexindustries.donatecase.api.data.action.ActionExecutor;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoundActionExecutorImpl implements ActionExecutor {

    @Override
    public void execute(@Nullable DCPlayer player, @NotNull String context) throws ActionException {
        if (player == null) return;

        String[] args = context.split(" ");
        if (args.length < 1) {
            throw new ActionException("Sound not found!");
        }

        try {
            float volume = args.length > 1 ? Float.parseFloat(args[1]) : 1;
            float pitch = args.length > 2 ? Float.parseFloat(args[2]) : 1;

            player.playSound(args[0], volume, pitch);
        } catch (NumberFormatException e) {
            throw new ActionException("Invalid number format: " + context, e);
        } catch (IllegalArgumentException e) {
            throw new ActionException("Invalid sound: " + context.toUpperCase(), e);
        }
    }
}
