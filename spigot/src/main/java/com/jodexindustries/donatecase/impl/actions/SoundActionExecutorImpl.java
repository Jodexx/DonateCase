package com.jodexindustries.donatecase.impl.actions;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.action.ActionExecutor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoundActionExecutorImpl implements ActionExecutor<Player> {
    /**
     * Player sound for player with specific cooldown<br>
     * {@code - "[sound] (sound) (volume) (pitch)"}
     *
     * @param player The player to whom the sound will be played
     * @param context Sound context
     * @param cooldown Cooldown in seconds
     */
    @Override
    public void execute(@Nullable Player player, @NotNull String context, int cooldown) {
        String[] args = context.split(" ");
        Sound sound;
        try {
            if(args.length >= 1) {
                sound = Sound.valueOf(args[0].toUpperCase());
            } else {
                Case.getInstance().getLogger().warning("Sound not found!");
                return;
            }
        } catch (IllegalArgumentException e) {
            Case.getInstance().getLogger().warning("Invalid sound: " + context.toUpperCase());
            return;
        }

        try {
            float volume = args.length > 1 ? Float.parseFloat(args[1]) : 1;
            float pitch = args.length > 2 ? Float.parseFloat(args[2]) : 1;

            Bukkit.getScheduler().runTaskLater(Case.getInstance(), () -> {
                if (player != null) {
                    player.playSound(player.getLocation(), sound, volume, pitch);
                }
            }, 20L * cooldown);
        } catch (NumberFormatException e) {
            Case.getInstance().getLogger().warning("Invalid number format: " + context);
        }
    }
}
