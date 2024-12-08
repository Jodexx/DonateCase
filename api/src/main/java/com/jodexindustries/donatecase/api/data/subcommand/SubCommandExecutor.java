package com.jodexindustries.donatecase.api.data.subcommand;

import org.jetbrains.annotations.NotNull;

/**
 * Interface for implement subcommand executor
 */
public interface SubCommandExecutor<S> {

    /**
     * Executes the given sub command
     *
     * @param sender Source of the command
     * @param label  Command label
     * @param args   Passed command arguments
     */
    void execute(@NotNull S sender, @NotNull String label, @NotNull String[] args);
}
