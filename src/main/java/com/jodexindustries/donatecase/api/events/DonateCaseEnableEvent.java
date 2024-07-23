package com.jodexindustries.donatecase.api.events;

import org.bukkit.event.HandlerList;
import org.bukkit.event.server.PluginEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Called when DonateCase enabled
 */
public class DonateCaseEnableEvent extends PluginEvent {
    private static final HandlerList handlers = new HandlerList();

    /**
     * Default constructor
     * @param plugin DonateCase instance
     */
    public DonateCaseEnableEvent(@NotNull Plugin plugin) {
        super(plugin);
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
}
