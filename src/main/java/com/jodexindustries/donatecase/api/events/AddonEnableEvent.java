package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.addon.internal.InternalAddon;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when addon enabled
 */
public class AddonEnableEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final InternalAddon addon;
    public AddonEnableEvent(InternalAddon addon) {
        this.addon = addon;
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
     * Get addon object
     * @return Addon object
     */
    public InternalAddon getAddon() {
        return addon;
    }
}
