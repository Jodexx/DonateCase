package com.jodexindustries.donatecase.api.data.gui;

import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for handling (inventory) click on item event
 * @since 2.2.4.9
 */
public interface TypedItemClickHandler {

    /**
     * Called when the player clicks on an item in the case's GUI.
     * @param event called event
     */
    void onClick(@NotNull CaseGuiClickEvent event);
}
