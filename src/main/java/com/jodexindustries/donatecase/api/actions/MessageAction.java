package com.jodexindustries.donatecase.api.actions;

import com.jodexindustries.donatecase.api.data.CaseAction;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import static com.jodexindustries.donatecase.DonateCase.instance;

public class MessageAction implements CaseAction {
    /**
     * Send chat message for player with specific cooldown
     * @param player The player to whom the message will be sent
     * @param context Chat message
     * @param cooldown Cooldown in seconds
     */
    @Override
    public void execute(OfflinePlayer player, String context, int cooldown) {
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            if (player.getPlayer() != null) {
                player.getPlayer().sendMessage(context);
            }
        }, 20L * cooldown);
    }
}
