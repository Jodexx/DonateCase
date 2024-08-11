package com.jodexindustries.donatecase.api.data.gui;

import com.jodexindustries.donatecase.api.data.GUI;
import com.jodexindustries.donatecase.gui.CaseGui;
import org.jetbrains.annotations.NotNull;

/**
 * @since 2.2.4.9
 */
public interface TypedItemHandler {

    /**
     * Called when tried to handle item in GUI <br/>
     * Actually you can manipulate all items in GUI
     * @param caseGui Opened GUI
     * @param item Current item
     * @return Completed GUI.Item
     */
    @NotNull
    GUI.@NotNull Item handle(@NotNull CaseGui caseGui, @NotNull GUI.Item item);
}
