package com.jodexindustries.donatecase.api.data.casedata.gui.typeditem;

import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGui;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMenu;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for handling item creating
 */
public interface TypedItemHandler {

    /**
     * Called when tried to handle item in GUI <br/>
     * Actually you can manipulate all items in GUI
     *
     * @param caseGui Opened GUI
     * @param item    Current item
     * @return Completed GUI.Item
     */
    @Deprecated
    @NotNull
    default CaseGui.@NotNull Item handle(@NotNull CaseGuiWrapper caseGui, @NotNull CaseGui.Item item) throws TypedItemException {
        return CaseGui.Item.fromMenu(handle(caseGui, CaseGui.Item.toMenu(item)));
    }

    CaseMenu.Item handle(@NotNull CaseGuiWrapper caseGuiWrapper, @NotNull CaseMenu.Item item) throws TypedItemException;
}
