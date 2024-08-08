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
    private final Addon addon;
    private final boolean isDefault;

    /**
     * Default constructor
     * @param subCommand Sub command class
     * @param addon Sub command addon
     * @param isDefault Is default?
     */
    public SubCommandRegisteredEvent(SubCommand subCommand,
                                     Addon addon, boolean isDefault) {
        this.subCommand = subCommand;
        this.addon = addon;
        this.isDefault = isDefault;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get handlers
     * @return handlers list
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Get subcommand name
     * @return subcommand name
     */
    @Deprecated
    public String getSubCommandName() {
        return subCommand.getName();
    }

    /**
     * Get SubCommand class
     * @return SubCommand
     */
    public SubCommand getSubCommand() {
        return subCommand;
    }

    /**
     * Get if this SubCommand is default
     * @return boolean
     */
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * Get subcommand addon
     * @return Addon object
     */
    public Addon getAddon() {
        return addon;
    }
}
