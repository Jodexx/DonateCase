package com.jodexindustries.donatecase.api.actions;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.action.ActionExecutor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class MessageActionExecutorImpl implements ActionExecutor {
    /**
     * Send chat message for player with specific cooldown<br>
     * {@code - "[message] (message)"}
     *
     * @param player The player to whom the message will be sent
     * @param context Chat message
     * @param cooldown Cooldown in seconds
     */
    @Override
    public void execute(@NotNull OfflinePlayer player, @NotNull String context, int cooldown) {
        Bukkit.getScheduler().runTaskLater(Case.getInstance(), () -> {
            if (player.getPlayer() != null) {
                player.getPlayer().sendMessage(context);
            }
        }, 20L * cooldown);
    }
}
