package com.jodexindustries.donatecase.api.manager;

import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Interface for managing subcommands, providing methods to register, unregister, and retrieve
 * subcommands as well as handle tab completions.
 *
 * @param <S> the sender type, representing the source of the command (e.g., player)
 */
public interface SubCommandManager<S> {

    /**
     * Provides a builder for creating a new subcommand with the specified name.
     *
     * @param name the name of the subcommand to create
     * @return a builder instance for constructing the SubCommand
     * @see #registerSubCommand(SubCommand)
     */
    @NotNull
    SubCommand.Builder<S> builder(@NotNull String name);

    /**
     * Registers a subcommand to the manager.
     *
     * @param subCommand the SubCommand object to register
     * @return true if the registration was successful, false otherwise
     * @see #builder(String)
     */
    boolean registerSubCommand(SubCommand<S> subCommand);

    /**
     * Unregisters a subcommand from the manager by its name.
     *
     * @param commandName the name of the subcommand to unregister
     */
    void unregisterSubCommand(String commandName);

    /**
     * Unregisters all subcommands currently managed by this instance.
     */
    void unregisterSubCommands();

    @Nullable
    SubCommand<S> getRegisteredSubCommand(String commandName);

    @NotNull
    Map<String, SubCommand<S>> getRegisteredSubCommands();

    /**
     * Provides a list of possible tab completions for a given subcommand, based on the command's
     * sender, name, and provided arguments.
     *
     * @param sender         the source of the command (e.g., player or console)
     * @param subCommandName the name of the subcommand
     * @param label          the main command label
     * @param args           the arguments passed with the command
     * @return a list of possible tab completions
     */
    List<String> getTabCompletionsForSubCommand(S sender, String subCommandName, String label, String[] args);

}
