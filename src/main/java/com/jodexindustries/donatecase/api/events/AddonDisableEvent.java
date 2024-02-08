package com.jodexindustries.donatecase.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when addon disabled
 */
public class AddonDisableEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String addon;
    public AddonDisableEvent(String addon) {
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

    public String getAddon() {
        return addon;
    }
}
