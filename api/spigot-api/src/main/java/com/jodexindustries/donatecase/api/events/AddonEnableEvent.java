package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.addon.PowerReason;
import com.jodexindustries.donatecase.api.addon.InternalAddon;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when addon enabled
 */
public class AddonEnableEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final InternalAddon addon;
    private final PowerReason reason;

    /**
     * Default constructor
     *
     * @param addon  Internal DonateCase addon
     */
    public AddonEnableEvent(InternalAddon addon) {
        this.addon = addon;
        this.reason = PowerReason.DONATE_CASE;
    }

    /**
     * Constructor with power reason
     *
     * @param addon  Internal DonateCase addon
     * @param reason Power reason
     */
    public AddonEnableEvent(InternalAddon addon, PowerReason reason) {
        this.addon = addon;
        this.reason = reason;
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
     * Get addon object
     *
     * @return Addon object
     */
    public InternalAddon getAddon() {
        return addon;
    }

    /**
     * Get addon power reason
     *
     * @return Power reason
     */
    public PowerReason getReason() {
        return reason;
    }
}
