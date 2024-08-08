package com.jodexindustries.donatecase.api.data.subcommand;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface SubCommandExecutor {
    /**
     * Executes the given sub command
     * @param sender Source of the command
     * @param label Command label
     * @param args Passed command arguments
     */
    void execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args);
}
