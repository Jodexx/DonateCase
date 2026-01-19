package com.jodexindustries.donatecase.common.gui.items;

import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.event.player.GuiClickEvent;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItemClickHandler;
import org.jetbrains.annotations.NotNull;

public class CLOSEItemClickHandlerImpl implements TypedItemClickHandler {

    @Override
    public void onClick(@NotNull GuiClickEvent e) {
        CaseGuiWrapper gui = e.guiWrapper();
        gui.getPlayer().closeInventory();
    }
}