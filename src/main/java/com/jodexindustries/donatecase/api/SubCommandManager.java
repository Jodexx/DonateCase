package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.events.SubCommandRegisteredEvent;
import com.jodexindustries.donatecase.dc.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sub command class manager
 */
public class SubCommandManager {
    public static final Map<String, SubCommand> subCommands = new HashMap<>();

    /**
     * Register sub command
     * @param commandName Sub command name to register
     * @param subCommand Class that implements the SubCommand interface
     */
    public static void registerSubCommand(String commandName, SubCommand subCommand) {
        if(subCommands.get(commandName.toLowerCase()) == null) {
            subCommands.put(commandName.toLowerCase(), subCommand);
            SubCommandRegisteredEvent subCommandRegisteredEvent = new SubCommandRegisteredEvent(commandName);
            Bukkit.getServer().getPluginManager().callEvent(subCommandRegisteredEvent);
        } else {
            Main.instance.getLogger().warning("Sub command " + commandName + " already registered!");
        }
    }

    /**
     * Unregister sub command
     * @param commandName Sub command name to unregister
     */
    public static void unregisterSubCommand(String commandName) {
        if(subCommands.get(commandName.toLowerCase()) != null) {
            subCommands.remove(commandName.toLowerCase());
            SubCommandRegisteredEvent subCommandRegisteredEvent = new SubCommandRegisteredEvent(commandName);
            Bukkit.getServer().getPluginManager().callEvent(subCommandRegisteredEvent);
        } else {
            Main.instance.getLogger().warning("Sub command " + commandName + " already unregistered!");
        }
    }

    /**
     * Get all subcommands
     * @return String - sub command name <br> SubCommand - Class that implements the SubCommand interface
     */
    public static Map<String, SubCommand> getSubCommands() {
        return subCommands;
    }

    /**
     * Get tab completions for a subcommand
     * @param sender Source of the command
     * @param args Passed command arguments
     * @param subCommandName Sub command name
     * @return Tab completions
     */
    public static List<String> getTabCompletionsForSubCommand(CommandSender sender, String subCommandName, String[] args) {
        SubCommand subCommand = subCommands.get(subCommandName.toLowerCase());
        if (subCommand != null) {
            return subCommand.getTabCompletions(sender, args);
        }
        return null;
    }
}
