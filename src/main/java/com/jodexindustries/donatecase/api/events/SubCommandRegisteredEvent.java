package com.jodexindustries.donatecase.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a new subcommand has registered
 */

public class SubCommandRegisteredEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String subCommandName;
    public SubCommandRegisteredEvent(String subCommandName) {
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

    public String getSubCommandName() {
        return subCommandName;
    }
}
