package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a new subcommand has registered
 */

public class SubCommandRegisteredEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final SubCommand<CommandSender> subCommand;

    /**
     * Default constructor
     *
     * @param subCommand Sub command class
     */
    public SubCommandRegisteredEvent(SubCommand<CommandSender> subCommand) {
        this.subCommand = subCommand;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get handlers
     *
     * @return handlers list
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Get SubCommand class
     *
     * @return SubCommand
     */
    @NotNull
    public SubCommand<CommandSender> getSubCommand() {
        return subCommand;
    }

    /**
     * Get if this SubCommand is default
     *
     * @return boolean
     */
    public boolean isDefault() {
        return subCommand.getAddon().getName().equals("DonateCase");
    }
}