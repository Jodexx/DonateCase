package com.jodexindustries.donatecase.api.data;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Interface for registering sub commands
 */

public interface SubCommand {
    /**
     * Executes the given sub command
     * @param sender Source of the command
     * @param args Passed command arguments
     */
    void execute(CommandSender sender, String[] args);

    /**
     * Get command tab completions
     * @param sender Command sender
     * @param args Command args
     * @return tab completions
     */
    List<String> getTabCompletions(CommandSender sender, String[] args);

    /**
     * Get command type
     * @return command type
     */
    SubCommandType getType();

    /**
     * Get command arguments     *  to appear in the Command Description in /dc help
     * @return Command arguments
     */
    default String[] getArgs() {
        return null;
    }

    /**
     * Get command description
     * A description that should act as a description of the command in the /dc help
     * @return Command description
     */
    default String getDescription() {
        return null;
    }
}
