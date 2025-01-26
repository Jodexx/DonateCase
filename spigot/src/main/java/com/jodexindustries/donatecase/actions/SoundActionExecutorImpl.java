package com.jodexindustries.donatecase.actions;

import com.jodexindustries.donatecase.api.data.action.ActionException;
import com.jodexindustries.donatecase.api.data.action.ActionExecutor;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.tools.BukkitUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoundActionExecutorImpl implements ActionExecutor {

    @Override
    public void execute(@Nullable DCPlayer player, @NotNull String context) throws ActionException {
        if (player == null) return;

        Player bukkitPlayer = BukkitUtils.toBukkit(player);

        String[] args = context.split(" ");
        Sound sound;
        try {
            if (args.length >= 1) {
                sound = Sound.valueOf(args[0].toUpperCase());
            } else {
                throw new ActionException("Sound not found!");
            }
        } catch (IllegalArgumentException e) {
            throw new ActionException("Invalid sound: " + context.toUpperCase(), e);
        }

        try {
            float volume = args.length > 1 ? Float.parseFloat(args[1]) : 1;
            float pitch = args.length > 2 ? Float.parseFloat(args[2]) : 1;

            bukkitPlayer.playSound(bukkitPlayer.getLocation(), sound, volume, pitch);
        } catch (NumberFormatException e) {
            throw new ActionException("Invalid number format: " + context, e);
        }
    }
}
