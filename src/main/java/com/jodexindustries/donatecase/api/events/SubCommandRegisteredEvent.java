package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a new subcommand has registered
 */

public class SubCommandRegisteredEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final SubCommand subCommand;

    /**
     * Default constructor
     *
     * @param subCommand Sub command class
     */
    public SubCommandRegisteredEvent(SubCommand subCommand) {
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
     * Get subcommand name
     *
     * @return subcommand name
     * @deprecated Use {@link SubCommand#getName()}
     */
    @Deprecated
    public String getSubCommandName() {
        return subCommand.getName();
    }

    /**
     * Get SubCommand class
     *
     * @return SubCommand
     */
    @NotNull
    public SubCommand getSubCommand() {
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

    /**
     * Get subcommand addon
     *
     * @return Addon object
     * @deprecated Use {@link #getSubCommand()#getAddon()} instead
     */
    @Deprecated
    public Addon getAddon() {
        return subCommand.getAddon();
    }
}
