package com.jodexindustries.donatecase.api.events;

import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the player clicks on the case gui
 */
public class CaseGuiClickEvent extends InventoryClickEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Location location;
    private final String caseType;
    private final boolean isOpenItem;

    /**
     * Default constructor
     * @param view Inventory view
     * @param type Slot type
     * @param slot Slot index
     * @param click Click type
     * @param action Action type
     * @param location Location where opened case
     * @param caseType Case type
     * @param isOpenItem Is OPEN item type
     */
    public CaseGuiClickEvent(@NotNull InventoryView view, @NotNull InventoryType.SlotType type, int slot, @NotNull ClickType click, @NotNull InventoryAction action, @NotNull Location location, String caseType, boolean isOpenItem) {
        super(view, type, slot, click, action);
        this.location = location;
        this.caseType = caseType;
        this.isOpenItem = isOpenItem;
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
    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Get case location
     * @return Case location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Get case type
     * @return case type
     */
    public String getCaseType() {
        return caseType;
    }

    /**
     * Check for "OPEN" item
     * @return result
     */
    public boolean isOpenItem() {
        return isOpenItem;
    }
}
