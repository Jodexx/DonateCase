package com.jodexindustries.donatecase.api.events;

import org.bukkit.event.HandlerList;
import org.bukkit.event.server.PluginEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Called when DonateCase (configs) reloaded
 */
public class DonateCaseReloadEvent extends PluginEvent {
    private static final HandlerList handlers = new HandlerList();

    private final Type type;

    /**
     * Default constructor
     * @param plugin DonateCase instance
     * @param type Reload type
     */
    public DonateCaseReloadEvent(@NotNull Plugin plugin, Type type) {
        super(plugin);
        this.type = type;
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
     * Get reload Type
     * @return CONFIG or CASES
     * @since 2.2.4.4
     */
    public Type getType() {
        return type;
    }

    /**
     * Enum for reload type
     */
    public enum Type {
        /**
         * Config reloaded
         */
        CONFIG,
        /**
         * Cases reloaded
         */
        CASES
    }
}
