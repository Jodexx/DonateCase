package com.jodexindustries.donatecase.api.impl.actions;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.action.ActionExecutor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class CommandActionExecutorImpl implements ActionExecutor {
    /**
     * Send command to console with specific cooldown<br>
     * {@code - "[command] (command)"}
     *
     * @param context Console command
     * @param cooldown Cooldown in seconds
     */
    @Override
    public void execute(@NotNull OfflinePlayer player, @NotNull String context, int cooldown) {
        Bukkit.getScheduler().runTaskLater(Case.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                context), 20L * cooldown);
    }
}
