package com.jodexindustries.donatecase.api.data.casedata.gui;

import org.jetbrains.annotations.NotNull;

/**
 * Interface for handling (inventory) click on item event
 * @param <E> the type of clicked event
 */
public interface TypedItemClickHandler<E> {

    /**
     * Called when the player clicks on an item in the case's GUI.
     *
     * @param event called event
     */
    void onClick(@NotNull E event);
}
