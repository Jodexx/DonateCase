package com.jodexindustries.donatecase.api.actions;

import com.jodexindustries.donatecase.api.data.CaseAction;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import static com.jodexindustries.donatecase.DonateCase.instance;

public class CommandAction implements CaseAction {
    /**
     * Send command to console with specific cooldown
     * @param context Console command
     * @param cooldown Cooldown in seconds
     */
    @Override
    public void execute(@NotNull OfflinePlayer player, @NotNull String context, int cooldown) {
        Bukkit.getScheduler().runTaskLater(instance, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                context), 20L * cooldown);
    }
}
