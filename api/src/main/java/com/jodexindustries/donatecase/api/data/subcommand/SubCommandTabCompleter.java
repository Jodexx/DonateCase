package com.jodexindustries.donatecase.api.data.subcommand;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Interface for implement subcommand tab completer
 * @param <S> the type of command sender
 */
public interface SubCommandTabCompleter<S> {

    /**
     * Provides tab-completion suggestions for the subcommand.
     *
     * @param sender The sender requesting tab completions.
     * @param label  The command label.
     * @param args   The current arguments input by the sender.
     * @return A list of tab-completion suggestions.
     */
    List<String> getTabCompletions(@NotNull S sender, @NotNull String label, @NotNull String[] args);
}
