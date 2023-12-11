package com.jodexindustries.donatecase.api;

import com.jodexindustries.donatecase.api.events.SubCommandRegisteredEvent;
import com.jodexindustries.donatecase.dc.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubCommandManager {
    public static final Map<String, SubCommand> subCommands = new HashMap<>();

    public static void registerSubCommand(String commandName, SubCommand subCommand) {
        if(subCommands.get(commandName.toLowerCase()) == null) {
            subCommands.put(commandName.toLowerCase(), subCommand);
            SubCommandRegisteredEvent subCommandRegisteredEvent = new SubCommandRegisteredEvent(commandName);
            Bukkit.getServer().getPluginManager().callEvent(subCommandRegisteredEvent);
        } else {
            Main.instance.getLogger().warning("Sub command " + commandName + " already registered!");
        }
    }
    public static void unregisterSubCommand(String commandName) {
        if(subCommands.get(commandName.toLowerCase()) != null) {
            subCommands.remove(commandName.toLowerCase());
            SubCommandRegisteredEvent subCommandRegisteredEvent = new SubCommandRegisteredEvent(commandName);
            Bukkit.getServer().getPluginManager().callEvent(subCommandRegisteredEvent);
        } else {
            Main.instance.getLogger().warning("Sub command " + commandName + " already unregistered!");
        }
    }
    public static Map<String, SubCommand> getSubCommands() {
        return subCommands;
    }
    public static List<String> getTabCompletionsForSubCommand(CommandSender sender, String subCommandName, String[] args) {
        SubCommand subCommand = subCommands.get(subCommandName.toLowerCase());
        if (subCommand != null) {
            return subCommand.getTabCompletions(sender, args);
        }
        return null;
    }
}
