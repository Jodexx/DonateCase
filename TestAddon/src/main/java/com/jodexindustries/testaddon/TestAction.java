package com.jodexindustries.testaddon;

import com.jodexindustries.donatecase.api.data.action.ActionExecutor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class TestAction implements ActionExecutor {
    @Override
    public void execute(@NotNull OfflinePlayer player, @NotNull String context, int cooldown) {
        if(player.getPlayer() != null) {
            player.getPlayer().sendMessage("Hello!");
        }
    }
}
