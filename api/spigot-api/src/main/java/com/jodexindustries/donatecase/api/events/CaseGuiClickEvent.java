package com.jodexindustries.donatecase.api.events;

import com.jodexindustries.donatecase.api.event.GUIClickEvent;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.tools.BukkitUtils;
import org.bukkit.entity.Player;
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
public class CaseGuiClickEvent extends InventoryClickEvent implements GUIClickEvent {
    private static final HandlerList handlers = new HandlerList();
    private final CaseGuiWrapper gui;
    private final String itemType;
    private boolean cancel;

    /**
     * Default constructor
     *
     * @param view     Inventory view
     * @param type     Slot type
     * @param slot     Slot index
     * @param click    Click type
     * @param action   Action type
     * @param gui      Opened GUI
     * @param itemType GUI item type
     */
    public CaseGuiClickEvent(@NotNull InventoryView view, @NotNull InventoryType.SlotType type,
                             int slot, @NotNull ClickType click, @NotNull InventoryAction action,
                             @NotNull CaseGuiWrapper gui, String itemType) {
        super(view, type, slot, click, action);
        this.gui = gui;
        this.itemType = itemType;
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
    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull DCPlayer getPlayer() {
        return BukkitUtils.fromBukkit((Player) getWhoClicked());
    }

    /**
     * Get opened gui
     *
     * @return gui
     */
    @Override
    public @NotNull CaseGuiWrapper getCaseGUI() {
        return gui;
    }

    /**
     * Get GUI item type
     *
     * @return item type
     */
    @Override
    public @NotNull String getItemType() {
        return itemType;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * Cancel click. If you cancel this event, then GUI will not activate an animation from OPEN item.
     *
     * @param toCancel true or false
     */
    @Override
    public void setCancelled(boolean toCancel) {
        this.cancel = toCancel;
    }
}
