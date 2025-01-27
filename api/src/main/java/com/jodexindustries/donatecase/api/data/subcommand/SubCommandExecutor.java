package com.jodexindustries.donatecase.api.data.subcommand;

import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for implement subcommand executor
 */
public interface SubCommandExecutor {

    /**
     * Executes the given sub command
     *
     * @param sender Source of the command
     * @param label  Command label
     * @param args   Passed command arguments
     */
    boolean execute(@NotNull DCCommandSender sender, @NotNull String label, @NotNull String[] args) throws SubCommandException;

}
