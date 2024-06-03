package com.jodexindustries.donatecase.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a new subcommand has unregistered
 */

public class SubCommandUnregisteredEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String subCommandName;
    public SubCommandUnregisteredEvent(String subCommandName) {
        this.subCommandName = subCommandName;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

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
}
