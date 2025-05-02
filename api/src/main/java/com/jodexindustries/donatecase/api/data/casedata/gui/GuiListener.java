package com.jodexindustries.donatecase.api.data.casedata.gui;

import com.jodexindustries.donatecase.api.event.player.GuiClickEvent;


public class GuiListener {
    public void onGuiClick(GuiClickEvent event) {
        CaseGuiWrapper wrapper = event.guiWrapper();

        String itemType = wrapper.getCaseData().caseGui().getItemTypeBySlot(event.slot());

        if (itemType == null) return;

        switch (itemType) {
            case "PAGINATION_PREV":
                new PaginationHandlers.PrevPageHandler().onClick(event);
                break;
            case "PAGINATION_NEXT":
                new PaginationHandlers.NextPageHandler().onClick(event);
                break;
        }
    }
}