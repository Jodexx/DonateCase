package com.jodexindustries.donatecase.api.data.subcommand;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Interface for implement subcommand tab completer
 * @since 2.2.4.5
 */
public interface SubCommandTabCompleter {
    /**
     * Get command tab completions
     * @param sender Command sender
     * @param label Command label
     * @param args Command args
     * @return tab completions
     */
    List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args);
}
