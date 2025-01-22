package com.jodexindustries.donatecase;

import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.tools.BukkitUtils;
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
        return command.execute(get(sender), s, strings);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] strings) {
        return command.getTabCompletions(get(sender), s, strings);
    }

    private static DCCommandSender get(CommandSender sender) {
        return sender instanceof Player ? BukkitUtils.fromBukkit((Player) sender) : BukkitUtils.fromBukkit(sender);
    }
}
