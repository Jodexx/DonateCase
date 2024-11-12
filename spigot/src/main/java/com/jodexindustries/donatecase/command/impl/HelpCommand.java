package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.impl.managers.SubCommandManagerImpl;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.command.GlobalCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for /dc help subcommand implementation
 */
public class HelpCommand implements SubCommandExecutor<CommandSender>, SubCommandTabCompleter<CommandSender> {

    public static void register(SubCommandManagerImpl manager) {
        HelpCommand command = new HelpCommand();

        SubCommand subCommand = manager.builder("help")
                .executor(command)
                .tabCompleter(command)
                .permission(SubCommandType.PLAYER.permission)
                .build();
        manager.registerSubCommand(subCommand);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        GlobalCommand.sendHelp(sender, label);
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }

}
