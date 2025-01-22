package com.jodexindustries.donatecase.impl.actions;

import com.jodexindustries.donatecase.api.data.action.ActionExecutor;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BroadcastActionExecutorImpl implements ActionExecutor {

    @Override
    public void execute(@Nullable DCPlayer player, @NotNull String context) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(context);
            }
    }
}
