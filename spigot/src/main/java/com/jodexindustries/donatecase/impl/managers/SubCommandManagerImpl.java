package com.jodexindustries.donatecase.impl.managers;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.events.SubCommandRegisteredEvent;
import com.jodexindustries.donatecase.api.events.SubCommandUnregisteredEvent;
import com.jodexindustries.donatecase.api.manager.SubCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubCommandManagerImpl implements SubCommandManager<CommandSender> {
    private static final Map<String, SubCommand<CommandSender>> registeredSubCommands = new HashMap<>();
    private final Addon addon;

    public SubCommandManagerImpl(Addon addon) {
        this.addon = addon;
    }

    @NotNull
    @Override
    public SubCommand.Builder<CommandSender> builder(@NotNull String name) {
        return new SubCommand.Builder<>(name, addon);
    }

    @Override
    public boolean registerSubCommand(SubCommand<CommandSender> subCommand) {
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

    @Override
    public void unregisterSubCommand(String commandName) {
        if(registeredSubCommands.get(commandName.toLowerCase()) != null) {
            registeredSubCommands.remove(commandName.toLowerCase());
            SubCommandUnregisteredEvent subCommandUnregisteredEvent = new SubCommandUnregisteredEvent(commandName);
            Bukkit.getServer().getPluginManager().callEvent(subCommandUnregisteredEvent);
        } else {
            addon.getLogger().warning("Sub command " + commandName + " already unregistered!");
        }
    }

    @Override
    public void unregisterSubCommands() {
        List<String> list = new ArrayList<>(registeredSubCommands.keySet());
        list.forEach(this::unregisterSubCommand);
    }

    @Override
    public @Nullable SubCommand<CommandSender> getRegisteredSubCommand(String commandName) {
        return registeredSubCommands.get(commandName);
    }

    @Override
    public @NotNull Map<String, SubCommand<CommandSender>> getRegisteredSubCommands() {
        return registeredSubCommands;
    }

    @Override
    public List<String> getTabCompletionsForSubCommand(CommandSender sender, String subCommandName, String label, String[] args) {
        SubCommand<CommandSender> subCommand = registeredSubCommands.get(subCommandName.toLowerCase());
        return subCommand != null ? subCommand.getTabCompletions(sender, label, args) : null;
    }

}
