package com.jodexindustries.testaddon.commands;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SecondCommand extends SubCommand {

    public SecondCommand(String name, Addon addon) {
        super(name, addon);

        setType(SubCommandType.PLAYER);
        setDescription("This is a second command");
        setArgs(new String[]{"(test)", "(test2)"});
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        sender.sendMessage("Second command");
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return List.of("1", "2", "3");
    }
}
