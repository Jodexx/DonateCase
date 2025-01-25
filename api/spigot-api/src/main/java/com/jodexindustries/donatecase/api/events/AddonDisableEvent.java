package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.addon.PowerReason;
import com.jodexindustries.donatecase.api.addon.InternalAddon;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when addon disabled
 */
public class AddonDisableEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final InternalAddon addon;
    private final Addon caused;
    private final PowerReason reason;

    /**
     * Default constructor
     *
     * @param addon  Internal DonateCase addon
     * @param caused Which addon caused the disabling
     */
    public AddonDisableEvent(InternalAddon addon, Addon caused) {
        this.addon = addon;
        this.caused = caused;
        this.reason = PowerReason.DONATE_CASE;
    }

    /**
     * Constructor with power reason
     *
     * @param addon  Internal DonateCase addon
     * @param caused Which addon caused the disabling
     * @param reason Power reason
     */
    public AddonDisableEvent(InternalAddon addon, Addon caused, PowerReason reason) {
        this.addon = addon;
        this.caused = caused;
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

    /**
     * Get the addon that caused the disabling
     *
     * @return Caused addon
     */
    public Addon getCaused() {
        return caused;
    }

}
