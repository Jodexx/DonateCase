package com.jodexindustries.donatecase.spigot.api.platform;

import com.jodexindustries.donatecase.api.platform.DCCommandSender;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BukkitCommandSender implements DCCommandSender {

    private final CommandSender sender;

    public BukkitCommandSender(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public @NotNull String getName() {
        return sender.getName();
    }

    @Override
    public @NotNull CommandSender getHandler() {
        return sender;
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public void sendMessage(@NotNull String text) {
        sender.sendMessage(text);
    }
}
