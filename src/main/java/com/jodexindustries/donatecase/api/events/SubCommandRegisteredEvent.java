package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.data.SubCommand;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a new subcommand has registered
 */

public class SubCommandRegisteredEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String subCommandName;
    private final SubCommand subCommand;
    private final String subCommandAddonName;
    private final boolean isDefault;

    /**
     * Default constructor
     * @param subCommandName Sub command name
     * @param subCommand Sub command class
     * @param subCommandAddonName Sub command addon name
     * @param isDefault Is default?
     */
    public SubCommandRegisteredEvent(String subCommandName, SubCommand subCommand,
                                     String subCommandAddonName, boolean isDefault) {
        this.subCommandName = subCommandName;
        this.subCommand = subCommand;
        this.subCommandAddonName = subCommandAddonName;
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
    public String getSubCommandName() {
        return subCommandName;
    }

    /**
     * Get SubCommand class
     * @return SubCommand
     */
    public SubCommand getSubCommand() {
        return subCommand;
    }

    /**
     * Get SubCommand addon name
     * @return addon name
     */
    public String getSubCommandAddonName() {
        return subCommandAddonName;
    }

    /**
     * Get if this SubCommand is default
     * @return boolean
     */
    public boolean isDefault() {
        return isDefault;
    }
}
