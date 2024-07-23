package com.jodexindustries.donatecase.api.actions;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.CaseAction;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public class SoundActionImpl implements CaseAction {
    /**
     * Player sound for player with specific cooldown<br>
     * {@code - "[sound] (sound) (volume) (pitch)"}
     *
     * @param player The player to whom the sound will be played
     * @param context Sound context
     * @param cooldown Cooldown in seconds
     */
    @Override
    public void execute(@NotNull OfflinePlayer player, @NotNull String context, int cooldown) {
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
                if (player.getPlayer() != null) {
                    player.getPlayer().playSound(player.getPlayer().getLocation(), sound, volume, pitch);
                }
            }, 20L * cooldown);
        } catch (NumberFormatException e) {
            Case.getInstance().getLogger().warning("Invalid number format: " + context);
        }
    }
}
