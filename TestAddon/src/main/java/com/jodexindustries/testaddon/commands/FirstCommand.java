package com.jodexindustries.testaddon.commands;

import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FirstCommand implements SubCommandExecutor, SubCommandTabCompleter {

    @Override
    public void execute(CommandSender sender, @NotNull String label, String[] args) {
        sender.sendMessage("First command");
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }

}
