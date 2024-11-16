package com.jodexindustries.donatecase.impl.actions;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.action.ActionExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandActionExecutorImpl implements ActionExecutor<Player> {
    /**
     * Send command to console with specific cooldown<br>
     * {@code - "[command] (command)"}
     *
     * @param context Console command
     * @param cooldown Cooldown in seconds
     */
    @Override
    public void execute(@Nullable Player player, @NotNull String context, int cooldown) {
        Bukkit.getScheduler().runTaskLater(Case.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                context), 20L * cooldown);
    }
}
