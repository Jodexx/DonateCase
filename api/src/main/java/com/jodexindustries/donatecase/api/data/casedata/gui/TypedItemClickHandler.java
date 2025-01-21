package com.jodexindustries.donatecase.api.data.casedata.gui;

import com.jodexindustries.donatecase.api.event.GUIClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for handling (inventory) click on item event
 */
public interface TypedItemClickHandler {

    /**
     * Called when the player clicks on an item in the case's GUI.
     *
     * @param event called event
     */
    void onClick(@NotNull GUIClickEvent event);
}
