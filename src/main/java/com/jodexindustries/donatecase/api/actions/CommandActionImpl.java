package com.jodexindustries.donatecase.api.actions;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.CaseAction;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class CommandActionImpl implements CaseAction {
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
