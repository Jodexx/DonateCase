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
     * Register sub command
     * @param commandName Sub command name to register
     * @param executor Class that implements the SubCommand interface
     * @return true, if successful
     */
    @Deprecated
    public boolean registerSubCommand(String commandName, com.jodexindustries.donatecase.api.data.SubCommand executor) {
        SubCommand subCommand = new SubCommand(commandName, addon);
        subCommand.setExecutor((sender, label, args) -> executor.execute(sender, args));
        subCommand.setTabCompleter((sender, label, args) -> executor.getTabCompletions(sender, args));
        subCommand.setType(executor.getType());
        subCommand.setArgs(executor.getArgs());
        subCommand.setDescription(executor.getDescription());
        return registerSubCommand(subCommand);
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

            boolean isDefault = addon.getName().equalsIgnoreCase("DonateCase");

            SubCommandRegisteredEvent subCommandRegisteredEvent = new SubCommandRegisteredEvent(subCommand,
                    addon, isDefault);
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
        List<String> list = new ArrayList<>(getSubCommands().keySet());
        list.forEach(this::unregisterSubCommand);
    }

    /**
     * Get all subcommands
     * @return String - sub command name <br> SubCommand - Class that implements the SubCommand interface
     */
    public static Map<String, SubCommand> getSubCommands() {
        return registeredSubCommands;
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
