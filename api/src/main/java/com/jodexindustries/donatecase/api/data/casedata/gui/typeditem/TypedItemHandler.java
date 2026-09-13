package com.jodexindustries.donatecase.api.data.casedata.gui.typeditem;

import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMenu;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for handling item creating
 */
public interface TypedItemHandler {

    CaseMenu.Item handle(@NotNull CaseGuiWrapper caseGuiWrapper, @NotNull CaseMenu.Item item) throws TypedItemException;
}
