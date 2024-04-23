package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.SubCommand;
import com.jodexindustries.donatecase.api.events.SubCommandRegisteredEvent;
import com.jodexindustries.donatecase.tools.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for managing subcommands, registering commands.
 */
public class SubCommandManager {
    public static final Map<String, Pair<SubCommand, Addon>> subCommands = new HashMap<>();
    private final Addon addon;
    public SubCommandManager(Addon addon) {
        this.addon = addon;
    }

    /**
     * Register sub command
     * @param commandName Sub command name to register
     * @param subCommand Class that implements the SubCommand interface
     */
    public void registerSubCommand(String commandName, SubCommand subCommand) {
        if(subCommands.get(commandName.toLowerCase()) == null) {
            subCommands.put(commandName.toLowerCase(), new Pair<>(subCommand, addon));
            SubCommandRegisteredEvent subCommandRegisteredEvent = new SubCommandRegisteredEvent(commandName);
            Bukkit.getServer().getPluginManager().callEvent(subCommandRegisteredEvent);
        } else {
            addon.getDonateCase().getLogger().warning("Sub command " + commandName + " already registered!");
        }
    }

    /**
     * Unregister sub command
     * @param commandName Sub command name to unregister
     */
    public void unregisterSubCommand(String commandName) {
        if(subCommands.get(commandName.toLowerCase()) != null) {
            subCommands.remove(commandName.toLowerCase());
            SubCommandRegisteredEvent subCommandRegisteredEvent = new SubCommandRegisteredEvent(commandName);
            Bukkit.getServer().getPluginManager().callEvent(subCommandRegisteredEvent);
        } else {
            addon.getDonateCase().getLogger().warning("Sub command " + commandName + " already unregistered!");
        }
    }

    /**
     * Get all subcommands
     * @return String - sub command name <br> SubCommand - Class that implements the SubCommand interface
     */
    public Map<String, Pair<SubCommand, Addon>> getSubCommands() {
        return subCommands;
    }

    /**
     * Get tab completions for a subcommand
     * @param sender Source of the command
     * @param args Passed command arguments
     * @param subCommandName Sub command name
     * @return Tab completions
     */
    public List<String> getTabCompletionsForSubCommand(CommandSender sender, String subCommandName, String[] args) {
        SubCommand subCommand = subCommands.get(subCommandName.toLowerCase()).getFirst();
        if (subCommand != null) {
            return subCommand.getTabCompletions(sender, args);
        }
        return null;
    }
}
