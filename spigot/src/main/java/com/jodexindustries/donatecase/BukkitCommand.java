package com.jodexindustries.donatecase;

import com.jodexindustries.donatecase.api.platform.BukkitCommandSender;
import com.jodexindustries.donatecase.api.platform.BukkitPlayer;
import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.command.GlobalCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BukkitCommand implements CommandExecutor, TabCompleter {

    private final GlobalCommand command;

    public BukkitCommand(BukkitBackend backend) {
        this.command = new GlobalCommand(backend);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] strings) {
        DCCommandSender commandSender;
        if (sender instanceof Player) {
            commandSender = new BukkitPlayer((Player) sender);
        } else {
            commandSender = new BukkitCommandSender(sender);
        }
        return command.execute(commandSender, s, strings);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] strings) {
        DCCommandSender commandSender;
        if (sender instanceof Player) {
            commandSender = new BukkitPlayer((Player) sender);
        } else {
            commandSender = new BukkitCommandSender(sender);
        }
        return command.getTabCompletions(commandSender, s, strings);
    }
}
