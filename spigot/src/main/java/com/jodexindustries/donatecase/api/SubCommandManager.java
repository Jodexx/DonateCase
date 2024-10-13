package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.events.SubCommandRegisteredEvent;
import com.jodexindustries.donatecase.api.events.SubCommandUnregisteredEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for managing subcommands, registering commands.
 */
public class SubCommandManager {
    /**
     * Map of all registered subcommands
     */
    public static final Map<String, SubCommand> registeredSubCommands = new HashMap<>();
    private final Addon addon;

    /**
     * Default constructor
     * @param addon An addon that will manage subcommands
     */
    public SubCommandManager(Addon addon) {
        this.addon = addon;
    }

    /**
     * Gets Builder for creating sub command
     * @param name Sub command name to create
     * @see #registerSubCommand(SubCommand)
     * @return SubCommand Builder
     * @since 2.2.4.5
     */
    @NotNull
    public SubCommand.Builder builder(String name) {
        return new SubCommand.Builder(name, addon);
    }

    /**
     * Register sub command
     * @see #builder(String)
     * @param subCommand SubCommand object
     * @return true, if successful
     * @since 2.2.4.5
     */
    public boolean registerSubCommand(SubCommand subCommand) {
        String name = subCommand.getName();
        if (registeredSubCommands.get(name.toLowerCase()) == null) {
            registeredSubCommands.put(name.toLowerCase(), subCommand);
            SubCommandRegisteredEvent subCommandRegisteredEvent = new SubCommandRegisteredEvent(subCommand);
            Bukkit.getServer().getPluginManager().callEvent(subCommandRegisteredEvent);
            return true;
        } else {
            addon.getLogger().warning("Sub command " + name + " already registered!");
        }
        return false;
    }

    /**
     * Unregister sub command
     * @param commandName Sub command name to unregister
     */
    public void unregisterSubCommand(String commandName) {
        if(registeredSubCommands.get(commandName.toLowerCase()) != null) {
            registeredSubCommands.remove(commandName.toLowerCase());
            SubCommandUnregisteredEvent subCommandUnregisteredEvent = new SubCommandUnregisteredEvent(commandName);
            Bukkit.getServer().getPluginManager().callEvent(subCommandUnregisteredEvent);
        } else {
            addon.getLogger().warning("Sub command " + commandName + " already unregistered!");
        }
    }

    /**
     * Unregister all subcommands
     */
    public void unregisterSubCommands() {
        List<String> list = new ArrayList<>(registeredSubCommands.keySet());
        list.forEach(this::unregisterSubCommand);
    }

    /**
     * Get tab completions for a subcommand
     * @param sender Source of the command
     * @param args Passed command arguments
     * @param subCommandName Sub command name
     * @return Tab completions
     */
    public static List<String> getTabCompletionsForSubCommand(CommandSender sender, String subCommandName, String label, String[] args) {
        SubCommand subCommand = registeredSubCommands.get(subCommandName.toLowerCase());
        return subCommand != null ? subCommand.getTabCompletions(sender, label, args) : null;
    }

}
